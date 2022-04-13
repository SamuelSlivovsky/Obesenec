package sk.uniza.fri.slivovsky.semestralnapraca.title

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentFeedbackBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class FeedbackFragment : Fragment() {

    private lateinit var binding: FragmentFeedbackBinding
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
    ): View {

        binding = FragmentFeedbackBinding.inflate(inflater, container, false)

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

    private fun View.hideSoftInput() {
        val inputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(windowToken, 0)
    }

}