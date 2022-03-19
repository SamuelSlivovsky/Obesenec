package sk.uniza.fri.slivovsky.semestralnapraca.title

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import sk.uniza.fri.slivovsky.semestralnapraca.LocaleHelper
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentSettingsBinding
import sk.uniza.fri.slivovsky.semestralnapraca.game.GameActivity

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

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

        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
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


        binding.menuButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.overflow_menu)
        }

        val currLang = LocaleHelper.getLanguage(requireContext())
        println(currLang)
        if (currLang == "sk") binding.currLangTextView.text =
            getString(R.string.currLang) + " Slovenčina" else binding.currLangTextView.text =
            getString(R.string.currLang) + " English"


    }

    private fun showMenu(v: View, @MenuRes menuRes: Int) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem: MenuItem ->


            when (menuItem.itemId) {
                R.id.option_1 -> {
                    LocaleHelper.setLocale(requireContext(), "en")
                    activity?.supportFragmentManager?.beginTransaction()?.detach(this)
                        ?.attach(this)
                        ?.commit();

                }
                R.id.option_2 -> {
                    LocaleHelper.setLocale(requireContext(), "sk")

                }
                else -> {
                    val intent = Intent(context, TitleActivity::class.java)
                    intent.putExtra("background", "background2")
                    startActivity(intent)
                }

            }

            true
        }
        popup.setOnDismissListener {
            // Respond to popup being dismissed.
        }
        // Show the popup menu.
        popup.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}