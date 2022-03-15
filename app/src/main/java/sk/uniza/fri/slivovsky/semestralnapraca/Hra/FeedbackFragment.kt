package sk.uniza.fri.slivovsky.semestralnapraca.Historia

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.MenuActivity
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.TitulkaActivity
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.TutorialActivity
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.Skore.ScoreActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentFeedbackBinding
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentHistoriaHracaBinding
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentTitulkaBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class FeedbackFragment : Fragment() {

    private var _binding: FragmentFeedbackBinding? = null
    private val binding get() = _binding!!
    private lateinit var auth: FirebaseAuth

    /**
     *
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFeedbackBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Ziska z bundle meno hraca, bundle je posielany z SkoreAdapter po longClicku.
     * Datbaza na zaklade mena hraca ziska jeho historiu hier
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val currUser = auth.currentUser
        val db = Firebase.firestore

        binding.submitButton.setOnClickListener {

            val feedback = hashMapOf(
                "feedback" to binding.feedbackInputText.text.toString().trim()
            )

            db.collection("feedback").document(currUser!!.uid).set(feedback)
            Toast.makeText(context, "Feedback odoslany", Toast.LENGTH_LONG).show()
            binding.feedbackInputText.setText("")
            view.hideSoftInput()
        }


    }

    fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}