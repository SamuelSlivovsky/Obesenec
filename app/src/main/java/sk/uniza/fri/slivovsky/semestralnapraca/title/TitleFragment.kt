package sk.uniza.fri.slivovsky.semestralnapraca.title

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import sk.uniza.fri.slivovsky.semestralnapraca.game.MenuActivity
import sk.uniza.fri.slivovsky.semestralnapraca.tutorial.TutorialActivity
import sk.uniza.fri.slivovsky.semestralnapraca.score.ScoreActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentTitleBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class TitleFragment : Fragment() {

    private lateinit var binding: FragmentTitleBinding

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
    ): View {

        binding = FragmentTitleBinding.inflate(inflater, container, false)

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
        binding.tutorialButton.setOnClickListener {
            startActivity(Intent(context, TutorialActivity::class.java))
        }

        binding.scoreIcon.setOnClickListener {
            startActivity(Intent(context, ScoreActivity::class.java))
        }


    }

}