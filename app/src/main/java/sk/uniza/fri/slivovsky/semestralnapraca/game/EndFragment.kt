package sk.uniza.fri.slivovsky.semestralnapraca.game

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.database.Skore
import sk.uniza.fri.slivovsky.semestralnapraca.database.SkoreDatabaza
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.score.ScoreActivity
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.FragmentKoniecBinding
import sk.uniza.fri.slivovsky.semestralnapraca.title.TitleActivity

/**
 * Trieda KoniecFragment zabezpecuje vlozenie
 * mena hraca a jeho skore do databazy
 */
class EndFragment: Fragment(){

    private var _binding: FragmentKoniecBinding? = null
    private lateinit var auth: FirebaseAuth
    private val binding get()=_binding!!
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        var databaza = SkoreDatabaza.getInstance(requireContext()).SkoreDatabazaDao
        var bundle = arguments
        auth = Firebase.auth
        val currUser = auth.currentUser

        databaza.insert(Skore(currUser!!.displayName.toString(),bundle!!.getInt("points"),currUser.uid))

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
    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val currUser = auth.currentUser
        var bundle = arguments
        binding.endTextView.text = currUser!!.displayName + " " +binding.endTextView.text.toString() + " " + bundle!!.getInt("points")
        view.findViewById<Button>(R.id.backToMenuButton).setOnClickListener {

            startActivity(Intent(requireContext(), TitleActivity::class.java))
        }
        view.findViewById<Button>(R.id.toScoreButton).setOnClickListener {
            startActivity(Intent(requireContext(),ScoreActivity::class.java))
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
    }
}