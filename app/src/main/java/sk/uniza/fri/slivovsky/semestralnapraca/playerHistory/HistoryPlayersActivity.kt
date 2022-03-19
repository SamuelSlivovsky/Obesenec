package sk.uniza.fri.slivovsky.semestralnapraca.playerHistory

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityPlayersHistoryBinding
import sk.uniza.fri.slivovsky.semestralnapraca.score.ScoreActivity

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class HistoryPlayersActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPlayersHistoryBinding
    var list = mutableListOf<HistoryPlayerModelClass>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayersHistoryBinding.inflate(layoutInflater)
        val view= binding.root
        setContentView(view)
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
                binding.historyRecylcerView.adapter = HistoryPlayersAdapter(this@HistoryPlayersActivity, list)

            }

        binding.buttonBackToMenu.setOnClickListener {
            startActivity(Intent(this@HistoryPlayersActivity,ScoreActivity::class.java))
        }
        binding.playerNameTextView.text = "Hráč: $userName"
    }

}