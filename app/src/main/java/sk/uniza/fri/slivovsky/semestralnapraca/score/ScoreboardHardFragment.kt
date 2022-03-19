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
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentScoreHardBinding
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentScoreMediumBinding

/**
 * Fragment ktori zobrazuje top hracov, ich skore a datum.
 *
 */
class ScoreboardHardFragment:Fragment() {
    private var _binding: FragmentScoreHardBinding? = null
    private val binding get()=_binding!!
    var list = mutableListOf<PlayersModelClass>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentScoreHardBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Z databazy dostane pomocou funkcie getBest najlepsich 10 hracov.
     * Do adaptera posle zoznam tychto hracov.
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        for (item in list){
            list.remove(item)
        }
        val db = Firebase.firestore
        db.collection("scoreboardhard")
            .orderBy("score", Query.Direction.DESCENDING).limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    list.add(PlayersModelClass(document.data.getValue("name").toString(),
                        (document.data.getValue("score") as Number).toInt(), document.data.getValue("date").toString(),
                        document.id
                    ))
                }
                binding.skoreRecylclerView.adapter = SkoreAdapter(requireContext(),list)
            }
        binding.buttonBackToMenu.setOnClickListener {

            startActivity(Intent(requireContext(), TitleActivity::class.java))
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}