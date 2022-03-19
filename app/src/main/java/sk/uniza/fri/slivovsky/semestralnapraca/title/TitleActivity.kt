package sk.uniza.fri.slivovsky.semestralnapraca.title


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import sk.uniza.fri.slivovsky.semestralnapraca.LocaleHelper
import sk.uniza.fri.slivovsky.semestralnapraca.playerHistory.*
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityTitleBinding
import java.util.*


class TitleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTitleBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTitleBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        val fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = TitleFragment()
        fragmentTransaction.add(R.id.fragment_container,fragment).commit()
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_1 -> {
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container,fragment).commit()

                        true
                    }
                    R.id.page_2 -> {
                        val fragmentH = PlayerHistoryFragment()
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container,fragmentH).commit()

                        true
                    }

                    R.id.page_3 -> {
                        val fragmentF = FeedbackFragment()
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container,fragmentF).commit()

                        true
                    }
                    else -> {

                        val fragmentS = SettingsFragment()
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container,fragmentS).commit()
                        true
                    }
                }
            }

    }
}