package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityMenuBinding


/**
 * Fragment menu kde hrac zada svoje meno a vyberie si obtiaznost
 *
 */
class MenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuBinding.inflate(layoutInflater)
        val view = binding.root
        val user = Firebase.auth.currentUser
        setContentView(view)

        val intent =Intent(this@MenuActivity,HraActivity::class.java)
        binding.lahkaButton.setOnClickListener {
            if (binding.menoInputText.text.toString().trim().isNotEmpty()){
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.menoInputText.text.toString().trim()
                }
                user!!.updateProfile(profileUpdates)
            }

            intent.putExtra("druh","svkWordsEasy.txt")
            intent.putExtra("docName","svkEz")
            startActivity(intent)
            }
        binding.stredneTazkaButton.setOnClickListener {
            if (binding.menoInputText.text.toString().trim().isNotEmpty()){
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.menoInputText.text.toString().trim()
                }
                user!!.updateProfile(profileUpdates)
            }

            intent.putExtra("druh","medium")
            startActivity(intent)

            }
        binding.tazkaButton.setOnClickListener {

            if (binding.menoInputText.text.toString().trim().isNotEmpty()){
                val profileUpdates = userProfileChangeRequest {
                    displayName = binding.menoInputText.text.toString().trim()
                }
                user!!.updateProfile(profileUpdates)
            }
            intent.putExtra("druh","tazke")
            startActivity(intent)

        }

    }


    /**
     * Funkcia ktora skontroluje ci hrac zadal svoje meno.
     * Ak nezadal tak ho nepusti hrat a vypise warning.
     *
     * @return ak nie je zadane meno tak vrati false, inac true
     */
    fun skontrolujInput(): Boolean {

        if (binding.menoInputText.text.toString().trim().isEmpty()) {
            binding.menoInputText.error = "Toto pole nesmie ostat prazdne"
            binding.lahkaButton.isActivated = false
            return false
        }
        return true
    }

}