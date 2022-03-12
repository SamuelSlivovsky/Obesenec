package sk.uniza.fri.slivovsky.semestralnapraca.Historia

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentHistoriaHracaBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class FragmentHistoriaHraca : Fragment() {

    private var _binding: FragmentHistoriaHracaBinding? = null
    private val binding get()=_binding!!
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

        _binding = FragmentHistoriaHracaBinding.inflate(inflater, container, false)

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

        var hrac = requireArguments().getString("hrac")
        var databaza = SkoreDatabaza.getInstance(requireContext()).SkoreDatabazaDao

        val historiaHraca = databaza.getHistoriaHraca(hrac!!)
        binding.menoHracaText.text = "Hráč: " +  hrac.toString()
        binding.historiaRecylcerView.adapter = HistoriaAdapter(requireContext(),historiaHraca!!)

        view.findViewById<Button>(R.id.buttonSpatZHistorie).setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}