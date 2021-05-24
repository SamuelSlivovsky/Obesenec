package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_hra.*
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel
import java.util.*

/**
 * Fragment ktori sa stara o logiku hry
 *
 */
class HraFragment : Fragment() {

    private var slova = mutableListOf<String>()
    private var hladaneSlovo: String? = null
    private var zoznamPismien: CharArray = charArrayOf()
    private var pomocnePismeno: CharArray = charArrayOf()
    private val pouzitePismena = ArrayList<String>()
    private var zivoty = 0
    private val Random = Random()
    private var body = 0
    private var powerUpUkaz = 0
    private var powerUpDajCas = 0
    private var powerUpDajZivot = 0
    private var powerUpOtvorene = false
    private var pocetPowerUpov = 0
    private var menoHraca = ""
    private val viewModel: UdajeViewModel by activityViewModels()
    private val slovaViewModel: SlovaViewModel by activityViewModels()
    private var pokracuj = false

    /**
     * Funkcia oncreate ktora je dedena z Fragment classy,
     * zabezpecuje vytvorenie fragmentu
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
      *@return inflater
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_hra, container, false)

    }

    /**
     * Po vytvoreni view sa zavola funkcia nova hra a nastavi sa zoznam slov na zaklade obtiaznosti.
     * Nastavi tick listenery pre casovace a click listenery bre buttony.
     *
     * @param view
     * @param savedInstanceState
     */
    @SuppressLint("NewApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //switch ktory urci ktore z balicka slov bude pouzity vzhladom na obtiaznost
        when (slovaViewModel.druhSlova) {
            "lahke" -> slova = slovaViewModel.slovaLahke
            "stredneTazke" -> slova = slovaViewModel.slovaStredneTazke
            "tazke" -> slova = slovaViewModel.slovaTazke

        }
        //start novej hry
        novaHra(slova)

        //nastavenie tickListenerov pre 2 casovace
        casovac2_TextView.setOnChronometerTickListener {
            if (casovac2_TextView.text == "00:00") {
                casovac_TextView.start()
                casovac2_TextView.visibility = View.INVISIBLE
                casovac_TextView.visibility = View.VISIBLE
                casovac2_TextView.stop()
            }
        }

        casovac_TextView.setOnChronometerTickListener {
            if (casovac_TextView.text == "00:00") {
                prehra()
                casovac_TextView.stop()
            }
        }

        //  animacia pre powerUp button

        val ukazButon: Animation = AnimationUtils.loadAnimation(getActivity(), R.anim.show_button)
        val schovajButon: Animation = AnimationUtils.loadAnimation(getActivity(),
            R.anim.hide_button
        )


        //listenery pre powerUpButtony

        val powerUpPismeno: FloatingActionButton = view.findViewById(R.id.pismenoPowerUpButton)
        powerUpPismeno.setOnClickListener {
            if (powerUpUkaz > 0) {
                powerUpUkaz--
                pocetPowerUpov--
                doplnPismeno()
                if (powerUpUkaz == 0) {
                    powerUpPismenoTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                pocet_powerUp_Text_View.visibility = View.INVISIBLE
            }
            aktPocetPUPtext()
        }

        val powerUpCas: FloatingActionButton = view.findViewById(R.id.powerUpCasButton)
        powerUpCas.setOnClickListener {
            if (powerUpDajCas > 0) {
                powerUpDajCas--
                pocetPowerUpov--
                casovac2_TextView.visibility = View.VISIBLE
                casovac_TextView.visibility = View.INVISIBLE
                casovac_TextView.stop()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    casovac2_TextView.isCountDown = true
                }
                casovac2_TextView.base = SystemClock.elapsedRealtime() + 10000
                casovac2_TextView.start()

                if (powerUpDajCas == 0) {
                    powerUpCasTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                pocet_powerUp_Text_View.visibility = View.INVISIBLE
            }
            aktPocetPUPtext()
        }


        val powerUpZivoty: FloatingActionButton = view.findViewById(R.id.zivotyPowerUpButton)
        powerUpZivoty.setOnClickListener {
            if (powerUpDajZivot > 0) {
                powerUpDajZivot--
                pocetPowerUpov--
                this.zivoty++
                if (powerUpDajZivot == 0) {
                    powerUpZivotyTextView2.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                pocet_powerUp_Text_View.visibility = View.INVISIBLE
            }
            updateObr()
            aktPocetPUPtext()
        }


        val powerUpMenu: FloatingActionButton = view.findViewById(R.id.powerUpButton)
        powerUpMenu.setOnClickListener {

            if (powerUpOtvorene) {
                powerUpMenu.startAnimation(schovajButon)
                powerUpLayout.visibility = View.INVISIBLE
                powerUpOtvorene = false
            } else {
                powerUpMenu.startAnimation(ukazButon)
                powerUpLayout.visibility = View.VISIBLE
                powerUpOtvorene = true
            }

        }

        //nastavenie onClickListenerov pre buttony
        nastavButtony()

        //listener pre button na pokracovanie
        val pokracovat: Button = view.findViewById(R.id.pokracovatButton)
        pokracovat.setOnClickListener {
            if (slova.isNotEmpty()) {
                pokracuj = true
                novaHra(slova)
                odkryVsetkyButtony()
                pauzaLayout.visibility = View.INVISIBLE
                powerUpButton.isEnabled = true
            } else {
                prehra()
                prezradeneSlovoText.text =
                    "Gratulujem, uhádol si všetky slová. Za uhádnutie všetkých slov dostaneš " + pocetPowerUpov + " bonusových bodov"
                body += pocetPowerUpov
                updateSkore()
            }
        }

        //listener pre button na ukoncenie
        val koniec: Button = view.findViewById(R.id.koniecButton)
        koniec.setOnClickListener {
            val bundle = bundleOf("body" to body)
            findNavController().navigate(R.id.action_fragmentHra_to_koniecFragment, bundle)
        }

    }

    /**
     * Funkcia pre update resp. zvysenie skóre
     */
    fun updateSkore() {
        skoreTextView.text = "Skóre: " + body
    }

    /**
     * Funkcia pre vybranie nahodne slova zo zoznamu, pouzite slovo sa zo zoznamu vyhodi
     * @return slovo vrati vybrane slovo
     */

    fun slovoNaNajdenie(slova: MutableList<String>): String {
        var cislo = Random.nextInt(slova.size)
        var slovo = slova[cislo]
        slova.removeAt(cislo)
        return slovo
    }

    /**
     *update obrazku obesenca vzhladom na pocet zivotov
     *
     */

    private fun updateObr() {
        if (zivoty >= 6) {

            ObesenecObr.setImageResource(R.drawable.obesenec_6)
        } else {
            val resImg = resources.getIdentifier(
                "obesenec_$zivoty", "drawable",
                getActivity()?.getPackageName()
            )
            ObesenecObr.setImageResource(resImg)
        }
        zivotyTextView.text = "" + zivoty
    }

    /**
     * funkcia pre start novej hry, inicializuje atributy na startovne hodnoty
     * @param slova Mutable list ktory je naplneny slovami
     */

    fun novaHra(slova: MutableList<String>) {

        if (!pokracuj) {
            menoHraca = viewModel.menoHraca
            this.zivoty = 6
            body = 0
        }
        pouzitePismena.clear()
        hladaneSlovo = slovoNaNajdenie(slova)
        zoznamPismien = CharArray(hladaneSlovo!!.length)
        for (i in zoznamPismien.indices) {
            zoznamPismien[i] = '_'
        }
        casovac2_TextView.visibility = View.INVISIBLE

        var cas = 0
        when (slovaViewModel.druhSlova) {
            "lahke" -> cas = 60000
            "stredneTazke" -> cas = 40000
            "tazke" -> cas = 20000
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            casovac_TextView.isCountDown = true
        }
        casovac_TextView.visibility = View.VISIBLE
        casovac_TextView.base = SystemClock.elapsedRealtime() + cas
        casovac_TextView.start()

        hladaneSlovoText.text = textInicalizacia()
        prezradeneSlovoText.visibility = View.GONE
        prezradeneSlovoText.text = ""
        zadaj(" ")

    }

    /**
     * funkcia pre inicializaciu textu resp. nacitanie jednotlivych pismen do char array
     *
     * @return vrati zoznam pismen
     */
    fun textInicalizacia(): String {
        val builder = StringBuilder()
        for (i in zoznamPismien.indices) {
            builder.append(zoznamPismien[i])
            if (i < zoznamPismien.size - 1) {
                builder.append(" ")
            }
        }

        return builder.toString()
    }

    /**
     * funkcia pre doplnenie pismena pomocou powerUpu
     *
     */
    fun doplnPismeno() {
        //nacitam hladane slovo do charArray
        pomocnePismeno = hladaneSlovo!!.toCharArray()

        var index = Random.nextInt(pomocnePismeno.size)
        var pismeno = pomocnePismeno[index]
        //podmienka ktora pozera ci sa nevratila medzera alebo pismeno ktore uz bolo najdene
        if (pismeno == ' ' || pouzitePismena.contains(pismeno)) {
            doplnPismeno()
        }
        //zavola metodu zadaj
        zadaj(pismeno.toString())
    }

    /**
     *  funkcia pre porovnanie ci sa zadane pismeno nachadza v hladanom slove
     *  stara sa aj o priebeh hry, kontroluje pocet zivotov
     *  @param p - pismeno zadane pomocou buttonu
     */

    fun zadaj(p: String) {

        //schovam button
        schovajButtony(p, true)
        //podmienka ktora pozera ci sa pismeno nachadza v slove
            if (hladaneSlovo!!.contains(p)) {
                var index = hladaneSlovo!!.indexOf(p)
                while (index >= 0) {
                    zoznamPismien[index] = p[0]
                    index = hladaneSlovo!!.indexOf(p, index + 1)
                }
                hladaneSlovoText.text = zoznamPismien.joinToString(" ")
                //pokial sa pismeno nenachadza v slove a nie je to medzera tak uberie zivot
            } else if (p != " ") {
                this.zivoty--
                updateObr()
            }
        //prida pismeno do zoznamu pouzitych pismen
            pouzitePismena.add(p)

        //podmienka ak bolo slovo uhadnute, prida body a zastavi hru, hrac sa rozhodne ci pokracuje alebo konci
        if (boloNajdene()) {
            body++
            updateSkore()
            powerUp()
            schovajButtony("", false)
            schovajVsetkyButtony()
            casovac_TextView.stop()
            casovac2_TextView.stop()
            pauzaLayout.visibility = View.VISIBLE
            powerUpButton.isEnabled = false
        }

        //podmienka pre prehru
        if (this.zivoty == 0) {
            prehra()
            casovac_TextView.stop()
            schovajVsetkyButtony()
        }
    }

    /**
     * funkcia pre powerupy, vyberie nahodny z 3 powerupov
     */
    fun powerUp() {
        var hodnota = 0
        when (slovaViewModel.druhSlova) {
            "lahke" -> hodnota = 1
            "stredneTazke" -> hodnota = 2
            "tazke" -> hodnota = 3
        }
        if (body % hodnota == 0) {
            var nahCislo = Random().nextInt(3)
            when (nahCislo) {
                0 -> powerUpUkaz++
                1 -> powerUpDajCas++
                2 -> powerUpDajZivot++
            }
            pismenoPowerUpButton.isClickable = true
        }


        pocetPowerUpov = powerUpUkaz + powerUpDajCas + powerUpDajZivot

        //podmienky pre zobrazenie textu u buttonov, text ukazuje kolko ma hrac jednotlivych pouziti daneho powerUpu
        if (powerUpUkaz > 0) {
            powerUpPismenoTextView.visibility = View.VISIBLE

        } else {
            powerUpPismenoTextView.visibility = View.INVISIBLE
        }

        if (powerUpDajCas > 0) {
            powerUpCasTextView.visibility = View.VISIBLE

        } else {
            powerUpCasTextView.visibility = View.INVISIBLE
        }

        if (powerUpDajZivot > 0) {
            powerUpZivotyTextView2.visibility = View.VISIBLE

        } else {
            powerUpZivotyTextView2.visibility = View.INVISIBLE
        }

        if (pocetPowerUpov > 0) {
            pocet_powerUp_Text_View.visibility = View.VISIBLE

        } else {
            pocet_powerUp_Text_View.visibility = View.INVISIBLE
        }

        aktPocetPUPtext()
    }

    /**
     * jednoducha boolean funkcia pre rozhodnutie ci hrac nasiel slovo
     */
    fun boloNajdene(): Boolean {

        return hladaneSlovo?.contentEquals(String(zoznamPismien)) == true
    }

    fun aktPocetPUPtext(){
        pocet_powerUp_Text_View.text = pocetPowerUpov.toString()
        powerUpZivotyTextView2.text = "" + powerUpDajZivot
        powerUpCasTextView.text = "" + powerUpDajCas
        powerUpPismenoTextView.text = "" + powerUpUkaz
    }

    /**
     * funkcia pre nastavenie listenerov vsetkych zadavacich buttonov
     */
    fun nastavButtony() {
        zadajAbutton.setOnClickListener { zadaj("A") }
        zadajBbutton.setOnClickListener { zadaj("B") }
        zadajCbutton.setOnClickListener { zadaj("C") }
        zadajDbutton.setOnClickListener { zadaj("D") }
        zadajEbutton.setOnClickListener { zadaj("E") }
        zadajFbutton.setOnClickListener { zadaj("F") }
        zadajGbutton.setOnClickListener { zadaj("G") }
        zadajHbutton.setOnClickListener { zadaj("H") }
        zadajIbutton.setOnClickListener { zadaj("I") }
        zadajJbutton.setOnClickListener { zadaj("J") }
        zadajKbutton.setOnClickListener { zadaj("K") }
        zadajLbutton.setOnClickListener { zadaj("L") }
        zadajMbutton.setOnClickListener { zadaj("M") }
        zadajNbutton.setOnClickListener { zadaj("N") }
        zadajObutton.setOnClickListener { zadaj("O") }
        zadajPbutton.setOnClickListener { zadaj("P") }
        zadajQbutton.setOnClickListener { zadaj("Q") }
        zadajRbutton.setOnClickListener { zadaj("R") }
        zadajSbutton.setOnClickListener { zadaj("S") }
        zadajTbutton.setOnClickListener { zadaj("T") }
        zadajUbutton.setOnClickListener { zadaj("U") }
        zadajVbutton.setOnClickListener { zadaj("V") }
        zadajWbutton.setOnClickListener { zadaj("W") }
        zadajXbutton.setOnClickListener { zadaj("X") }
        zadajYbutton.setOnClickListener { zadaj("Y") }
        zadajZbutton.setOnClickListener { zadaj("Z") }
    }

    /**
     * funkcia pre schovanie jedlotlivych buttonov podla zadaneho pismena
     * @param p
     * @param schovaj
     */
    fun schovajButtony(p: String, schovaj: Boolean) {
        if (schovaj) {
            when (p) {
                "A" -> zadajAbutton.visibility = View.INVISIBLE
                "B" -> zadajBbutton.visibility = View.INVISIBLE
                "C" -> zadajCbutton.visibility = View.INVISIBLE
                "D" -> zadajDbutton.visibility = View.INVISIBLE
                "E" -> zadajEbutton.visibility = View.INVISIBLE
                "F" -> zadajFbutton.visibility = View.INVISIBLE
                "G" -> zadajGbutton.visibility = View.INVISIBLE
                "H" -> zadajHbutton.visibility = View.INVISIBLE
                "I" -> zadajIbutton.visibility = View.INVISIBLE
                "J" -> zadajJbutton.visibility = View.INVISIBLE
                "K" -> zadajKbutton.visibility = View.INVISIBLE
                "L" -> zadajLbutton.visibility = View.INVISIBLE
                "M" -> zadajMbutton.visibility = View.INVISIBLE
                "N" -> zadajNbutton.visibility = View.INVISIBLE
                "O" -> zadajObutton.visibility = View.INVISIBLE
                "P" -> zadajPbutton.visibility = View.INVISIBLE
                "Q" -> zadajQbutton.visibility = View.INVISIBLE
                "R" -> zadajRbutton.visibility = View.INVISIBLE
                "S" -> zadajSbutton.visibility = View.INVISIBLE
                "T" -> zadajTbutton.visibility = View.INVISIBLE
                "U" -> zadajUbutton.visibility = View.INVISIBLE
                "V" -> zadajVbutton.visibility = View.INVISIBLE
                "W" -> zadajWbutton.visibility = View.INVISIBLE
                "X" -> zadajXbutton.visibility = View.INVISIBLE
                "Y" -> zadajYbutton.visibility = View.INVISIBLE
                "Z" -> zadajZbutton.visibility = View.INVISIBLE

            }
        } else {
            zadajAbutton.visibility = View.VISIBLE
            zadajBbutton.visibility = View.VISIBLE
            zadajCbutton.visibility = View.VISIBLE
            zadajDbutton.visibility = View.VISIBLE
            zadajEbutton.visibility = View.VISIBLE
            zadajFbutton.visibility = View.VISIBLE
            zadajGbutton.visibility = View.VISIBLE
            zadajHbutton.visibility = View.VISIBLE
            zadajIbutton.visibility = View.VISIBLE
            zadajJbutton.visibility = View.VISIBLE
            zadajKbutton.visibility = View.VISIBLE
            zadajLbutton.visibility = View.VISIBLE
            zadajMbutton.visibility = View.VISIBLE
            zadajNbutton.visibility = View.VISIBLE
            zadajObutton.visibility = View.VISIBLE
            zadajPbutton.visibility = View.VISIBLE
            zadajQbutton.visibility = View.VISIBLE
            zadajRbutton.visibility = View.VISIBLE
            zadajSbutton.visibility = View.VISIBLE
            zadajTbutton.visibility = View.VISIBLE
            zadajUbutton.visibility = View.VISIBLE
            zadajVbutton.visibility = View.VISIBLE
            zadajWbutton.visibility = View.VISIBLE
            zadajXbutton.visibility = View.VISIBLE
            zadajYbutton.visibility = View.VISIBLE
            zadajZbutton.visibility = View.VISIBLE
        }
    }

    /**
     * funkcia ak hrac prehra
     */
    fun prehra() {
        prezradeneSlovoText.visibility = View.VISIBLE
        hladaneSlovoText.visibility = View.INVISIBLE
        prezradeneSlovoText.text = "Prehral si, hladane slovo bolo " + hladaneSlovo
        pauzaLayout.visibility = View.VISIBLE
        pokracovatButton.visibility = View.INVISIBLE
        schovajVsetkyButtony()
        powerUpButton.isEnabled = false
        powerUpLayout.visibility = View.INVISIBLE
    }

    /**
     * funkcia pre schovanie layoutu so vsetkymi buttonami pismen
     */
    fun schovajVsetkyButtony() {
        buttonyLayout.visibility = View.INVISIBLE

    }

    /**
     * funkcia pre odkrytie layoutu so vsetkymi buttonami pismen
     */
    fun odkryVsetkyButtony() {
        buttonyLayout.visibility = View.VISIBLE
    }


}