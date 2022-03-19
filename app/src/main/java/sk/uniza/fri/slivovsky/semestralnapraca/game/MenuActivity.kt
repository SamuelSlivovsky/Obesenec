package sk.uniza.fri.slivovsky.semestralnapraca.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import sk.uniza.fri.slivovsky.semestralnapraca.LocaleHelper
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityMenuBinding
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity
import java.io.File
import java.io.InputStream


/**
 * Fragment menu kde hrac zada svoje meno a vyberie si obtiaznost
 *
 */
class MenuActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        println(LocaleHelper.getLanguage(this@MenuActivity))
        val items = listOf(
            getString(R.string.diff),
            getString(R.string.animals),
            getString(R.string.cities),
            getString(R.string.food)
        )
        val arrayAdapter =
            ArrayAdapter(this@MenuActivity, R.layout.list_item_menu, R.id.menu_item, items)
        binding.menuText.setAdapter(arrayAdapter)
        binding.menuText.setOnItemClickListener { _, _, _, id ->

            when (id) {
                0.toLong() -> {
                    hideOrShow(true)
                }
                1.toLong() -> {
                    showLevel("svkAnimals")
                    hideOrShow(false)
                    binding.playButton.setOnClickListener {
                        startActivity(
                            createIntent(
                                "animals",
                                false,
                                "svkAnimals.txt",
                                "svkAnimals"
                            )
                        )
                    }
                }
                2.toLong() -> {
                    showLevel("svkCapitalCities")
                    hideOrShow(false)
                    binding.playButton.setOnClickListener {
                        startActivity(
                            createIntent(
                                "cities",
                                false,
                                "svkCapitalCities.txt",
                                "svkCapitalCities"
                            )
                        )
                    }
                }
                3.toLong() -> {
                    showLevel("svkFood")
                    hideOrShow(false)
                    binding.playButton.setOnClickListener {
                        startActivity(createIntent("food", false, "svkFood.txt", "svkFood"))
                    }
                }

            }
        }
        println(binding.menuText.text)

        binding.easyButton.setOnClickListener {

            startActivity(createIntent("easy", true, "svkWordsEasy.txt", "svkEz"))
        }
        binding.mediumButon.setOnClickListener {

            intent.putExtra("druh", "medium")
            startActivity(intent)

        }
        binding.hardButton.setOnClickListener {
            /*      if (binding.nameInputText.text.toString().trim().isNotEmpty()) {
                      val profileUpdates = userProfileChangeRequest {
                          displayName = binding.nameInputText.text.toString().trim()
                      }
                      user!!.updateProfile(profileUpdates)
                  }*/
            intent.putExtra("druh", "hard")
            startActivity(intent)

        }

    }

    private fun createIntent(type: String, compet: Boolean, druh: String, docName: String): Intent {

        val intent = Intent(this@MenuActivity, GameActivity::class.java)
        intent.putExtra("type", type)
        intent.putExtra("compet", compet)
        intent.putExtra("druh", druh)
        intent.putExtra("docName", docName)
        return intent
    }

    fun hideOrShow(compet: Boolean) {

        if (compet) {

            binding.infoTextView.visibility = View.VISIBLE
            binding.easyButton.visibility = View.VISIBLE
            binding.mediumButon.visibility = View.VISIBLE
            binding.hardButton.visibility = View.VISIBLE
            binding.playButton.visibility = View.INVISIBLE
        } else {
            binding.playButton.visibility = View.VISIBLE
            binding.infoTextView.visibility = View.INVISIBLE
            binding.easyButton.visibility = View.INVISIBLE
            binding.mediumButon.visibility = View.INVISIBLE
            binding.hardButton.visibility = View.INVISIBLE
        }
    }

    @SuppressLint("SetTextI18n")
    fun showLevel(docName: String) {
        auth = Firebase.auth
        val currUser = auth.currentUser
        val db = Firebase.firestore
        var level = 1
        val docRef = db.collection("words" + currUser!!.uid)
            .document(docName)
        //init words
        docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    db.collection("words" + currUser.uid).get().addOnSuccessListener { result ->
                        for (document in result) {
                            if (document.id == docName) {
                                level = (document["level"] as Number).toInt()
                            }
                        }
                        binding.playButton.text =
                            getString(R.string.play_buttoon) + " LEVEL " + level.toString()

                    }
                } else {


                }
            } else {
                binding.playButton.text =
                    getString(R.string.play_buttoon) + " LEVEL " + level.toString()
            }
        })

    }


}