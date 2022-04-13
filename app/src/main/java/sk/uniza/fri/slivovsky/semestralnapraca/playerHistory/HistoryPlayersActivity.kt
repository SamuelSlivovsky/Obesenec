package sk.uniza.fri.slivovsky.semestralnapraca.playerHistory

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityPlayersHistoryBinding
import sk.uniza.fri.slivovsky.semestralnapraca.score.ScoreActivity

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class HistoryPlayersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayersHistoryBinding
    var list = mutableListOf<HistoryPlayerModelClass>()

    @SuppressLint("SetTextI18n")
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
        binding = ActivityPlayersHistoryBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        hideSystemBars()
        val uid = intent.getStringExtra("uid")
        val userName = intent.getStringExtra("userName")
        val db = Firebase.firestore
        db.collection("history$uid")
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    list.add(
                        HistoryPlayerModelClass(
                            (document.data.getValue("score") as Number).toInt(),
                            document.data.getValue("date").toString()
                        )
                    )
                }
                binding.historyRecylcerView.adapter =
                    HistoryPlayersAdapter(this@HistoryPlayersActivity, list)

            }

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@HistoryPlayersActivity, ScoreActivity::class.java))
        }
        binding.playerNameTextView.text ="$userName"
    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

}