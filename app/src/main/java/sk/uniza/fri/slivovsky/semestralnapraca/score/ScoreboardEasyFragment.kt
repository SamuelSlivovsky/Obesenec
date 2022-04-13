package sk.uniza.fri.slivovsky.semestralnapraca.score

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentScoreBinding
import sk.uniza.fri.slivovsky.semestralnapraca.playerHistory.HistoryAdapter

/**
 * Fragment ktori zobrazuje top hracov, ich skore a datum.
 *
 */
class ScoreboardEasyFragment : Fragment() {
    private lateinit var binding: FragmentScoreBinding
    var list = mutableListOf<PlayersModelClass>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        for (item in list) {
            list.remove(item)
        }
        binding = FragmentScoreBinding.inflate(inflater, container, false)
        val db = Firebase.firestore
        db.collection("scoreboardeasy")
            .orderBy("score", Query.Direction.DESCENDING).limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    list.add(
                        PlayersModelClass(
                            document.data.getValue("name").toString(),
                            (document.data.getValue("score") as Number).toInt(),
                            document.data.getValue("date").toString(),
                            document.id
                        )
                    )
                }
                binding.skoreRecylclerView.adapter = context?.let { SkoreAdapter(it, list) }
            }

        binding.backButton.setOnClickListener {
            startActivity(Intent(requireContext(), TitleActivity::class.java))
        }
        return binding.root
    }


}