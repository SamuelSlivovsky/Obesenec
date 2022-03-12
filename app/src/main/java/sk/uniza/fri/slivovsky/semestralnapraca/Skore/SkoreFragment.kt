package sk.uniza.fri.slivovsky.semestralnapraca.Skore

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentSkoreBinding

/**
 * Fragment ktori zobrazuje top hracov, ich skore a datum.
 *
 */
class SkoreFragment:Fragment() {
    private var _binding: FragmentSkoreBinding? = null
    private val binding get()=_binding!!
    var list = mutableListOf<PlayersModelClass>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        _binding = FragmentSkoreBinding.inflate(inflater, container, false)

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

        var databaza = SkoreDatabaza.getInstance(requireContext()).SkoreDatabazaDao
        val db = Firebase.firestore


        db.collection("scoreboard")
            .orderBy("score", Query.Direction.DESCENDING).limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    list.add(PlayersModelClass(document.data.getValue("name").toString(),
                        (document.data.getValue("score") as Number).toInt(), document.data.getValue("date").toString()
                    ))
                }
                binding.skoreRecylclerView.adapter = SkoreAdapter(requireContext(),list)

            }

        view.findViewById<Button>(R.id.buttonVratDoMenuZoSkore).setOnClickListener {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}