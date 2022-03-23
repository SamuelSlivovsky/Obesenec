package sk.uniza.fri.slivovsky.semestralnapraca.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityEndBinding
import java.text.SimpleDateFormat
import java.util.*

class EndActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityEndBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = getSharedPreferences(
            "background",
            MODE_PRIVATE
        )
        when (background.getString("background", "")) {
            "background1" -> setTheme(R.style.Background1)
            "background2" -> setTheme(R.style.Background2)
            "background3" -> setTheme(R.style.Background3)
            "background4" -> setTheme(R.style.Background4)
            "background5" -> setTheme(R.style.Background5)

        }
        binding = ActivityEndBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = EndFragment()

        val points = intent.getIntExtra("points", 0)
        val bundle = Bundle()
        bundle.putInt("points", points)
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.fragment_container, fragment).commit()
        auth = Firebase.auth
        val currUser = auth.currentUser
        val db = Firebase.firestore
        var bestScore = 0

        if (intent.getStringExtra("type") == "easy" || intent.getStringExtra("type") == "medium" || intent.getStringExtra(
                "type"
            ) == "hard"
        ) {


            db.collection("scoreboard" + intent.getStringExtra("type"))
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.id == currUser!!.uid) {
                            bestScore = (document.data["score"] as Number).toInt()
                        }

                    }

                    if (points > bestScore) {
                        val user = hashMapOf(
                            "name" to currUser!!.displayName,
                            "score" to points,
                            "date" to SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(
                                Calendar.getInstance().time

                            )
                        )

                        db.collection("scoreboard" + intent.getStringExtra("type"))
                            .document(currUser.uid).set(user)
                    }
                }

                .addOnFailureListener {

                }
            val user = hashMapOf(
                "score" to points,
                "date" to SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(Calendar.getInstance().time)
            )

            db.collection("history" + currUser!!.uid).add(user)
        }


    }
}