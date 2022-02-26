package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.navigation.fragment.findNavController
import sk.uniza.fri.slivovsky.semestralnapraca.R

/**
 * Prvy fragment aplikacie
 *
 *
 */
class TitulkaFragment : Fragment() {

    /**
     *
     *Funkcia ktora vytvori fragment
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_titulka, container, false)
    }

    /**
     *Po vytvoreni fragmentu sa nastavia onClickListenery pre buttony
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.playButton).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_menuFragment)
        }

        view.findViewById<ImageView>(R.id.scoreIcon).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_skoreFragment2)
        }

    }
}