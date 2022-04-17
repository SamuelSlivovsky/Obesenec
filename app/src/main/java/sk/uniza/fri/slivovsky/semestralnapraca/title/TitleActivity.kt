package sk.uniza.fri.slivovsky.semestralnapraca.title

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import sk.uniza.fri.slivovsky.semestralnapraca.LocaleHelper
import sk.uniza.fri.slivovsky.semestralnapraca.playerHistory.*
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityTitleBinding
import java.util.*


class TitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTitleBinding
    @SuppressLint("ApplySharedPref")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val anim = getSharedPreferences("anims", MODE_PRIVATE)
        val anims = intent?.getStringExtra("anims")
        val isAnim: String
            if (anims == null){
                if (anim.getString("anims","").toString() == ""){
                    isAnim = "y"
                }else{
                isAnim = anim.getString("anims","").toString()
                }
            }else{

                isAnim = anims
            }

        val myBackground = intent?.getStringExtra("background")
        val background = getSharedPreferences(
            "background",
            MODE_PRIVATE
        )
        val back: String


        if (myBackground == null) {
            back = background.getString("background", "").toString()
            when (back) {
                "background1" -> setTheme(R.style.Background1)
                "background2" -> setTheme(R.style.Background2)
                "background3" -> setTheme(R.style.Background3)
                "background4" -> setTheme(R.style.Background4)
                "background5" -> setTheme(R.style.Background5)

            }
        } else {
            back = myBackground
            when (back) {
                "background1" -> setTheme(R.style.Background1)
                "background2" -> setTheme(R.style.Background2)
                "background3" -> setTheme(R.style.Background3)
                "background4" -> setTheme(R.style.Background4)
                "background5" -> setTheme(R.style.Background5)

            }

        }

        binding = ActivityTitleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        hideSystemBars()
        LocaleHelper.setLocale(this@TitleActivity, LocaleHelper.getLanguage(this@TitleActivity))
        val editor = background.edit()
        val editor2 = anim.edit()
        editor.putString("background", back)
        editor2.putString("anims", isAnim)
        editor.commit()
        editor2.commit()
        replaceFragment(TitleFragment())
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_1 -> {
                        replaceFragment(TitleFragment())
                        true
                    }
                    R.id.page_2 -> {
                        replaceFragment(PlayerHistoryFragment())
                        true
                    }

                    R.id.page_3 -> {
                        replaceFragment(FeedbackFragment())
                        true
                    }
                    else -> {
                        replaceFragment(SettingsFragment())
                        true
                    }
                }
            }

    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragment_container, fragment).commit()
    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}