package sk.uniza.fri.slivovsky.semestralnapraca.Skore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.list_item.view.*
import org.w3c.dom.Text
import sk.uniza.fri.slivovsky.semestralnapraca.Databaza.Skore
import sk.uniza.fri.slivovsky.semestralnapraca.R

/**
 * Adapter pre skore recycler view
 *
 * @property context
 * @property najHraci
 * @property poradoveCislo
 */
class SkoreAdapter(private val context: Context,private val najHraci: List<Skore>, private var poradoveCislo: Int = 0) : RecyclerView.Adapter<SkoreAdapter.SkoreViewHolder>() {


    /**
     *
     *
     * @constructor
     *
     *
     * @param itemView
     */
    class SkoreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnLongClickListener {

        var poradoveCisloText: TextView = itemView.poradoveCTextView
        var menoHraca: TextView = itemView.skore_historiaTextView
        var pocetBodov: TextView = itemView.bodyTextView
        var datum: TextView = itemView.datum_skore_TextView

        init {

            itemView.itemLayout.setOnLongClickListener(this)
        }

        /**
         * Tato funkcia zabezpeci presun na fragment historie hraca ak hrac podrzi
         * v skoreFragment na meno hraca.
         *
         * @param v
         * @return
         */
        override fun onLongClick(v: View?): Boolean {

            var bundle = bundleOf("hrac" to menoHraca.text.toString().trim())

            v!!.findNavController().navigate(R.id.action_skoreFragment_to_fragmentHistoriaHraca,bundle)
            return true
        }

        /**
         * Funkica ktora nastavi textView itemov na hodnoty ziskane z databazy
         *
         * @param skore
         * @param poradoveCislo
         */
        fun nastavHodnoty(skore: Skore, poradoveCislo: Int) {
            menoHraca.text = skore.menoHraca
            pocetBodov.text = skore.skore.toString()
            poradoveCisloText.text = poradoveCislo.toString()
            datum.text = skore.datum.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SkoreViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_item, parent, false)

        return SkoreViewHolder(layout)
    }

    override fun onBindViewHolder(holder: SkoreViewHolder, position: Int) {

        poradoveCislo++
        holder.nastavHodnoty(najHraci[position], poradoveCislo)
    }

    /**
     * Vrati velkost zoznamu
     *
     * @return
     */
    override fun getItemCount(): Int = najHraci.size
}