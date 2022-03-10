package sk.uniza.fri.slivovsky.semestralnapraca.Skore

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentSkoreBinding

/**
 * Fragment ktori zobrazuje top hracov, ich skore a datum.
 *
 */
class SkoreFragment:Fragment() {
    private var _binding: FragmentSkoreBinding? = null
    private val binding get()=_binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {



        _binding = FragmentSkoreBinding.inflate(inflater, container, false)

        return binding.root
    }

    /**
     * Z databazy dostane pomocou funkcie getBest najlepsich 10 hracov.
     * Do adaptera posle zoznam tychto hracov.
     *
     * @param view
     * @param savedInstanceState
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var databaza = SkoreDatabaza.getInstance(requireContext()).SkoreDatabazaDao

        val najHraci = databaza.getBest()

        binding.skoreRecylclerView.adapter = SkoreAdapter(requireContext(),najHraci!!)

        view.findViewById<Button>(R.id.buttonVratDoMenuZoSkore).setOnClickListener {
            findNavController().navigate(R.id.action_skoreFragment_to_FirstFragment2)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}