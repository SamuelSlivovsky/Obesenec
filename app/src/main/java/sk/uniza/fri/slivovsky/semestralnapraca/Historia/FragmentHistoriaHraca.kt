package sk.uniza.fri.slivovsky.semestralnapraca.Historia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_historia_hraca.*
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class FragmentHistoriaHraca : Fragment() {

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

        return inflater.inflate(R.layout.fragment_historia_hraca, container, false)
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

        var hrac = requireArguments().getString("hrac")
        var databaza = SkoreDatabaza.getInstance(requireContext()).SkoreDatabazaDao

        val historiaHraca = databaza.getHistoriaHraca(hrac!!)
        menoHracaText.text = "Hráč: " +  hrac.toString()
        historiaRecylcerView.adapter = HistoriaAdapter(requireContext(),historiaHraca!!)

        view.findViewById<Button>(R.id.buttonSpatZHistorie).setOnClickListener {
            findNavController().navigate(R.id.action_fragmentHistoriaHraca_to_skoreFragment)
        }
    }
}