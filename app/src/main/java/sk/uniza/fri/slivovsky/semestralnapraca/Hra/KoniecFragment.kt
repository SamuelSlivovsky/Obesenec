package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.Skore
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentKoniecBinding

/**
 * Trieda KoniecFragment zabezpecuje vlozenie
 * mena hraca a jeho skore do databazy
 */
class KoniecFragment: Fragment(){

    private val viewModel: UdajeViewModel by activityViewModels();
    private var _binding: FragmentKoniecBinding? = null
    private val binding get()=_binding!!
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        var body = requireArguments().getInt("body")
        var databaza = SkoreDatabaza.getInstance(requireContext()).SkoreDatabazaDao
        databaza.insert(Skore(viewModel.menoHraca,body))

        _binding = FragmentKoniecBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * v tejto funkcii si pomocou bundle vypytam hracom ziskane body
     * tieto body potom vypisem pomocou koniecTextView spolu s jeho menom
     * nachadzaju sa tu aj dva buttony ktore hraca presmeruju naspat do menu
     * alebo do fragmentu skore
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var body = requireArguments().getInt("body")
        binding.koniecTextView.text = viewModel.menoHraca + " " +binding.koniecTextView.text.toString() + " " + body
        view.findViewById<Button>(R.id.vratDoMenuButton).setOnClickListener {

        }
        view.findViewById<Button>(R.id.DoSkoreButon).setOnClickListener {

        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}