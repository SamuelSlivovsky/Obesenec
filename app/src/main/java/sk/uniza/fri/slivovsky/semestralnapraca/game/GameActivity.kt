package sk.uniza.fri.slivovsky.semestralnapraca.game

import android.annotation.SuppressLint
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import sk.uniza.fri.slivovsky.semestralnapraca.MainActivity
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityGameBinding
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity
import java.io.File
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.max
import kotlin.properties.Delegates


/**
 * Fragment ktori sa stara o logiku hry
 *
 */
class GameActivity : AppCompatActivity() {

    private var words = mutableListOf<String>()
    private var lastPause: Long = 0
    private lateinit var auth: FirebaseAuth
    private var wordToFind: String? = null
    private var lettersArray: CharArray = charArrayOf()
    private var lost = false
    private var helpingLetter: CharArray = charArrayOf()
    private val usedLetters = ArrayList<String>()
    private var lives = 0
    private val random = Random()
    private var points = 0
    private var powerUpShow = 0
    private var powerUpTime = 0
    private var powerUpLife = 0
    private var pause = false
    private var powerUpOtvorene = false
    private var pocetPowerUpov = 0
    private var nextGame = false
    private lateinit var binding: ActivityGameBinding
    private var gameType = ""
    private var wordsBefore = mutableListOf<String>()
    private var isCompet = false
    private var maxLevel = 0
    private var uhadnute = false
    private var currLevel = 0
    private var anim = ""
    private var repeat = false

    /**
     * Funkcia oncreate ktora je dedena z Fragment classy,
     * zabezpecuje vytvorenie fragmentu
     *
     * @param savedInstanceState
     */

    @SuppressLint("RestrictedApi", "SetTextI18n")
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
        val anims = getSharedPreferences(
            "anims",
            MODE_PRIVATE
        )
        anim = anims.getString("anims", "").toString()
        //binding
        binding = ActivityGameBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        hideSystemBars()
        //init timer
        binding.timerTextView.base = SystemClock.elapsedRealtime() + 100000000000

        //firebase
        val storage = Firebase.storage
        val storageRef = storage.reference
        auth = Firebase.auth
        val currUser = auth.currentUser
        val db = Firebase.firestore
        val docRef = db.collection("words" + currUser!!.uid)
            .document(intent.getStringExtra("docName").toString())

