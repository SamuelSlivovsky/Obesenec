package sk.uniza.fri.slivovsky.semestralnapraca.score

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ListItemBinding
import sk.uniza.fri.slivovsky.semestralnapraca.playerHistory.HistoryPlayersActivity

/**
 * Adapter pre skore recycler view
 *
 * @property context
 * @property najHraci
 * @property poradoveCislo
 */
class SkoreAdapter(private val context: Context,private val najHraci: List<PlayersModelClass>, private var poradoveCislo: Int = 0) : RecyclerView.Adapter<SkoreAdapter.SkoreViewHolder>() {


    /**
     *
     *View holder v sebe drzi atributy list_itemu2
     * @constructor
     *
     * @param itemView view do ktoreho posielam list_item
     */
    class SkoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {
        val binding = ListItemBinding.bind(itemView)
        private var poradoveCisloText: TextView = binding.poradoveCTextView
        private var menoHraca: TextView = binding.skoreHistoriaTextView
        private var pocetBodov: TextView = binding.bodyTextView
        private var datum: TextView = binding.datumSkoreTextView
        var uid: String = ""
        init {

            binding.itemLayout.setOnLongClickListener(this)
        }

        /**
         * Tato funkcia zabezpeci presun na fragment historie hraca ak hrac podrzi
         * v skoreFragment na meno hraca.
         *
         * @param v View
         * @return
         */
        override fun onLongClick(v: View?): Boolean {

            val intent = Intent(v!!.context, HistoryPlayersActivity::class.java)
            intent.putExtra("uid", uid)
            intent.putExtra("userName", menoHraca.text.toString().trim())
            v.context.startActivity(intent)

            return true
        }

        /**
         * Funkica ktora nastavi textView itemov na hodnoty ziskane z databazy
         *
         * @param poradoveCislo poradove cislo v zozname
         */
        @SuppressLint("SetTextI18n")
        fun nastavHodnoty(hrac: PlayersModelClass, poradoveCislo: Int) {
            menoHraca.text = hrac.name
            poradoveCisloText.text = "$poradoveCislo."
            pocetBodov.text = hrac.score.toString()
            datum.text = hrac.date
            uid = hrac.uid
            if (poradoveCislo == 1){
                binding.imageView2.visibility = View.VISIBLE
            }

        }
    }

    /**
     * Funkcia ktora vyberie item layout do ktoreho sa budu nasledne ukladat data
     *
     * @param parent
     * @param viewType
     * @return
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkoreViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return SkoreViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SkoreViewHolder, position: Int) {

        val hrac = najHraci[position]
        holder.nastavHodnoty(hrac, position+1)

    }

    /**
     * Vrati velkost zoznamu
     *
     * @return vrati velkost zoznamu
     */
    override fun getItemCount(): Int = najHraci.size
}