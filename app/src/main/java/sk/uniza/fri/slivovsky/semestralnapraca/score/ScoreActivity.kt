package sk.uniza.fri.slivovsky.semestralnapraca.score


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityScoreBinding
import sk.uniza.fri.slivovsky.semestralnapraca.playerHistory.PlayerHistoryFragment
import sk.uniza.fri.slivovsky.semestralnapraca.title.FeedbackFragment
import sk.uniza.fri.slivovsky.semestralnapraca.title.SettingsFragment
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleFragment


class ScoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = ScoreboardEasyFragment()
        fragmentTransaction.add(R.id.fragment_container, fragment).commit()
        findViewById<BottomNavigationView>(R.id.bottom_navigation)
            .setOnNavigationItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.page_1 -> {
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, fragment).commit()

                        true
                    }
                    R.id.page_2 -> {
                        val fragmentM = ScoreboardMediumFragment()
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, fragmentM).commit()

                        true
                    }

                    else -> {
                        val fragmentH = ScoreboardHardFragment()
                        fragmentTransaction = fragmentManager.beginTransaction()
                        fragmentTransaction.replace(R.id.fragment_container, fragmentH).commit()

                        true
                    }
                }
            }
    }
}