package sk.uniza.fri.slivovsky.semestralnapraca.Skore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_skore.*
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R

/**
 * Fragment ktori zobrazuje top hracov, ich skore a datum.
 *
 */
class SkoreFragment:Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        return inflater.inflate(R.layout.fragment_skore, container, false)
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

        val najHraci = databaza.getBest()

        skoreRecylclerView.adapter = SkoreAdapter(requireContext(),najHraci!!)

        view.findViewById<Button>(R.id.buttonVratDoMenuZoSkore).setOnClickListener {
            findNavController().navigate(R.id.action_skoreFragment_to_FirstFragment2)
        }

    }
}