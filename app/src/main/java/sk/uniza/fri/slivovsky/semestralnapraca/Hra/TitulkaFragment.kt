package sk.uniza.fri.slivovsky.semestralnapraca.Historia

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.MenuActivity
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.TitulkaActivity
import sk.uniza.fri.slivovsky.semestralnapraca.Hra.TutorialActivity
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.Skore.ScoreActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentHistoriaHracaBinding
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentTitulkaBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class TitulkaFragment : Fragment() {

    private var _binding: FragmentTitulkaBinding? = null
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

        _binding = FragmentTitulkaBinding.inflate(inflater, container, false)

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

        binding.playButton.setOnClickListener {
            startActivity(Intent(context, MenuActivity::class.java))
        }
        binding.menuDoSkoreButton.setOnClickListener {
            startActivity(Intent(context, TutorialActivity::class.java))
        }

        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(context, ScoreActivity::class.java))
        }


    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}