package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityHraBinding
import java.io.File
import java.io.InputStream
import java.util.*
import com.google.firebase.firestore.DocumentSnapshot

import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity

import com.google.android.gms.tasks.OnCompleteListener


/**
 * Fragment ktori sa stara o logiku hry
 *
 */
class HraActivity : AppCompatActivity() {

    private var words = mutableListOf<String>()
    private lateinit var auth: FirebaseAuth
    private var wordToFind: String? = null
    private var lettersArray: CharArray = charArrayOf()
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
    private lateinit var wordsViewModel: SlovaViewModel
    private var nextGame = false
    private lateinit var binding: ActivityHraBinding
    private var cas = 0

    /**
     * Funkcia oncreate ktora je dedena z Fragment classy,
     * zabezpecuje vytvorenie fragmentu
     *
     * @param savedInstanceState
     */

    @SuppressLint("RestrictedApi", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = Firebase.storage
        binding = ActivityHraBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.casovacTextView.base = SystemClock.elapsedRealtime() + 100000000000
        wordsViewModel = ViewModelProviders.of(this).get(SlovaViewModel::class.java)

        val storageRef = storage.reference
        auth = Firebase.auth
        val currUser = auth.currentUser
        val db = Firebase.firestore

        val docRef = db.collection("words" + currUser!!.uid)
            .document(intent.getStringExtra("docName").toString())

        docRef.get().addOnCompleteListener(OnCompleteListener<DocumentSnapshot?> { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document.exists()) {
                    db.collection("words" + currUser!!.uid).get().addOnSuccessListener { result ->
                        for (document in result) {
                            val list = document.data

                            words = list["words"] as MutableList<String>

                        }

                        newGame(words)
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
                        newGame(words)


                    }.addOnFailureListener {
                        println("haha no file")
                    }
                }
            } else {

            }
        })


        binding.casovac2TextView.setOnChronometerTickListener {
            if (binding.casovac2TextView.text == "00:00") {
                binding.casovacTextView.start()
                binding.casovac2TextView.visibility = View.INVISIBLE
                binding.casovacTextView.visibility = View.VISIBLE
                binding.casovac2TextView.stop()
            }
        }


        binding.casovacTextView.setOnChronometerTickListener {

            if (pause) {
                binding.casovacTextView.stop()
            }

            if (binding.casovacTextView.text == "00:00") {
                loss()
                binding.casovacTextView.stop()
            }
        }
        //  animacia pre powerUp button

        val ukazButon: Animation =
            AnimationUtils.loadAnimation(getActivity(this@HraActivity), R.anim.show_button)
        val schovajButon: Animation = AnimationUtils.loadAnimation(
            getActivity(this@HraActivity),
            R.anim.hide_button
        )


        //listenery pre powerUpButtony

        val powerUpPismeno: FloatingActionButton = view.findViewById(R.id.pismenoPowerUpButton)
        powerUpPismeno.setOnClickListener {
            if (powerUpShow > 0) {
                powerUpShow--
                pocetPowerUpov--
                fillInLetter()
                if (powerUpShow == 0) {
                    binding.powerUpPismenoTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.pocetPowerUpTextView.visibility = View.INVISIBLE
            }
            howManyPowerUps()
        }

        val powerUpCas: FloatingActionButton = view.findViewById(R.id.powerUpCasButton)
        powerUpCas.setOnClickListener {
            if (powerUpTime > 0) {
                powerUpTime--
                pocetPowerUpov--
                binding.casovac2TextView.visibility = View.VISIBLE
                binding.casovacTextView.visibility = View.INVISIBLE
                binding.casovacTextView.stop()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    binding.casovac2TextView.isCountDown = true
                }
                binding.casovac2TextView.base = SystemClock.elapsedRealtime() + 10000
                binding.casovac2TextView.start()

                if (powerUpTime == 0) {
                    binding.powerUpCasTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.pocetPowerUpTextView.visibility = View.INVISIBLE
            }
            howManyPowerUps()
        }


        val powerUplives: FloatingActionButton = view.findViewById(R.id.zivotyPowerUpButton)
        powerUplives.setOnClickListener {
            if (powerUpLife > 0) {
                powerUpLife--
                pocetPowerUpov--
                this.lives++
                if (powerUpLife == 0) {
                    binding.powerUpZivotyTextView2.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.pocetPowerUpTextView.visibility = View.INVISIBLE
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
        nastavButtony()

        //listener pre button na pokracovanie
        val pokracovat: Button = view.findViewById(R.id.pokracovatButton)
        pokracovat.setOnClickListener {

            if (words.isNotEmpty()) {
                val wordsColl = hashMapOf(
                    "words" to words
                )
                db.collection("words" + currUser!!.uid).document(intent.getStringExtra("docName").toString()).set(wordsColl)
                nextGame = true
                newGame(words)
                showAllButtons()
                binding.pauzaLayout.visibility = View.INVISIBLE
                binding.powerUpButton.isEnabled = true
            } else {
                loss()
                binding.prezradeneSlovoText.text =
                    "Gratulujem, uhádol si všetky slová. Za uhádnutie všetkých slov dostaneš " + pocetPowerUpov + " bonusových bodov"
                points += pocetPowerUpov
                updateScore()
            }
        }

        //listener pre button na ukoncenie

        binding.koniecButton.setOnClickListener {
            val wordsColl = hashMapOf(
                "words" to words
            )
            db.collection("words" + currUser!!.uid).document(intent.getStringExtra("docName").toString()).set(wordsColl)
            val intent = Intent(this@HraActivity, KoniecActivity::class.java)
            intent.putExtra("points", points)
            startActivity(intent)
        }

    }

    override fun onPause() {
        super.onPause()
        binding.casovacTextView.stop()
        pause = true
    }

    override fun onResume() {
        super.onResume()
        binding.casovacTextView.stop()
        pause = false
        if (!pause) {
            binding.casovacTextView.start()
        }
    }


    /**
     * Funkcia pre update resp. zvysenie skóre
     */
    @SuppressLint("SetTextI18n")
    fun updateScore() {
        binding.skoreTextView.text = "Skóre: " + points
    }

    /**
     * Funkcia pre vybranie nahodne words zo zoznamu, pouzite slovo sa zo zoznamu vyhodi
     * @return slovo vrati vybrane slovo
     */

    fun wordToFind(words: MutableList<String>): String {
        val cislo = random.nextInt(words.size)
        val slovo = words[cislo]
        words.removeAt(cislo)
        return slovo
    }

    /**
     *update obrazku obesenca vzhladom na pocet zivotov
     *
     */

    @SuppressLint("RestrictedApi", "SetTextI18n")
    private fun updateImage() {
        if (lives >= 6) {

            binding.ObesenecObr.setImageResource(R.drawable.obesenec_6)
        } else {
            val resImg = resources.getIdentifier(
                "obesenec_$lives", "drawable",
                getActivity(this@HraActivity)?.packageName
            )
            binding.ObesenecObr.setImageResource(resImg)
        }
        binding.zivotyTextView.text = "" + lives
    }

    /**
     * funkcia pre start novej hry, inicializuje atributy na startovne hodnoty
     * @param words Mutable list ktory je naplneny wordsmi
     */

    fun newGame(words: MutableList<String>) {

        if (!nextGame) {
            this.lives = 6
            points = 0
        }
        usedLetters.clear()
        wordToFind = wordToFind(words)
        println(wordToFind)
        lettersArray = CharArray(wordToFind!!.length)
        for (i in lettersArray.indices) {
            lettersArray[i] = '_'
        }
        binding.casovac2TextView.visibility = View.INVISIBLE

        var cas = 60000
        when (intent.getStringExtra("druh")) {
            "lahke" -> cas = 60000
            "medium" -> cas = 40000
            "tazke" -> cas = 20000
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.casovacTextView.isCountDown = true
        }
        binding.casovacTextView.visibility = View.VISIBLE
        binding.casovacTextView.base = SystemClock.elapsedRealtime() + cas
        binding.casovacTextView.start()

        binding.hladaneSlovoText.text = textInit()
        binding.prezradeneSlovoText.visibility = View.GONE
        binding.prezradeneSlovoText.text = ""
        submit(" ")

    }

    /**
     * funkcia pre inicializaciu textu resp. nacitanie jednotlivych pismen do char array
     *
     * @return vrati zoznam pismen
     */
    fun textInit(): String {
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
    fun fillInLetter() {
        //nacitam hladane slovo do charArray
        helpingLetter = wordToFind!!.toCharArray()

        val index = random.nextInt(helpingLetter.size)
        val pismeno = helpingLetter[index]
        //podmienka ktora pozera ci sa nevratila medzera alebo pismeno ktore uz bolo najdene
        if (pismeno == ' ' || usedLetters.contains(pismeno)) {
            fillInLetter()
        }
        //zavola metodu submit
        submit(pismeno.toString())
    }

    /**
     *  funkcia pre porovnanie ci sa zadane pismeno nachadza v hladanom slove
     *  stara sa aj o priebeh hry, kontroluje pocet zivotov
     *  @param p - pismeno zadane pomocou buttonu
     */

    fun submit(p: String) {

        //schovam button
        hideButtons(p, true)
        //podmienka ktora pozera ci sa pismeno nachadza v slove
        if (wordToFind!!.contains(p)) {
            var index = wordToFind!!.indexOf(p)
            while (index >= 0) {
                lettersArray[index] = p[0]
                index = wordToFind!!.indexOf(p, index + 1)
            }
            binding.hladaneSlovoText.text = lettersArray.joinToString(" ")
            //pokial sa pismeno nenachadza v slove a nie je to medzera tak uberie zivot
        } else if (p != " ") {
            this.lives--
            updateImage()
        }
        //prida pismeno do zoznamu pouzitych pismen
        usedLetters.add(p)

        //podmienka ak bolo slovo uhadnute, prida points a zastavi hru, hrac sa rozhodne ci nextGamee alebo konci
        if (found()) {
            points++
            updateScore()
            powerUp()
            hideButtons("", false)
            hideAllButtonns()
            binding.casovacTextView.stop()
            binding.casovac2TextView.stop()
            binding.pauzaLayout.visibility = View.VISIBLE
            binding.powerUpButton.isEnabled = false
        }

        //podmienka pre prehru
        if (this.lives == 0) {
            loss()
            binding.casovacTextView.stop()
            hideAllButtonns()
        }
    }

    /**
     * funkcia pre powerupy, vyberie nahodny z 3 powerupov
     */
    fun powerUp() {
        var hodnota = 1


        when (intent.getStringExtra("druh")) {
            "lahke" -> hodnota = 1
            "medium" -> hodnota = 2
            "tazke" -> hodnota = 3
        }
        if (points % hodnota == 0) {
            when (Random().nextInt(3)) {
                0 -> powerUpShow++
                1 -> powerUpTime++
                2 -> powerUpLife++
            }
            binding.pismenoPowerUpButton.isClickable = true
        }


        pocetPowerUpov = powerUpShow + powerUpTime + powerUpLife

        //podmienky pre zobrazenie textu u buttonov, text ukazuje kolko ma hrac jednotlivych pouziti daneho powerUpu
        if (powerUpShow > 0) {
            binding.powerUpPismenoTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpPismenoTextView.visibility = View.INVISIBLE
        }

        if (powerUpTime > 0) {
            binding.powerUpCasTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpCasTextView.visibility = View.INVISIBLE
        }

        if (powerUpLife > 0) {
            binding.powerUpZivotyTextView2.visibility = View.VISIBLE

        } else {
            binding.powerUpZivotyTextView2.visibility = View.INVISIBLE
        }

        if (pocetPowerUpov > 0) {
            binding.pocetPowerUpTextView.visibility = View.VISIBLE

        } else {
            binding.pocetPowerUpTextView.visibility = View.INVISIBLE
        }

        howManyPowerUps()
    }

    /**
     * jednoducha boolean funkcia pre rozhodnutie ci hrac nasiel slovo
     */
    fun found(): Boolean {

        return wordToFind?.contentEquals(String(lettersArray)) == true
    }

    @SuppressLint("SetTextI18n")
    fun howManyPowerUps() {
        binding.pocetPowerUpTextView.text = pocetPowerUpov.toString()
        binding.powerUpZivotyTextView2.text = "" + powerUpLife
        binding.powerUpCasTextView.text = "" + powerUpTime
        binding.powerUpPismenoTextView.text = "" + powerUpShow
    }

    /**
     * funkcia pre nastavenie listenerov vsetkych zadavacich buttonov
     */
    fun nastavButtony() {
        binding.submitAbutton.setOnClickListener {
            if (wordToFind!!.contains("A")) {
                submit("A")
            }
            if (wordToFind!!.contains("Á")) {
                submit("Á")
            }
            if (wordToFind!!.contains("Ä")) {
                submit("Ä")
            }

            if (!wordToFind!!.contains("A") && !wordToFind!!.contains("Á") && !wordToFind!!.contains(
                    "Ä"
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

            if (wordToFind!!.contains("Č")) {
                submit("Č")

            }
            if (!wordToFind!!.contains("Č") && !wordToFind!!.contains("C")) {
                submit("C")
            }
        }
        binding.submitDbutton.setOnClickListener {

            if (wordToFind!!.contains("D")) {
                submit("D")

            }
            if (wordToFind!!.contains("Ď")) {
                submit("Ď")

            }
            if (!wordToFind!!.contains("D") && !wordToFind!!.contains("Ď")) {
                submit("D")
            }
        }
        binding.submitEbutton.setOnClickListener {

            if (wordToFind!!.contains("E")) {
                submit("E")

            }
            if (wordToFind!!.contains("É")) {
                submit("É")

            }
            if (!wordToFind!!.contains("E") && !wordToFind!!.contains("É")) {
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
            if (wordToFind!!.contains("Í")) {
                submit("Í")

            }
            if (!wordToFind!!.contains("I") && !wordToFind!!.contains("Í")) {
                submit("I")
            }
        }
        binding.submitJbutton.setOnClickListener { submit("J") }
        binding.submitKbutton.setOnClickListener { submit("K") }
        binding.submitLbutton.setOnClickListener {

            if (wordToFind!!.contains("L")) {
                submit("L")

            }
            if (wordToFind!!.contains("Ĺ")) {
                submit("Ĺ")

            } else if (wordToFind!!.contains("Ľ")) {
                submit("Ľ")

            }

            if (!wordToFind!!.contains("L") && !wordToFind!!.contains("Ľ") && !wordToFind!!.contains(
                    "Ĺ"
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
            if (wordToFind!!.contains("Ň")) {
                submit("Ň")

            }

            if (!wordToFind!!.contains("N") && !wordToFind!!.contains("Ň")) {
                submit("N")
            }
        }
        binding.submitObutton.setOnClickListener {

            if (wordToFind!!.contains("O")) {
                submit("O")

            }
            if (wordToFind!!.contains("Ó")) {
                submit("Ó")

            } else if (wordToFind!!.contains("Ô")) {
                submit("Ô")

            }

            if (!wordToFind!!.contains("O") && !wordToFind!!.contains("Ó") && !wordToFind!!.contains(
                    "Ô"
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
            if (wordToFind!!.contains("Š")) {
                submit("Š")

            }
            if (!wordToFind!!.contains("S") && !wordToFind!!.contains("Ś")) {
                submit("S")
            }
        }
        binding.submitTbutton.setOnClickListener {
            if (wordToFind!!.contains("T")) {
                submit("T")

            }
            if (wordToFind!!.contains("Ť")) {
                submit("Ť")

            }
            if (!wordToFind!!.contains("T") && !wordToFind!!.contains("Ť")) {
                submit("T")
            }
        }
        binding.submitUbutton.setOnClickListener {
            if (wordToFind!!.contains("U")) {
                submit("U")

            }
            if (wordToFind!!.contains("Ú")) {
                submit("Ú")

            }
            if (!wordToFind!!.contains("Ú") && !wordToFind!!.contains("U")) {
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
            if (wordToFind!!.contains("Ý")) {
                submit("Ý")

            }

            if (!wordToFind!!.contains("Y") && !wordToFind!!.contains("Ý")) {
                submit("Y")
            }
        }
        binding.submitZbutton.setOnClickListener {
            if (wordToFind!!.contains("Z")) {
                submit("Z")

            }
            if (wordToFind!!.contains("Ž")) {
                submit("Ž")

            }

            if (!wordToFind!!.contains("Z") && !wordToFind!!.contains("Ž")) {
                submit("Z")
            }
        }
    }

    /**
     * funkcia pre schovanie jedlotlivych buttonov podla zadaneho pismena
     * @param p
     * @param schovaj
     */
    fun hideButtons(p: String, schovaj: Boolean) {
        if (schovaj) {
            when (p) {
                "A", "Á" -> binding.submitAbutton.visibility = View.INVISIBLE
                "B" -> binding.submitBbutton.visibility = View.INVISIBLE
                "C", "Č" -> binding.submitCbutton.visibility = View.INVISIBLE
                "D", "Ď" -> binding.submitDbutton.visibility = View.INVISIBLE
                "E", "É" -> binding.submitEbutton.visibility = View.INVISIBLE
                "F" -> binding.submitFbutton.visibility = View.INVISIBLE
                "G" -> binding.submitGbutton.visibility = View.INVISIBLE
                "H" -> binding.submitHbutton.visibility = View.INVISIBLE
                "I", "Í" -> binding.submitIbutton.visibility = View.INVISIBLE
                "J" -> binding.submitJbutton.visibility = View.INVISIBLE
                "K" -> binding.submitKbutton.visibility = View.INVISIBLE
                "L", "Ľ" -> binding.submitLbutton.visibility = View.INVISIBLE
                "M" -> binding.submitMbutton.visibility = View.INVISIBLE
                "N", "Ň" -> binding.submitNbutton.visibility = View.INVISIBLE
                "O", "Ó" -> binding.submitObutton.visibility = View.INVISIBLE
                "P" -> binding.submitPbutton.visibility = View.INVISIBLE
                "Q" -> binding.submitQbutton.visibility = View.INVISIBLE
                "R" -> binding.submitRbutton.visibility = View.INVISIBLE
                "S", "Š" -> binding.submitSbutton.visibility = View.INVISIBLE
                "T", "Ť" -> binding.submitTbutton.visibility = View.INVISIBLE
                "U", "Ú" -> binding.submitUbutton.visibility = View.INVISIBLE
                "V" -> binding.submitVbutton.visibility = View.INVISIBLE
                "W" -> binding.submitWbutton.visibility = View.INVISIBLE
                "X" -> binding.submitXbutton.visibility = View.INVISIBLE
                "Y", "Ý" -> binding.submitYbutton.visibility = View.INVISIBLE
                "Z", "Ž" -> binding.submitZbutton.visibility = View.INVISIBLE

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
        binding.prezradeneSlovoText.visibility = View.VISIBLE
        binding.hladaneSlovoText.visibility = View.INVISIBLE
        binding.prezradeneSlovoText.text = "Prehral si, hladane slovo bolo " + wordToFind
        binding.pauzaLayout.visibility = View.VISIBLE
        binding.pokracovatButton.visibility = View.INVISIBLE
        hideAllButtonns()
        binding.powerUpButton.isEnabled = false
        binding.powerUpLayout.visibility = View.INVISIBLE
    }

    /**
     * funkcia pre schovanie layoutu so vsetkymi buttonami pismen
     */
    fun hideAllButtonns() {
        binding.buttonyLayout.visibility = View.INVISIBLE

    }

    /**
     * funkcia pre odkrytie layoutu so vsetkymi buttonami pismen
     */
    fun showAllButtons() {
        binding.buttonyLayout.visibility = View.VISIBLE
    }
}