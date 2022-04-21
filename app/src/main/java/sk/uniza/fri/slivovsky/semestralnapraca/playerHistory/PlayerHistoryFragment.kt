package sk.uniza.fri.slivovsky.semestralnapraca.playerHistory

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentPlayerHistoryBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class PlayerHistoryFragment : Fragment() {

    private lateinit var binding: FragmentPlayerHistoryBinding
    private lateinit var auth: FirebaseAuth
    var list = mutableListOf<HistoryPlayerModelClass>()

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

        binding = FragmentPlayerHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Ziska z bundle meno hraca, bundle je posielany z SkoreAdapter po longClicku.
     * Datbaza na zaklade mena hraca ziska jeho historiu hier
     *
     * @param view
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val currUser = auth.currentUser

        val db = Firebase.firestore
        db.collection("history" + currUser!!.uid)
            .orderBy("date", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val mode: String = when(document.data.getValue("mode").toString()){
                        "easy" -> getString(R.string.easy)
                        "medium" -> getString(R.string.medium)
                        "hard"-> getString(R.string.hard)
                        else -> ""
                    }
                    list.add(
                        HistoryPlayerModelClass(
                            (document.data.getValue("score") as Number).toInt(),
                            document.data.getValue("date").toString(), mode
                        )
                    )
                }
                binding.historyRecylcerView.adapter = context?.let { HistoryAdapter(it, list) }

            }

        binding.playerNameTextView.text =
            getString(R.string.historyPlayer) + " " + currUser.displayName.toString()


    }
}