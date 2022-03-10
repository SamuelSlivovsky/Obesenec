package sk.uniza.fri.slivovsky.semestralnapraca.Hra

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.SystemClock
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.SlovaViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.ViewModels.UdajeViewModel
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ActivityHraBinding
import java.util.*

/**
 * Fragment ktori sa stara o logiku hry
 *
 */
class HraActivity : AppCompatActivity() {

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
    private lateinit var viewModel: UdajeViewModel
    private lateinit var slovaViewModel: SlovaViewModel
    private var pokracuj = false
    private lateinit var binding: ActivityHraBinding

    /**
     * Funkcia oncreate ktora je dedena z Fragment classy,
     * zabezpecuje vytvorenie fragmentu
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     *@return inflater
     */

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHraBinding.inflate(layoutInflater)
        val view = binding.root

        val user = Firebase.auth.currentUser
        menoHraca = user?.displayName.toString()
        setContentView(view)
        viewModel = ViewModelProviders.of(this).get(UdajeViewModel::class.java)
        slovaViewModel = ViewModelProviders.of(this).get(SlovaViewModel::class.java)

        slova = slovaViewModel.slovaLahke


        novaHra(slova)

        binding.casovac2TextView.setOnChronometerTickListener {
            if (binding.casovac2TextView.text == "00:00") {
                binding.casovacTextView.start()
                binding.casovac2TextView.visibility = View.INVISIBLE
                binding.casovacTextView.visibility = View.VISIBLE
                binding.casovac2TextView.stop()
            }
        }

        binding.casovacTextView.setOnChronometerTickListener {
            if (binding.casovacTextView.text == "00:00") {
                prehra()
                binding.casovacTextView.stop()
            }
        }

        //  animacia pre powerUp button

