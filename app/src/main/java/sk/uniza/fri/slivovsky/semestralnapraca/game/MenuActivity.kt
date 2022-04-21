package sk.uniza.fri.slivovsky.semestralnapraca.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.LocaleHelper
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityMenuBinding
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity


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
        hideSystemBars()
        val items = listOf(
            getString(R.string.diff),
            getString(R.string.animals),
            getString(R.string.cities),
            getString(R.string.food)
        )

        binding.backButton.setOnClickListener {
            startActivity(Intent(this@MenuActivity, TitleActivity::class.java))
        }
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
            startActivity(createIntent("medium", true, "$docName.txt", docName))

        }
        binding.hardButton.setOnClickListener {
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
            startActivity(createIntent("hard", true, "$docName.txt", docName))
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

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
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
        var maxLevel = 0
        val docRef = db.collection("words" + currUser!!.uid)
            .document(docName)
        //init words
        println(docRef)
        docRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    db.collection("words" + currUser.uid).get().addOnSuccessListener { result ->
                        for (doc in result) {
                            if (doc.id == docName) {
                                level = (doc["level"] as Number).toInt()
                                maxLevel = if((doc["maxLevel"] as Number).toInt() > 0){
                                    (doc["maxLevel"] as Number).toInt()
                                }else{
                                    ((doc["words"]) as MutableList<String>).size
                                }
                            }
                        }
                        if(level > 0) {
                            binding.playButton.text =
                                getString(R.string.play_buttoon) +" " + getString(R.string.levelMenu)+ " "+ level.toString() + "/" + maxLevel
                        }else{
                            binding.playButton.text =
                                getString(R.string.play_buttoon) + " " + getString(R.string.levelMenu) + " 1/" + maxLevel
                        }
                    }
                }else{
                    binding.playButton.text =
                        getString(R.string.play_buttoon) + " " + getString(R.string.levelMenu) + " 1"
                }
            } else {

            }
        }

    }


}