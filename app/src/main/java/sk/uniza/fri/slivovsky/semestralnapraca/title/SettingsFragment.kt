package sk.uniza.fri.slivovsky.semestralnapraca.title

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import sk.uniza.fri.slivovsky.semestralnapraca.LocaleHelper
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentSettingsBinding

/**
 * Fragment ktory v recyler view drzi historu skore pre jednotlivych hracov
 *
 */
class SettingsFragment : Fragment() {

    private lateinit var binding: FragmentSettingsBinding

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

        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Ziska z bundle meno hraca, bundle je posielany z SkoreAdapter po longClicku.
     * Datbaza na zaklade mena hraca ziska jeho historiu hier
     *
     * @param view
     * @param savedInstanceState
     */
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.menuButton.setOnClickListener { v: View ->
            showMenu(v, R.menu.overflow_menu)
        }

        val currLang = LocaleHelper.getLanguage(requireContext())
        if (currLang == "sk") binding.currLangTextView.text =
            getString(R.string.currLang) + " Slovenčina" else binding.currLangTextView.text =
            getString(R.string.currLang) + " English"

        when (activity?.getSharedPreferences("background", AppCompatActivity.MODE_PRIVATE)
            ?.getString("background", "")) {
            "background1" -> binding.radioButton1.isChecked = true
            "background2" -> binding.radioButton2.isChecked = true
            "background3" -> binding.radioButton3.isChecked = true
            "background4" -> binding.radioButton4.isChecked = true
            "background5" -> binding.radioButton5.isChecked = true

        }

        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->

            when (checkedId) {
                R.id.radio_button_1 -> changeBackground("background1")

                R.id.radio_button_2 -> changeBackground("background2")

                R.id.radio_button_3 -> changeBackground("background3")

                R.id.radio_button_4 -> changeBackground("background4")

                R.id.radio_button_5 -> changeBackground("background5")


            }

        }


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
                        ?.commit()

                }
                R.id.option_2 -> {
                    LocaleHelper.setLocale(requireContext(), "sk")
                    activity?.supportFragmentManager?.beginTransaction()?.detach(this)
                        ?.attach(this)
                        ?.commit()
                }
                else -> {

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

    private fun changeBackground(background: String) {


        val intent = Intent(context, TitleActivity::class.java)
        intent.putExtra("background", background)
        startActivity(intent)

    }

}