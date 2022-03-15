package sk.uniza.fri.slivovsky.semestralnapraca.Skore

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.Skore
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ListItemBinding

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
        var poradoveCisloText: TextView = binding.poradoveCTextView
        var menoHraca: TextView = binding.skoreHistoriaTextView
        var pocetBodov: TextView = binding.bodyTextView
        var datum: TextView = binding.datumSkoreTextView

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

            val bundle = bundleOf("hrac" to menoHraca.text.toString().trim())

            return true
        }

        /**
         * Funkica ktora nastavi textView itemov na hodnoty ziskane z databazy
         *
         * @param skore Databaza
         * @param poradoveCislo poradove cislo v zozname
         */
        fun nastavHodnoty(hrac: PlayersModelClass, poradoveCislo: Int) {
            menoHraca.text = hrac.name
            poradoveCisloText.text = poradoveCislo.toString() + "."
            pocetBodov.text = hrac.score.toString()
            datum.text = hrac.date
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