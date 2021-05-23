package sk.uniza.fri.slivovsky.semestralnapraca.Historia

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import kotlinx.android.synthetic.main.list_item.view.poradoveCTextView
import kotlinx.android.synthetic.main.list_item.view.skore_historiaTextView
import kotlinx.android.synthetic.main.list_item2.view.*
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.Skore
import sk.uniza.fri.slivovsky.semestralnapraca.R

/**
 * Adapter pre historia recycler view.
 *
 * @property context
 * @property historiaHraca
 * @property poradoveCislo
 */
class HistoriaAdapter(private val context: Context,private val historiaHraca: List<Skore>, private var poradoveCislo: Int = 0) : RecyclerView.Adapter<HistoriaAdapter.HistoriaViewHolder>() {

    /**
     *
     *
     * @constructor
     *
     *
     * @param itemView
     */
    class HistoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var poradoveCisloText: TextView = itemView.poradoveCTextView
        var pocetBodov: TextView = itemView.skore_historiaTextView
        var datum: TextView = itemView.datum_historiaTextView2

        /**
         * Funkica ktora nastavi textView itemov na hodnoty ziskane z databazy
         *
         * @param skore
         * @param poradoveCislo
         */
        fun nastavHodnoty(skore: Skore, poradoveCislo: Int) {

            pocetBodov.text = skore.skore.toString()
            poradoveCisloText.text = poradoveCislo.toString()
            datum.text = skore.datum.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoriaViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_item2, parent, false)

        return HistoriaViewHolder(layout)
    }

    /**
     * Funkcia ktora zvysi poradove cislo hier a nastavi podla neho hodnoty
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: HistoriaViewHolder, position: Int) {

        poradoveCislo++
        holder.nastavHodnoty(historiaHraca[position], poradoveCislo)
    }

    /**
     * vrati velkost zoznamu
     *
     * @return
     */
    override fun getItemCount(): Int = historiaHraca.size

}