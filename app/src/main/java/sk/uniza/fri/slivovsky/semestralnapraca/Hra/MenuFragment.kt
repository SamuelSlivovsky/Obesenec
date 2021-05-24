package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fragment_menu.*
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel

/**
 * Fragment menu kde hrac zada svoje meno a vyberie si obtiaznost
 *
 */
class MenuFragment : Fragment() {


    private val viewModel: UdajeViewModel by activityViewModels()
    private val slovaViewModel : SlovaViewModel by activityViewModels()

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

        return inflater.inflate(R.layout.fragment_menu, container, false)
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
            viewModel.menoHraca = menoInputText.text.toString().trim()
            if (skontrolujInput()) {
                slovaViewModel.druhSlova = "lahke"
                findNavController().navigate(R.id.action_menuFragment_to_fragmentHra)
            }
        }
        view.findViewById<Button>(R.id.stredneTazkaButton).setOnClickListener {
            viewModel.menoHraca = menoInputText.text.toString().trim()
            if (skontrolujInput()) {
                slovaViewModel.druhSlova = "stredneTazke"
                findNavController().navigate(R.id.action_menuFragment_to_fragmentHra)
            }
        }
        view.findViewById<Button>(R.id.tazkaButton).setOnClickListener {
            viewModel.menoHraca = menoInputText.text.toString().trim()
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

        if (menoInputText.text.toString().trim().isEmpty()) {
            menoInputText.error = "Toto pole nesmie ostat prazdne"
            lahkaButton.isActivated = false
            return false
        }
        return true
    }
}