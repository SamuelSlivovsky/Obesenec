package sk.uniza.fri.slivovsky.semestralnapraca.Hra


import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sk.uniza.fri.slivovsky.semestralnapraca.Skore.ScoreActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityTitulkaBinding


class TitulkaActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTitulkaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTitulkaBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        binding.playButton.setOnClickListener {
            startActivity(Intent(this@TitulkaActivity,MenuActivity::class.java))
        }
        binding.menuDoSkoreButton.setOnClickListener {
            startActivity(Intent(this@TitulkaActivity,TutorialActivity::class.java))
        }

        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(this@TitulkaActivity,ScoreActivity::class.java))
        }
    }
}