package sk.uniza.fri.slivovsky.semestralnapraca.Skore


import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityScoreBinding






class ScoreActivity: AppCompatActivity() {
    private lateinit var binding: ActivityScoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityScoreBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, SkoreFragment()).commit()
    }
}