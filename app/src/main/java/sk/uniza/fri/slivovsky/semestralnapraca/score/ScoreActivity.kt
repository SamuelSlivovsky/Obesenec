package sk.uniza.fri.slivovsky.semestralnapraca.score


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityScoreBinding

class ScoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = getSharedPreferences(
            "background",
            MODE_PRIVATE
        )
        when (background.getString("background", "defaultvalue")) {
            "background1" -> setTheme(R.style.Background1)
            "background2" -> setTheme(R.style.Background2)
            "background3" -> setTheme(R.style.Background3)
            "background4" -> setTheme(R.style.Background4)
            "background5" -> setTheme(R.style.Background5)

        }
        binding = ActivityScoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        replaceFragment(ScoreboardEasyFragment())
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_1 -> {
                        replaceFragment(ScoreboardEasyFragment())
                        true
                    }
                    R.id.page_2 -> {
                        replaceFragment(ScoreboardMediumFragment())
                        true
                    }

                    else -> {
                        replaceFragment(ScoreboardHardFragment())
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
}