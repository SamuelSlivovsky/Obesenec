package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentMenuBinding

/**
 * Fragment menu kde hrac zada svoje meno a vyberie si obtiaznost
 *
 */
class MenuFragment : Fragment() {


    private val viewModel: UdajeViewModel by activityViewModels()
    private val slovaViewModel : SlovaViewModel by activityViewModels()
    private var _binding: FragmentMenuBinding? = null
    private val binding get()=_binding!!
    /**
     *Funkcia ktora vytvori fragment menu
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

        _binding =  FragmentMenuBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Nastavi listenery pre menu buttony
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.lahkaButton).setOnClickListener {
            viewModel.menoHraca = binding.menoInputText.text.toString().trim()
            if (skontrolujInput()) {
                slovaViewModel.druhSlova = "lahke"
                findNavController().navigate(R.id.action_menuFragment_to_fragmentHra)
            }
        }
        view.findViewById<Button>(R.id.stredneTazkaButton).setOnClickListener {
            viewModel.menoHraca = binding.menoInputText.text.toString().trim()
            if (skontrolujInput()) {
                slovaViewModel.druhSlova = "stredneTazke"
                findNavController().navigate(R.id.action_menuFragment_to_fragmentHra)
            }
        }
        view.findViewById<Button>(R.id.tazkaButton).setOnClickListener {
            viewModel.menoHraca = binding.menoInputText.text.toString().trim()
            if (skontrolujInput()) {
                slovaViewModel.druhSlova = "tazke"
                findNavController().navigate(R.id.action_menuFragment_to_fragmentHra)
            }
        }
    }

    /**
     * Funkcia ktora skontroluje ci hrac zadal svoje meno.
     * Ak nezadal tak ho nepusti hrat a vypise warning.
     *
     * @return ak nie je zadane meno tak vrati false, inac true
     */
    fun skontrolujInput(): Boolean {

        if (binding.menoInputText.text.toString().trim().isEmpty()) {
            binding.menoInputText.error = "Toto pole nesmie ostat prazdne"
            binding.lahkaButton.isActivated = false
            return false
        }
        return true
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}