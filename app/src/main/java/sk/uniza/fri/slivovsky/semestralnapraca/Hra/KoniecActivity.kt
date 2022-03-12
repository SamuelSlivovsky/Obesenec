package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.Skore.SkoreFragment

import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityKoniecBinding
import java.text.SimpleDateFormat
import java.util.*

class KoniecActivity:AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityKoniecBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityKoniecBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragment = KoniecFragment()

        val body = intent.getIntExtra("points",0)
        val bundle = Bundle()
        bundle.putInt("points",body)
        fragment.arguments = bundle
        fragmentTransaction.add(R.id.fragment_container,fragment).commit()
        auth = Firebase.auth
        val currUser = auth.currentUser
        val db = Firebase.firestore
        val user = hashMapOf(
            "name" to currUser!!.displayName,
            "score" to body,
            "date" to SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Calendar.getInstance().time)
        )
        for (i in 1..10){
            db.collection("scoreboard").document(i.toString()).set(user)
        }


    }
}