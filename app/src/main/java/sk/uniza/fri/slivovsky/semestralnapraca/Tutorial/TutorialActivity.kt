package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Message
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityHraBinding
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityTutorialBinding
import java.time.LocalDateTime
import java.util.*

/**
 * Fragment ktori sa stara o logiku hry
 *
 */
class TutorialActivity : AppCompatActivity() {

    private var words = mutableListOf<String>()
    private var wordToFind: String? = null
    private var lettersArray: CharArray = charArrayOf()
    private var helpingLetter: CharArray = charArrayOf()
    private val usedLetters = ArrayList<String>()
    private var lives = 0
    private val Random = Random()
    private var points = 0
    private var powerUpShow = 0
    private var powerUpTime = 0
    private var powerUpLife = 0
    private var powerUpOtvorene = false
    private var pocetPowerUpov = 0
    private var userName = ""
    private lateinit var viewModel: UdajeViewModel
    private lateinit var wordsViewModel: SlovaViewModel
    private var nextGame = false
    private lateinit var binding: ActivityTutorialBinding
    private var sortToast: Toast? = null
    /**
     * Funkcia oncreate ktora je dedena z Fragment classy,
     * zabezpecuje vytvorenie fragmentu
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     *@return inflater
     */

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTutorialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.powerUpButton.setOnClickListener {  showToast("Tlacidlo ktore vam otvori vase dostupne powerUpy")}
        binding.powerUpCasButton.setOnClickListener {  showToast("PowerUp ktory pozastavi casomieru na 10 sekund")}
        binding.pismenoPowerUpButton.setOnClickListener {  showToast("PowerUp ktory za vas doplni jedno alebo viacero rovnakych pismen ktore sa nachadzaju v slove")}
        binding.zivotyPowerUpButton.setOnClickListener {  showToast("PowerUp ktory vam prida 1 zivot")}
        binding.ObesenecObr.setOnClickListener {  showToast("Tu sa ukazuje vizualny priebeh vasho obesenca")}
        binding.zivotyImageView.setOnClickListener {  showToast("Pocet aktualnych zivotov")}
        binding.casovacTextView.setOnClickListener {  showToast("Zostavajuci cas")}
        binding.skoreTextView.setOnClickListener {  showToast("Zobrazenie vasho aktualneho skore")}
        binding.hladaneSlovoText.setOnClickListener {  showToast("Tu sa zobrazuju priebeh hladaneho slova")}
        binding.submitAbutton.setOnClickListener {  showToast("Pomocou podobnych tlacidiel budete mat moznost hadat pismena")}

    }


    fun showToast(message: String){
        sortToast?.cancel()
        sortToast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        sortToast?.show()
    }






}