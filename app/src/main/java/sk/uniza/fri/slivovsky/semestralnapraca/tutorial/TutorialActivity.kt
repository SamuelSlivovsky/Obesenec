package sk.uniza.fri.slivovsky.semestralnapraca.tutorial

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityTutorialBinding
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity

/**
 * Fragment ktori sa stara o logiku hry
 *
 */
class TutorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTutorialBinding
    private var sortToast: Toast? = null
    /**
     * Funkcia oncreate ktora je dedena z Fragment classy,
     * zabezpecuje vytvorenie fragmentu
     *
     * @param savedInstanceState
     *@return inflater
     */

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val background = getSharedPreferences(
            "background",
            MODE_PRIVATE
        )
        val myBackground = background.getString("background", "defaultvalue")
        if (myBackground == "background2") {
            setTheme(R.style.Background2)
        }
        binding = ActivityTutorialBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        hideSystemBars()
        binding.powerUpButton.setOnClickListener {  showToast("Tlacidlo ktore vam otvori vase dostupne powerUpy")}
        binding.powerUpTimeButton.setOnClickListener {  showToast("PowerUp ktory pozastavi casomieru na 10 sekund")}
        binding.showPowerUpButton.setOnClickListener {  showToast("PowerUp ktory za vas doplni jedno alebo viacero rovnakych pismen ktore sa nachadzaju v slove")}
        binding.livesPowerUpButton.setOnClickListener {  showToast("PowerUp ktory vam prida 1 zivot")}
        binding.hangmanImageView.setOnClickListener {  showToast("Tu sa ukazuje vizualny priebeh vasho obesenca")}
        binding.livesImageView.setOnClickListener {  showToast("Pocet aktualnych zivotov")}
        binding.timerTextView.setOnClickListener {  showToast("Zostavajuci cas")}
        binding.scoreTextView.setOnClickListener {  showToast("Zobrazenie vasho aktualneho skore")}
        binding.wordToFindSlovoText.setOnClickListener {  showToast("Tu sa zobrazuju priebeh hladaneho slova")}
        binding.submitAbutton.setOnClickListener {  showToast("Pomocou podobnych tlacidiel budete mat moznost hadat pismena")}
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@TutorialActivity, TitleActivity::class.java))
        }

    }

    private fun hideSystemBars() {
        val windowInsetsController = ViewCompat.getWindowInsetsController(window.decorView) ?: return
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
    private fun showToast(message: String){
        sortToast?.cancel()
        sortToast = Toast.makeText(applicationContext, message, Toast.LENGTH_LONG)
        sortToast?.show()
    }






}