        val ukazButon: Animation =
            AnimationUtils.loadAnimation(getActivity(this@HraActivity), R.anim.show_button)
        val schovajButon: Animation = AnimationUtils.loadAnimation(
            getActivity(this@HraActivity),
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
                    binding.powerUpPismenoTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.pocetPowerUpTextView.visibility = View.INVISIBLE
            }
            aktPocetPUPtext()
        }

        val powerUpCas: FloatingActionButton = view.findViewById(R.id.powerUpCasButton)
        powerUpCas.setOnClickListener {
            if (powerUpDajCas > 0) {
                powerUpDajCas--
                pocetPowerUpov--
                binding.casovac2TextView.visibility = View.VISIBLE
                binding.casovacTextView.visibility = View.INVISIBLE
                binding.casovacTextView.stop()
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    binding.casovac2TextView.isCountDown = true
                }
                binding.casovac2TextView.base = SystemClock.elapsedRealtime() + 10000
                binding.casovac2TextView.start()

                if (powerUpDajCas == 0) {
                    binding.powerUpCasTextView.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.pocetPowerUpTextView.visibility = View.INVISIBLE
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
                    binding.powerUpZivotyTextView2.visibility = View.INVISIBLE
                }
            }

            if (pocetPowerUpov == 0) {
                binding.pocetPowerUpTextView.visibility = View.INVISIBLE
            }
            updateObr()
            aktPocetPUPtext()
        }


        val powerUpMenu: FloatingActionButton = view.findViewById(R.id.powerUpButton)
        powerUpMenu.setOnClickListener {

            if (powerUpOtvorene) {
                powerUpMenu.startAnimation(schovajButon)
                binding.powerUpLayout.visibility = View.INVISIBLE
                powerUpOtvorene = false
            } else {
                powerUpMenu.startAnimation(ukazButon)
                binding.powerUpLayout.visibility = View.VISIBLE
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
                binding.pauzaLayout.visibility = View.INVISIBLE
                binding.powerUpButton.isEnabled = true
            } else {
                prehra()
                binding.prezradeneSlovoText.text =
                    "Gratulujem, uhádol si všetky slová. Za uhádnutie všetkých slov dostaneš " + pocetPowerUpov + " bonusových bodov"
                body += pocetPowerUpov
                updateSkore()
            }
        }

        /*   //listener pre button na ukoncenie
           val koniec: Button = view.findViewById(R.id.koniecButton)
           koniec.setOnClickListener {
               val bundle = bundleOf("body" to body)
               findNavController().navigate(R.id.action_fragmentHra_to_koniecFragment, bundle)
           }*/
    }


    /**
     * Funkcia pre update resp. zvysenie skóre
     */
    fun updateSkore() {
        binding.skoreTextView.text = "Skóre: " + body
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

    @SuppressLint("RestrictedApi")
    private fun updateObr() {
        if (zivoty >= 6) {

            binding.ObesenecObr.setImageResource(R.drawable.obesenec_6)
        } else {
            val resImg = resources.getIdentifier(
                "obesenec_$zivoty", "drawable",
                getActivity(this@HraActivity)?.getPackageName()
            )
            binding.ObesenecObr.setImageResource(resImg)
        }
        binding.zivotyTextView.text = "" + zivoty
    }

    /**
     * funkcia pre start novej hry, inicializuje atributy na startovne hodnoty
     * @param slova Mutable list ktory je naplneny slovami
     */

    fun novaHra(slova: MutableList<String>) {

        if (!pokracuj) {
            this.zivoty = 6
            body = 0
        }
        pouzitePismena.clear()
        hladaneSlovo = slovoNaNajdenie(slova)
        zoznamPismien = CharArray(hladaneSlovo!!.length)
        for (i in zoznamPismien.indices) {
            zoznamPismien[i] = '_'
        }
        binding.casovac2TextView.visibility = View.INVISIBLE

        var cas = 0
        /*   when (slovaViewModel.druhSlova) {
               "lahke" -> cas = 60000
               "stredneTazke" -> cas = 40000
               "tazke" -> cas = 20000
           }*/
        cas = 60000
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            binding.casovacTextView.isCountDown = true
        }
        binding.casovacTextView.visibility = View.VISIBLE
        binding.casovacTextView.base = SystemClock.elapsedRealtime() + cas
        binding.casovacTextView.start()

        binding.hladaneSlovoText.text = textInicalizacia()
        binding.prezradeneSlovoText.visibility = View.GONE
        binding.prezradeneSlovoText.text = ""
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
            binding.hladaneSlovoText.text = zoznamPismien.joinToString(" ")
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
            binding.casovacTextView.stop()
            binding.casovac2TextView.stop()
            binding.pauzaLayout.visibility = View.VISIBLE
            binding.powerUpButton.isEnabled = false
        }

        //podmienka pre prehru
        if (this.zivoty == 0) {
            prehra()
            binding.casovacTextView.stop()
            schovajVsetkyButtony()
        }
    }

    /**
     * funkcia pre powerupy, vyberie nahodny z 3 powerupov
     */
    fun powerUp() {
        var hodnota = 1
        /*when (slovaViewModel.druhSlova) {
            "lahke" -> hodnota = 1
            "stredneTazke" -> hodnota = 2
            "tazke" -> hodnota = 3
        }*/
        if (body % hodnota == 0) {
            var nahCislo = Random().nextInt(3)
            when (nahCislo) {
                0 -> powerUpUkaz++
                1 -> powerUpDajCas++
                2 -> powerUpDajZivot++
            }
            binding.pismenoPowerUpButton.isClickable = true
        }


        pocetPowerUpov = powerUpUkaz + powerUpDajCas + powerUpDajZivot

        //podmienky pre zobrazenie textu u buttonov, text ukazuje kolko ma hrac jednotlivych pouziti daneho powerUpu
        if (powerUpUkaz > 0) {
            binding.powerUpPismenoTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpPismenoTextView.visibility = View.INVISIBLE
        }

        if (powerUpDajCas > 0) {
            binding.powerUpCasTextView.visibility = View.VISIBLE

        } else {
            binding.powerUpCasTextView.visibility = View.INVISIBLE
        }

        if (powerUpDajZivot > 0) {
            binding.powerUpZivotyTextView2.visibility = View.VISIBLE

        } else {
            binding.powerUpZivotyTextView2.visibility = View.INVISIBLE
        }

        if (pocetPowerUpov > 0) {
            binding.pocetPowerUpTextView.visibility = View.VISIBLE

        } else {
            binding.pocetPowerUpTextView.visibility = View.INVISIBLE
        }

        aktPocetPUPtext()
    }

    /**
     * jednoducha boolean funkcia pre rozhodnutie ci hrac nasiel slovo
     */
    fun boloNajdene(): Boolean {

        return hladaneSlovo?.contentEquals(String(zoznamPismien)) == true
    }

    fun aktPocetPUPtext() {
        binding.pocetPowerUpTextView.text = pocetPowerUpov.toString()
        binding.powerUpZivotyTextView2.text = "" + powerUpDajZivot
        binding.powerUpCasTextView.text = "" + powerUpDajCas
        binding.powerUpPismenoTextView.text = "" + powerUpUkaz
    }

    /**
     * funkcia pre nastavenie listenerov vsetkych zadavacich buttonov
     */
    fun nastavButtony() {
        binding.zadajAbutton.setOnClickListener {
            if (hladaneSlovo!!.contains("A")) {
                zadaj("A")
            }
            if (hladaneSlovo!!.contains("Á")) {
                zadaj("Á")
            }
            if (hladaneSlovo!!.contains("Ä")) {
                zadaj("Ä")
            }

            if (!hladaneSlovo!!.contains("A") && !hladaneSlovo!!.contains("Á") && !hladaneSlovo!!.contains(
                    "Ä"
                )
            ) {
                zadaj("A")
            }
        }
        binding.zadajBbutton.setOnClickListener { zadaj("B") }
        binding.zadajCbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("C")) {
                zadaj("C")

            }

            if (hladaneSlovo!!.contains("Č")) {
                zadaj("Č")

            }
            if (!hladaneSlovo!!.contains("Č") && !hladaneSlovo!!.contains("C")) {
                zadaj("C")
            }
        }
        binding.zadajDbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("D")) {
                zadaj("D")

            }
            if (hladaneSlovo!!.contains("Ď")) {
                zadaj("Ď")

            }
            if (!hladaneSlovo!!.contains("D") && !hladaneSlovo!!.contains("Ď")) {
                zadaj("D")
            }
        }
        binding.zadajEbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("E")) {
                zadaj("E")

            }
            if (hladaneSlovo!!.contains("É")) {
                zadaj("É")

            }
            if (!hladaneSlovo!!.contains("E") && !hladaneSlovo!!.contains("É")) {
                zadaj("E")
            }
        }
        binding.zadajFbutton.setOnClickListener { zadaj("F") }
        binding.zadajGbutton.setOnClickListener { zadaj("G") }
        binding.zadajHbutton.setOnClickListener { zadaj("H") }
        binding.zadajIbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("I")) {
                zadaj("I")

            }
            if (hladaneSlovo!!.contains("Í")) {
                zadaj("Í")

            }
            if (!hladaneSlovo!!.contains("I") && !hladaneSlovo!!.contains("Í")) {
                zadaj("I")
            }
        }
        binding.zadajJbutton.setOnClickListener { zadaj("J") }
        binding.zadajKbutton.setOnClickListener { zadaj("K") }
        binding.zadajLbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("L")) {
                zadaj("L")

            }
            if (hladaneSlovo!!.contains("Ĺ")) {
                zadaj("Ĺ")

            } else if (hladaneSlovo!!.contains("Ľ")) {
                zadaj("Ľ")

            }

            if (!hladaneSlovo!!.contains("L") && !hladaneSlovo!!.contains("Ľ") && !hladaneSlovo!!.contains(
                    "Ĺ"
                )
            ) {
                zadaj("L")
            }
        }

        binding.zadajMbutton.setOnClickListener { zadaj("M") }
        binding.zadajNbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("N")) {
                zadaj("N")

            }
            if (hladaneSlovo!!.contains("Ň")) {
                zadaj("Ň")

            }

            if (!hladaneSlovo!!.contains("N") && !hladaneSlovo!!.contains("Ň")) {
                zadaj("N")
            }
        }
        binding.zadajObutton.setOnClickListener {

            if (hladaneSlovo!!.contains("O")) {
                zadaj("O")

            }
            if (hladaneSlovo!!.contains("Ó")) {
                zadaj("Ó")

            } else if (hladaneSlovo!!.contains("Ô")) {
                zadaj("Ô")

            }

            if (!hladaneSlovo!!.contains("O") && !hladaneSlovo!!.contains("Ó") && !hladaneSlovo!!.contains(
                    "Ô"
                )
            ) {
                zadaj("O")
            }
        }
        binding.zadajPbutton.setOnClickListener { zadaj("P") }
        binding.zadajQbutton.setOnClickListener { zadaj("Q") }
        binding.zadajRbutton.setOnClickListener { zadaj("R") }
        binding.zadajSbutton.setOnClickListener {

            if (hladaneSlovo!!.contains("S")) {
                zadaj("S")

            }
            if (hladaneSlovo!!.contains("Š")) {
                zadaj("Š")

            }
            if (!hladaneSlovo!!.contains("S") && !hladaneSlovo!!.contains("Ś")) {
                zadaj("S")
            }
        }
        binding.zadajTbutton.setOnClickListener {
            if (hladaneSlovo!!.contains("T")) {
                zadaj("T")

            }
            if (hladaneSlovo!!.contains("Ť")) {
                zadaj("Ť")

            }
            if (!hladaneSlovo!!.contains("T") && !hladaneSlovo!!.contains("Ť")) {
                zadaj("T")
            }
        }
        binding.zadajUbutton.setOnClickListener {
            if (hladaneSlovo!!.contains("U")) {
                zadaj("U")

            }
            if (hladaneSlovo!!.contains("Ú")) {
                zadaj("Ú")

            }
            if (!hladaneSlovo!!.contains("Ú") && !hladaneSlovo!!.contains("U")) {
                zadaj("U")
            }

        }
        binding.zadajVbutton.setOnClickListener { zadaj("V") }
        binding.zadajWbutton.setOnClickListener { zadaj("W") }
        binding.zadajXbutton.setOnClickListener { zadaj("X") }
        binding.zadajYbutton.setOnClickListener {
            if (hladaneSlovo!!.contains("Y")) {
                zadaj("Y")

            }
            if (hladaneSlovo!!.contains("Ý")) {
                zadaj("Ý")

            }

            if (!hladaneSlovo!!.contains("Y") && !hladaneSlovo!!.contains("Ý")) {
                zadaj("Y")
            }
        }
        binding.zadajZbutton.setOnClickListener {
            if (hladaneSlovo!!.contains("Z")) {
                zadaj("Z")

            }
            if (hladaneSlovo!!.contains("Ž")) {
                zadaj("Ž")

            }

            if (!hladaneSlovo!!.contains("Z") && !hladaneSlovo!!.contains("Ž")) {
                zadaj("Z")
            }
        }
    }

    /**
     * funkcia pre schovanie jedlotlivych buttonov podla zadaneho pismena
     * @param p
     * @param schovaj
     */
    fun schovajButtony(p: String, schovaj: Boolean) {
        if (schovaj) {
            when (p) {
                "A", "Á" -> binding.zadajAbutton.visibility = View.INVISIBLE
                "B" -> binding.zadajBbutton.visibility = View.INVISIBLE
                "C", "Č" -> binding.zadajCbutton.visibility = View.INVISIBLE
                "D", "Ď" -> binding.zadajDbutton.visibility = View.INVISIBLE
                "E", "É" -> binding.zadajEbutton.visibility = View.INVISIBLE
                "F" -> binding.zadajFbutton.visibility = View.INVISIBLE
                "G" -> binding.zadajGbutton.visibility = View.INVISIBLE
                "H" -> binding.zadajHbutton.visibility = View.INVISIBLE
                "I", "Í" -> binding.zadajIbutton.visibility = View.INVISIBLE
                "J" -> binding.zadajJbutton.visibility = View.INVISIBLE
                "K" -> binding.zadajKbutton.visibility = View.INVISIBLE
                "L", "Ľ" -> binding.zadajLbutton.visibility = View.INVISIBLE
                "M" -> binding.zadajMbutton.visibility = View.INVISIBLE
                "N", "Ň" -> binding.zadajNbutton.visibility = View.INVISIBLE
                "O", "Ó" -> binding.zadajObutton.visibility = View.INVISIBLE
                "P" -> binding.zadajPbutton.visibility = View.INVISIBLE
                "Q" -> binding.zadajQbutton.visibility = View.INVISIBLE
                "R" -> binding.zadajRbutton.visibility = View.INVISIBLE
                "S", "Š" -> binding.zadajSbutton.visibility = View.INVISIBLE
                "T", "Ť" -> binding.zadajTbutton.visibility = View.INVISIBLE
                "U", "Ú" -> binding.zadajUbutton.visibility = View.INVISIBLE
                "V" -> binding.zadajVbutton.visibility = View.INVISIBLE
                "W" -> binding.zadajWbutton.visibility = View.INVISIBLE
                "X" -> binding.zadajXbutton.visibility = View.INVISIBLE
                "Y", "Ý" -> binding.zadajYbutton.visibility = View.INVISIBLE
                "Z", "Ž" -> binding.zadajZbutton.visibility = View.INVISIBLE

            }
        } else {
            binding.zadajAbutton.visibility = View.VISIBLE
            binding.zadajBbutton.visibility = View.VISIBLE
            binding.zadajCbutton.visibility = View.VISIBLE
            binding.zadajDbutton.visibility = View.VISIBLE
            binding.zadajEbutton.visibility = View.VISIBLE
            binding.zadajFbutton.visibility = View.VISIBLE
            binding.zadajGbutton.visibility = View.VISIBLE
            binding.zadajHbutton.visibility = View.VISIBLE
            binding.zadajIbutton.visibility = View.VISIBLE
            binding.zadajJbutton.visibility = View.VISIBLE
            binding.zadajKbutton.visibility = View.VISIBLE
            binding.zadajLbutton.visibility = View.VISIBLE
            binding.zadajMbutton.visibility = View.VISIBLE
            binding.zadajNbutton.visibility = View.VISIBLE
            binding.zadajObutton.visibility = View.VISIBLE
            binding.zadajPbutton.visibility = View.VISIBLE
            binding.zadajQbutton.visibility = View.VISIBLE
            binding.zadajRbutton.visibility = View.VISIBLE
            binding.zadajSbutton.visibility = View.VISIBLE
            binding.zadajTbutton.visibility = View.VISIBLE
            binding.zadajUbutton.visibility = View.VISIBLE
            binding.zadajVbutton.visibility = View.VISIBLE
            binding.zadajWbutton.visibility = View.VISIBLE
            binding.zadajXbutton.visibility = View.VISIBLE
            binding.zadajYbutton.visibility = View.VISIBLE
            binding.zadajZbutton.visibility = View.VISIBLE
        }
    }

    /**
     * funkcia ak hrac prehra
     */
    fun prehra() {
        binding.prezradeneSlovoText.visibility = View.VISIBLE
        binding.hladaneSlovoText.visibility = View.INVISIBLE
        binding.prezradeneSlovoText.text = "Prehral si, hladane slovo bolo " + hladaneSlovo
        binding.pauzaLayout.visibility = View.VISIBLE
        binding.pokracovatButton.visibility = View.INVISIBLE
        schovajVsetkyButtony()
        binding.powerUpButton.isEnabled = false
        binding.powerUpLayout.visibility = View.INVISIBLE
    }

    /**
     * funkcia pre schovanie layoutu so vsetkymi buttonami pismen
     */
    fun schovajVsetkyButtony() {
        binding.buttonyLayout.visibility = View.INVISIBLE

    }

    /**
     * funkcia pre odkrytie layoutu so vsetkymi buttonami pismen
     */
    fun odkryVsetkyButtony() {
        binding.buttonyLayout.visibility = View.VISIBLE
    }
}