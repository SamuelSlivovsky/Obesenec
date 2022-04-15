package sk.uniza.fri.slivovsky.semestralnapraca.tutorial

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
        binding.powerUpButton.setOnClickListener {  showToast(getString(R.string.powerUpMenuTutorial))}
        binding.powerUpTimeButton.setOnClickListener {  showToast(getString(R.string.powerUpTimeTutorial))}
        binding.showPowerUpButton.setOnClickListener {  showToast(getString(R.string.powerUpShowTutorial))}
        binding.livesPowerUpButton.setOnClickListener {  showToast(getString(R.string.powerUpLivesTutorial))}
        binding.hangmanImageView.setOnClickListener {  showToast(getString(R.string.hangmanImageTutorial))}
        binding.livesImageView.setOnClickListener {  showToast(getString(R.string.livesImageTutorial))}
        binding.timerTextView.setOnClickListener {  showToast(getString(R.string.timerTutorial))}
        binding.scoreTextView.setOnClickListener {  showToast(getString(R.string.scoreTutorial))}
        binding.wordToFindSlovoText.setOnClickListener {  showToast(getString(R.string.wordTutorial))}
        binding.submitAbutton.setOnClickListener {  showToast(getString(R.string.submitTutorial))}
        binding.backButton.setOnClickListener {
            startActivity(Intent(this@TutorialActivity, TitleActivity::class.java))
        }
        binding.questionMarkButtonn.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"))
            startActivity(i)
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