        //check the game type
        gameType = intent.getStringExtra("type").toString()
        isCompet = intent.getBooleanExtra("compet", true)
        //init words
        if (!isCompet) {
            docRef.get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val document = task.result
                    if (document.exists()) {
                        db.collection("words" + currUser.uid).get().addOnSuccessListener { result ->
                            for (doc in result) {
                                if (doc.id == intent.getStringExtra("docName")) {
                                    val list = doc.data
                                    words = (list["words"]) as MutableList<String>
                                    pocetPowerUpov = (list["powerUps"] as? Number)!!.toInt()
                                    powerUpShow = (list["show"] as? Number)!!.toInt()
                                    powerUpTime = (list["time"] as? Number)!!.toInt()
                                    powerUpLife = (list["life"] as? Number)!!.toInt()
                                    howManyPowerUps()
                                    lives = (list["currLife"] as? Number)!!.toInt()
                                    currLevel = (list["level"] as? Number)!!.toInt()
                                    binding.scoreTextView.text =
                                        getString(R.string.level) + list["level"].toString()
                                    maxLevel = (list["maxLevel"] as? Number)!!.toInt()
                                }
                            }

                            newGame()
                        }
                    } else {
                        val word = storageRef.child(intent.getStringExtra("druh").toString())
                        val localFile = File.createTempFile("words", "txt")
                        word.getFile(localFile).addOnSuccessListener {
                            val inputStream: InputStream = File(localFile.path).inputStream()
                            val list = mutableListOf<String>()
                            inputStream.bufferedReader().forEachLine {
                                list.add(it)
                            }
                            binding.scoreTextView.text = getString(R.string.level) + "1"
                            words = list
                            maxLevel = words.size
                            newGame()


                        }.addOnFailureListener {
                            println("haha no file")
                        }
                    }
                }
            }
        } else {
            val word = storageRef.child(intent.getStringExtra("druh").toString())
            val localFile = File.createTempFile("words", "txt")
            word.getFile(localFile).addOnSuccessListener {
                val inputStream: InputStream = File(localFile.path).inputStream()
                val list = mutableListOf<String>()
                inputStream.bufferedReader().forEachLine {
                    list.add(it)
                }
                words = list
                newGame()

            }.addOnFailureListener {
                println("haha no file")
            }
        }

        //init textViews
        binding.timer2TextView.setOnChronometerTickListener {
            if (binding.timer2TextView.text == "00:00") {
                binding.timerTextView.base =
                    binding.timerTextView.base + SystemClock.elapsedRealtime() - lastPause
                binding.timerTextView.start()
                binding.timer2TextView.visibility = View.INVISIBLE
                binding.timerTextView.visibility = View.VISIBLE
                binding.timer2TextView.stop()
            }
        }


        binding.timerTextView.setOnChronometerTickListener {

            if (pause) {
                binding.timerTextView.stop()
            }

            if (binding.timerTextView.text == "00:00") {
                loss()
                lost = true
                binding.timerTextView.stop()
            }
        }
        //  animacia pre powerUp button

        val ukazButon: Animation =
            AnimationUtils.loadAnimation(getActivity(this@GameActivity), R.anim.show_button)
        val schovajButon: Animation = AnimationUtils.loadAnimation(
            getActivity(this@GameActivity),
            R.anim.hide_button
        )
        //listenery pre powerUpButtony

        val powerUpPismeno: FloatingActionButton = view.findViewById(R.id.showPowerUpButton)
        powerUpPismeno.setOnClickListener {
            if (powerUpShow > 0) {
                powerUpShow--
                pocetPowerUpov--
                fillInLetter()
                if (powerUpShow == 0) {
                    binding.powerUpShowTextView.visibility = View.INVISIBLE
                }
            }
            if (pocetPowerUpov == 0) {
                binding.amountPowerUpTextView.visibility = View.INVISIBLE
            }
            howManyPowerUps()
        }

        val powerUpCas: FloatingActionButton = view.findViewById(R.id.powerUpTimeButton)
        powerUpCas.setOnClickListener {
            if (powerUpTime > 0) {
                powerUpTime--
                pocetPowerUpov--
                binding.timer2TextView.visibility = View.VISIBLE
                binding.timerTextView.visibility = View.INVISIBLE
                lastPause = SystemClock.elapsedRealtime()
                binding.timerTextView.stop()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    binding.timer2TextView.isCountDown = true
                }
                binding.timer2TextView.base = SystemClock.elapsedRealtime() + 10000
                binding.timer2TextView.start()

                if (powerUpTime == 0) {
                    binding.powerUpTimeTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.amountPowerUpTextView.visibility = View.INVISIBLE
            }
            howManyPowerUps()
        }


        val powerUplives: FloatingActionButton = view.findViewById(R.id.livesPowerUpButton)
        powerUplives.setOnClickListener {
            if (powerUpLife > 0) {
                powerUpLife--
                pocetPowerUpov--
                this.lives++
                if (powerUpLife == 0) {
                    binding.powerUpLivesTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.amountPowerUpTextView.visibility = View.INVISIBLE
            }
            updateImage()
            howManyPowerUps()
        }


        val powerUpMenu: FloatingActionButton = view.findViewById(R.id.powerUpButton)
        powerUpMenu.setOnClickListener {

            if (powerUpOtvorene) {
                powerUpMenu.startAnimation(schovajButon)
                binding.powerUpLayout.visibility = View.INVISIBLE
                powerUpOtvorene = false
            } else {
                powerUpMenu.startAnimation(ukazButon)
                binding.powerUpLayout.visibility = View.VISIBLE
                powerUpOtvorene = true
            }

        }

        //nastavenie onClickListenerov pre buttony
        setButtons()

        //listener pre button na pokracovanie
        binding.continueButton.setOnClickListener {
            if (words.isNotEmpty()) {
                if (!isCompet) {
                    if (!lost) {
                        var previousPoints = 0
                        db.collection("words" + currUser.uid).get().addOnSuccessListener { result ->
                            for (document in result) {
                                if (document.id == intent.getStringExtra("docName"))
                                    previousPoints =
                                        (document.data.getValue("level") as Number).toInt()
                            }

                            val wordsColl = hashMapOf(
                                "words" to wordsBefore,
                                "level" to 1 + previousPoints,
                                "powerUps" to pocetPowerUpov,
                                "show" to powerUpShow,
                                "time" to powerUpTime,
                                "life" to powerUpLife,
                                "currLife" to lives,
                                "maxLevel" to maxLevel
                            )
                            db.collection("words" + currUser.uid)
                                .document(intent.getStringExtra("docName").toString())
                                .set(wordsColl)
                        }
                        nextGame = true
                        newGame()
                        showAllButtons()
                        binding.pauseLayout.visibility = View.INVISIBLE
                        binding.powerUpButton.isEnabled = true
                    } else {
                        var previousPoints = 0
                        db.collection("words" + currUser.uid).get().addOnSuccessListener { result ->
                            for (document in result) {
                                if (document.id == intent.getStringExtra("docName"))
                                    previousPoints =
                                        (document.data.getValue("level") as Number).toInt()
                            }

                            val wordsColl = hashMapOf(
                                "words" to wordsBefore,
                                "level" to previousPoints,
                                "powerUps" to pocetPowerUpov,
                                "show" to powerUpShow,
                                "time" to powerUpTime,
                                "life" to powerUpLife,
                                "currLife" to 7,
                                "maxLevel" to maxLevel
                            )
                            db.collection("words" + currUser.uid)
                                .document(intent.getStringExtra("docName").toString())
                                .set(wordsColl)
                        }
                        startActivity(intent)
                    }
                }else{
                    nextGame = true
                    newGame()
                    showAllButtons()
                    binding.pauseLayout.visibility = View.INVISIBLE
                    binding.powerUpButton.isEnabled = true
                }
            } else {
                if (!isCompet){
                    loss()
                    binding.spoilWordSlovoText.text =
                        getString(R.string.allWordsFound)
                }
                points += pocetPowerUpov
                updateScore()
            }
        }

        //listener pre button na ukoncenie

        binding.endButton.setOnClickListener {

            if (!isCompet) {
                var previousPoints = 0
                db.collection("words" + currUser.uid).get().addOnSuccessListener { result ->
                    for (document in result) {
                        if (document.id == intent.getStringExtra("docName"))
                            previousPoints = (document.data["level"] as Number).toInt()

                    }
                    if (previousPoints == 0) previousPoints = 1

                    val wordsColl = hashMapOf(
                        "words" to wordsBefore,
                        "level" to if (!lost) 1 + previousPoints else previousPoints,
                        "powerUps" to pocetPowerUpov,
                        "show" to powerUpShow,
                        "time" to powerUpTime,
                        "life" to powerUpLife,
                        "currLife" to lives,
                        "maxLevel" to maxLevel
                    )

                    db.collection("words" + currUser.uid)
                        .document(intent.getStringExtra("docName").toString()).set(wordsColl)
                }
            }

            val intent: Intent = if (!isCompet) {
                Intent(this@GameActivity, TitleActivity::class.java)
            } else {
                Intent(this@GameActivity, EndActivity::class.java)
            }
            intent.putExtra("points", points)
            intent.putExtra("type", gameType)
            startActivity(intent)
        }

        binding.searchButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_WEB_SEARCH)
            val term = wordToFind
            intent.putExtra(SearchManager.QUERY, term)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()
        binding.timerTextView.stop()
        lastPause = SystemClock.elapsedRealtime()
    }

    override fun onResume() {
        super.onResume()
        binding.timerTextView.base =
            binding.timerTextView.base + SystemClock.elapsedRealtime() - lastPause
        binding.timerTextView.start()
    }

    private fun hideSystemBars() {
        val windowInsetsController =
            ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@GameActivity, TitleActivity::class.java))
    }

    /**
     * Funkcia pre update resp. zvysenie sk??re
     */
    @SuppressLint("SetTextI18n")
    fun updateScore() {
        currLevel++
        if (isCompet) {
            binding.scoreTextView.text = getString(R.string.gameScore) + points
        } else {
            binding.scoreTextView.text = "Level: " + currLevel
        }

    }

    /**
     * Funkcia pre vybranie nahodne words zo zoznamu, pouzite slovo sa zo zoznamu vyhodi
     * @return slovo vrati vybrane slovo
     */

    private fun wordToFind(): String {
        val word: String
        if (!intent.getBooleanExtra("compet", true)) {
            word = words[0]
            words.removeAt(0)
        } else {
            val i = random.nextInt(words.size)
            word = words[i]
            words.removeAt(i)
        }
        return word
    }

    /**
     *update obrazku obesenca vzhladom na pocet zivotov
     *
     */

    @SuppressLint("RestrictedApi", "SetTextI18n")
    private fun updateImage() {
        if (anim == "y") {
            if (lives >= 7) {
                binding.hangmanImageView.setImageResource(R.drawable.hangman0)
            } else {
                val resImg = resources.getIdentifier(
                    "hangman${7 - lives}", "drawable",
                    getActivity(this@GameActivity)?.packageName
                )
                binding.hangmanImageView.setImageResource(resImg)
            }
            binding.livesTextView.text = "" + lives
        } else {
            if (lives >= 7) {
                binding.hangmanImageView.setImageResource(R.drawable.hangman0)
            } else {
                val resImg = resources.getIdentifier(
                    "hangman_n${7 - lives}", "drawable",
                    getActivity(this@GameActivity)?.packageName
                )
                binding.hangmanImageView.setImageResource(resImg)
            }
            binding.livesTextView.text = "" + lives
        }

    }

    /**
     * funkcia pre start novej hry, inicializuje atributy na startovne hodnoty
     */

    private fun newGame() {

        wordsBefore = ArrayList(words)
        if (!nextGame) {
            if (lives == 0) {
                this.lives = 7
            }
            points = 0
        }
        binding.livesTextView.text = lives.toString()
        uhadnute = false
        usedLetters.clear()
        if (!repeat) {
            wordToFind = wordToFind()
            repeat = false
        }
        println(wordToFind)
        lettersArray = CharArray(wordToFind!!.length)
        for (i in lettersArray.indices) {
            lettersArray[i] = '_'
        }
        binding.timer2TextView.visibility = View.INVISIBLE


        val cas = when (intent.getStringExtra("type")) {
            "easy" -> 180000
            "medium" -> 120000
            "hard" -> 60000
            else -> 180000
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.timerTextView.isCountDown = true
        }
        binding.timerTextView.visibility = View.VISIBLE
        binding.timerTextView.base = SystemClock.elapsedRealtime() + cas
        binding.timerTextView.start()

        binding.wordToFindSlovoText.text = textInit()
        binding.spoilWordSlovoText.visibility = View.GONE
        binding.spoilWordSlovoText.text = ""
        submit(" ")

    }

    /**
     * funkcia pre inicializaciu textu resp. nacitanie jednotlivych pismen do char array
     *
     * @return vrati zoznam pismen
     */
    private fun textInit(): String {
        val builder = StringBuilder()
        for (i in lettersArray.indices) {
            builder.append(lettersArray[i])
            if (i < lettersArray.size - 1) {
                builder.append(" ")
            }
        }
        return builder.toString()
    }

    /**
     * funkcia pre doplnenie pismena pomocou powerUpu
     *
     */
    private fun fillInLetter() {
        //nacitam hladane slovo do charArray
        helpingLetter = wordToFind!!.toCharArray()

        val index = random.nextInt(helpingLetter.size)
        val pismeno = helpingLetter[index]
        //podmienka ktora pozera ci sa nevratila medzera alebo pismeno ktore uz bolo najdene
        if (pismeno == ' ' || usedLetters.contains(pismeno.toString())) {
            fillInLetter()
        }
        //zavola metodu submit
        if (!uhadnute)
            submit(pismeno.toString())
    }

    /**
     *  funkcia pre porovnanie ci sa zadane pismeno nachadza v hladanom slove
     *  stara sa aj o priebeh hry, kontroluje pocet zivotov
     *  @param p - pismeno zadane pomocou buttonu
     */

    private fun submit(p: String) {

        //schovam button
        hideButton(p, true)
        //podmienka ktora pozera ci sa pismeno nachadza v slove
        if (wordToFind!!.contains(p)) {
            var index = wordToFind!!.indexOf(p)
            while (index >= 0) {
                lettersArray[index] = p[0]
                index = wordToFind!!.indexOf(p, index + 1)
            }
            binding.wordToFindSlovoText.text = lettersArray.joinToString(" ")
            //pokial sa pismeno nenachadza v slove a nie je to medzera tak uberie zivot
        } else if (p != " ") {
            this.lives--
            updateImage()
        }
        //prida pismeno do zoznamu pouzitych pismen
        usedLetters.add(p)

        //podmienka ak bolo slovo uhadnute, prida points a zastavi hru, hrac sa rozhodne ci nextGamee alebo konci
        if (found()) {
            uhadnute = true
            wordsBefore = ArrayList(words)
            points++
            updateScore()
            powerUp()
            hideButton("", false)
            hideAllButtonns()
            binding.timerTextView.stop()
            binding.timer2TextView.stop()
            binding.pauseLayout.visibility = View.VISIBLE
            binding.powerUpButton.isEnabled = false
        }

        //podmienka pre prehru
        if (this.lives == 0) {
            loss()
            lost = true
            binding.timerTextView.stop()
            hideAllButtonns()
        }
    }

    /**
     * funkcia pre powerupy, vyberie nahodny z 3 powerupov
     */
    private fun powerUp() {
        val hodnota = when (intent.getStringExtra("type")) {
            "easy" -> 1
            "medium" -> 2
            "hard" -> 3
            else -> 1
        }
        if (points % hodnota == 0) {
            when (Random().nextInt(100)) {
                in 0..32 -> powerUpShow++
                in 33..65 -> powerUpTime++
                in 66..99 -> powerUpLife++
            }
            binding.showPowerUpButton.isClickable = true
        }


        pocetPowerUpov = powerUpShow + powerUpTime + powerUpLife
        howManyPowerUps()
    }

    /**
     * jednoducha boolean funkcia pre rozhodnutie ci hrac nasiel slovo
     */
    private fun found(): Boolean {

        return wordToFind?.contentEquals(String(lettersArray)) == true
    }

    @SuppressLint("SetTextI18n")
    fun howManyPowerUps() {
        binding.amountPowerUpTextView.text = pocetPowerUpov.toString()
        binding.powerUpLivesTextView.text = powerUpLife.toString()
        binding.powerUpTimeTextView.text = powerUpTime.toString()
        binding.powerUpShowTextView.text = powerUpShow.toString()
        //podmienky pre zobrazenie textu u buttonov, text ukazuje kolko ma hrac jednotlivych pouziti daneho powerUpu
        if (powerUpShow > 0) {
            binding.powerUpShowTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpShowTextView.visibility = View.INVISIBLE
        }

        if (powerUpTime > 0) {
            binding.powerUpTimeTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpTimeTextView.visibility = View.INVISIBLE
        }

        if (powerUpLife > 0) {
            binding.powerUpLivesTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpLivesTextView.visibility = View.INVISIBLE
        }

        if (pocetPowerUpov > 0) {
            binding.amountPowerUpTextView.visibility = View.VISIBLE

        } else {
            binding.amountPowerUpTextView.visibility = View.INVISIBLE
        }
    }

    /**
     * funkcia pre nastavenie listenerov vsetkych zadavacich buttonov
     */
    private fun setButtons() {
        binding.submitAbutton.setOnClickListener {
            if (wordToFind!!.contains("A")) {
                submit("A")
            }
            if (wordToFind!!.contains("??")) {
                submit("??")
            }
            if (wordToFind!!.contains("??")) {
                submit("??")
            }

            if (!wordToFind!!.contains("A") && !wordToFind!!.contains("??") && !wordToFind!!.contains(
                    "??"
                )
            ) {
                submit("A")
            }
        }
        binding.submitBbutton.setOnClickListener { submit("B") }
        binding.submitCbutton.setOnClickListener {

            if (wordToFind!!.contains("C")) {
                submit("C")

            }

            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("??") && !wordToFind!!.contains("C")) {
                submit("C")
            }
        }
        binding.submitDbutton.setOnClickListener {

            if (wordToFind!!.contains("D")) {
                submit("D")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("D") && !wordToFind!!.contains("??")) {
                submit("D")
            }
        }
        binding.submitEbutton.setOnClickListener {

            if (wordToFind!!.contains("E")) {
                submit("E")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("E") && !wordToFind!!.contains("??")) {
                submit("E")
            }
        }
        binding.submitFbutton.setOnClickListener { submit("F") }
        binding.submitGbutton.setOnClickListener { submit("G") }
        binding.submitHbutton.setOnClickListener { submit("H") }
        binding.submitIbutton.setOnClickListener {

            if (wordToFind!!.contains("I")) {
                submit("I")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("I") && !wordToFind!!.contains("??")) {
                submit("I")
            }
        }
        binding.submitJbutton.setOnClickListener { submit("J") }
        binding.submitKbutton.setOnClickListener { submit("K") }
        binding.submitLbutton.setOnClickListener {

            if (wordToFind!!.contains("L")) {
                submit("L")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            } else if (wordToFind!!.contains("??")) {
                submit("??")

            }

            if (!wordToFind!!.contains("L") && !wordToFind!!.contains("??") && !wordToFind!!.contains(
                    "??"
                )
            ) {
                submit("L")
            }
        }

        binding.submitMbutton.setOnClickListener { submit("M") }
        binding.submitNbutton.setOnClickListener {

            if (wordToFind!!.contains("N")) {
                submit("N")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }

            if (!wordToFind!!.contains("N") && !wordToFind!!.contains("??")) {
                submit("N")
            }
        }
        binding.submitObutton.setOnClickListener {

            if (wordToFind!!.contains("O")) {
                submit("O")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            } else if (wordToFind!!.contains("??")) {
                submit("??")

            }

            if (!wordToFind!!.contains("O") && !wordToFind!!.contains("??") && !wordToFind!!.contains(
                    "??"
                )
            ) {
                submit("O")
            }
        }
        binding.submitPbutton.setOnClickListener { submit("P") }
        binding.submitQbutton.setOnClickListener { submit("Q") }
        binding.submitRbutton.setOnClickListener { submit("R") }
        binding.submitSbutton.setOnClickListener {

            if (wordToFind!!.contains("S")) {
                submit("S")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("S") && !wordToFind!!.contains("??")) {
                submit("S")
            }
        }
        binding.submitTbutton.setOnClickListener {
            if (wordToFind!!.contains("T")) {
                submit("T")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("T") && !wordToFind!!.contains("??")) {
                submit("T")
            }
        }
        binding.submitUbutton.setOnClickListener {
            if (wordToFind!!.contains("U")) {
                submit("U")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }
            if (!wordToFind!!.contains("??") && !wordToFind!!.contains("U")) {
                submit("U")
            }

        }
        binding.submitVbutton.setOnClickListener { submit("V") }
        binding.submitWbutton.setOnClickListener { submit("W") }
        binding.submitXbutton.setOnClickListener { submit("X") }
        binding.submitYbutton.setOnClickListener {
            if (wordToFind!!.contains("Y")) {
                submit("Y")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }

            if (!wordToFind!!.contains("Y") && !wordToFind!!.contains("??")) {
                submit("Y")
            }
        }
        binding.submitZbutton.setOnClickListener {
            if (wordToFind!!.contains("Z")) {
                submit("Z")

            }
            if (wordToFind!!.contains("??")) {
                submit("??")

            }

            if (!wordToFind!!.contains("Z") && !wordToFind!!.contains("??")) {
                submit("Z")
            }
        }
    }

    /**
     * funkcia pre schovanie jedlotlivych buttonov podla zadaneho pismena
     * @param p
     * @param hide
     */
    private fun hideButton(p: String, hide: Boolean) {
        if (hide) {
            when (p) {
                "A", "??" -> binding.submitAbutton.visibility = View.INVISIBLE
                "B" -> binding.submitBbutton.visibility = View.INVISIBLE
                "C", "??" -> binding.submitCbutton.visibility = View.INVISIBLE
                "D", "??" -> binding.submitDbutton.visibility = View.INVISIBLE
                "E", "??" -> binding.submitEbutton.visibility = View.INVISIBLE
                "F" -> binding.submitFbutton.visibility = View.INVISIBLE
                "G" -> binding.submitGbutton.visibility = View.INVISIBLE
                "H" -> binding.submitHbutton.visibility = View.INVISIBLE
                "I", "??" -> binding.submitIbutton.visibility = View.INVISIBLE
                "J" -> binding.submitJbutton.visibility = View.INVISIBLE
                "K" -> binding.submitKbutton.visibility = View.INVISIBLE
                "L", "??" -> binding.submitLbutton.visibility = View.INVISIBLE
                "M" -> binding.submitMbutton.visibility = View.INVISIBLE
                "N", "??" -> binding.submitNbutton.visibility = View.INVISIBLE
                "O", "??" -> binding.submitObutton.visibility = View.INVISIBLE
                "P" -> binding.submitPbutton.visibility = View.INVISIBLE
                "Q" -> binding.submitQbutton.visibility = View.INVISIBLE
                "R" -> binding.submitRbutton.visibility = View.INVISIBLE
                "S", "??" -> binding.submitSbutton.visibility = View.INVISIBLE
                "T", "??" -> binding.submitTbutton.visibility = View.INVISIBLE
                "U", "??" -> binding.submitUbutton.visibility = View.INVISIBLE
                "V" -> binding.submitVbutton.visibility = View.INVISIBLE
                "W" -> binding.submitWbutton.visibility = View.INVISIBLE
                "X" -> binding.submitXbutton.visibility = View.INVISIBLE
                "Y", "??" -> binding.submitYbutton.visibility = View.INVISIBLE
                "Z", "??" -> binding.submitZbutton.visibility = View.INVISIBLE

            }
        } else {
            binding.submitAbutton.visibility = View.VISIBLE
            binding.submitBbutton.visibility = View.VISIBLE
            binding.submitCbutton.visibility = View.VISIBLE
            binding.submitDbutton.visibility = View.VISIBLE
            binding.submitEbutton.visibility = View.VISIBLE
            binding.submitFbutton.visibility = View.VISIBLE
            binding.submitGbutton.visibility = View.VISIBLE
            binding.submitHbutton.visibility = View.VISIBLE
            binding.submitIbutton.visibility = View.VISIBLE
            binding.submitJbutton.visibility = View.VISIBLE
            binding.submitKbutton.visibility = View.VISIBLE
            binding.submitLbutton.visibility = View.VISIBLE
            binding.submitMbutton.visibility = View.VISIBLE
            binding.submitNbutton.visibility = View.VISIBLE
            binding.submitObutton.visibility = View.VISIBLE
            binding.submitPbutton.visibility = View.VISIBLE
            binding.submitQbutton.visibility = View.VISIBLE
            binding.submitRbutton.visibility = View.VISIBLE
            binding.submitSbutton.visibility = View.VISIBLE
            binding.submitTbutton.visibility = View.VISIBLE
            binding.submitUbutton.visibility = View.VISIBLE
            binding.submitVbutton.visibility = View.VISIBLE
            binding.submitWbutton.visibility = View.VISIBLE
            binding.submitXbutton.visibility = View.VISIBLE
            binding.submitYbutton.visibility = View.VISIBLE
            binding.submitZbutton.visibility = View.VISIBLE
        }
    }

    /**
     * funkcia ak hrac loss
     */
    @SuppressLint("SetTextI18n")
    fun loss() {
        words = wordsBefore
        binding.spoilWordSlovoText.visibility = View.VISIBLE
        binding.wordToFindSlovoText.visibility = View.INVISIBLE
        binding.pauseLayout.visibility = View.VISIBLE
        if (isCompet) {
            binding.spoilWordSlovoText.text = getString(R.string.competLoss) + " " + wordToFind
            binding.searchButton.visibility = View.VISIBLE
            binding.continueButton.visibility = View.INVISIBLE

        } else {
            binding.spoilWordSlovoText.text = getString(R.string.nonCompetLoss)
            binding.searchButton.visibility = View.INVISIBLE
            binding.continueButton.text = getString(R.string.playAgain)
            binding.continueButton.visibility = View.VISIBLE
        }
        hideAllButtonns()
        binding.powerUpButton.isEnabled = false
        binding.powerUpLayout.visibility = View.INVISIBLE

    }

    /**
     * funkcia pre schovanie layoutu so vsetkymi buttonami pismen
     */
    private fun hideAllButtonns() {
        binding.buttonsLayout.visibility = View.INVISIBLE

    }

    /**
     * funkcia pre odkrytie layoutu so vsetkymi buttonami pismen
     */
    private fun showAllButtons() {
        binding.buttonsLayout.visibility = View.VISIBLE
    }
}