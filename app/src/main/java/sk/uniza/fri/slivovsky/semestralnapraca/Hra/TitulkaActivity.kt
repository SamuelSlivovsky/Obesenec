package sk.uniza.fri.slivovsky.semestralnapraca.Hra


import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import sk.uniza.fri.slivovsky.semestralnapraca.Historia.FeedbackFragment
import sk.uniza.fri.slivovsky.semestralnapraca.Historia.FragmentHistoriaHraca
import sk.uniza.fri.slivovsky.semestralnapraca.Historia.SettingsFragment
import sk.uniza.fri.slivovsky.semestralnapraca.Historia.TitulkaFragment
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.Skore.ScoreActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityTitulkaBinding


class TitulkaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTitulkaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTitulkaBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

       /* binding.playButton.setOnClickListener {
            startActivity(Intent(this@TitulkaActivity, MenuActivity::class.java))
        }
        binding.menuDoSkoreButton.setOnClickListener {
            startActivity(Intent(this@TitulkaActivity, TutorialActivity::class.java))
        }

        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(this@TitulkaActivity, ScoreActivity::class.java))
        }*/




        val fragmentManager = supportFragmentManager
        var fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = TitulkaFragment()
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
                        val fragmentH = FragmentHistoriaHraca()
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