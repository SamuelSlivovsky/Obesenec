package sk.uniza.fri.slivovsky.semestralnapraca.game

import android.annotation.SuppressLint
import android.content.Context
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
import android.content.SharedPreferences


/**
 * Fragment menu kde hrac zada svoje meno a vyberie si obtiaznost
 *
 */
class MenuActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMenuBinding
    private lateinit var docName: String
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
        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root

        setContentView(view)

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
                    docName = when {
                        LocaleHelper.getLanguage(this@MenuActivity) == "sk" -> {
                            "svkAnimals"
                        }
                        LocaleHelper.getLanguage(this@MenuActivity) == "en" -> {
                            "enAnimals"
                        }
                        else -> {
                            "svkAnimals"
                        }
                    }
                    showLevel(docName)
                    hideOrShow(false)
                    binding.playButton.setOnClickListener {
                        startActivity(
                            createIntent(
                                "animals",
                                false,
                                "$docName.txt",
                                docName
                            )
                        )
                    }
                }
                2.toLong() -> {

                    docName = when {
                        LocaleHelper.getLanguage(this@MenuActivity) == "sk" -> {
                            "svkCapitalCities"
                        }
                        LocaleHelper.getLanguage(this@MenuActivity) == "en" -> {
                            "enCapitalCities"
                        }
                        else -> {
                            "svkCapitalCities"
                        }
                    }
                    showLevel(docName)
                    hideOrShow(false)
                    binding.playButton.setOnClickListener {
                        startActivity(
                            createIntent(
                                "cities",
                                false,
                                "$docName.txt",
                                docName
                            )
                        )
                    }
                }
                3.toLong() -> {
                    docName = when {
                        LocaleHelper.getLanguage(this@MenuActivity) == "sk" -> {
                            "svkFood"
                        }
                        LocaleHelper.getLanguage(this@MenuActivity) == "en" -> {
                            "enFood"
                        }
                        else -> {
                            "svkFood"
                        }
                    }
                    showLevel(docName)
                    hideOrShow(false)
                    binding.playButton.setOnClickListener {
                        startActivity(
                            createIntent(
                                "food",
                                false,
                                "$docName.txt",
                                docName
                            )
                        )
                    }
                }

            }
        }

        binding.easyButton.setOnClickListener {

            docName = when {
                LocaleHelper.getLanguage(this@MenuActivity) == "sk" -> {
                    "svkWordsEasy"
                }
                LocaleHelper.getLanguage(this@MenuActivity) == "en" -> {
                    "enWordsEasy"
                }
                else -> {
                    "svkWordsEasy"
                }
            }
            startActivity(createIntent("easy", true, "$docName.txt", docName))
        }
        binding.mediumButon.setOnClickListener {

            docName = when {
                LocaleHelper.getLanguage(this@MenuActivity) == "sk" -> {
                    "svkWordsMedium"
                }
                LocaleHelper.getLanguage(this@MenuActivity) == "en" -> {
                    "enWordsMedium"
                }
                else -> {
                    "svkWordsMedium"
                }
            }
            startActivity(createIntent("easy", true, "$docName.txt", docName))

        }
        binding.hardButton.setOnClickListener {
            /*      if (binding.nameInputText.text.toString().trim().isNotEmpty()) {
                      val profileUpdates = userProfileChangeRequest {
                          displayName = binding.nameInputText.text.toString().trim()
                      }
                      user!!.updateProfile(profileUpdates)
                  }*/
            docName = when {
                LocaleHelper.getLanguage(this@MenuActivity) == "sk" -> {
                    "svkWordsHard"
                }
                LocaleHelper.getLanguage(this@MenuActivity) == "en" -> {
                    "enWordsHard"
                }
                else -> {
                    "svkWordsHard"
                }
            }
            startActivity(createIntent("easy", true, "$docName.txt", docName))
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

    private fun hideOrShow(compet: Boolean) {

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