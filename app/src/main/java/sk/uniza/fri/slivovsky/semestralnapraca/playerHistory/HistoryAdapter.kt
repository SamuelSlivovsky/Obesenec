package sk.uniza.fri.slivovsky.semestralnapraca.playerHistory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ListItem2Binding

/**
 * Adapter pre historia recycler view.
 *
 * @property context
 * @property historiaHraca
 * @property poradoveCislo
 */
class HistoryAdapter(
    private val context: Context,
    private val historiaHraca: List<HistoryPlayerModelClass>,
    private var poradoveCislo: Int = 0
) : RecyclerView.Adapter<HistoryAdapter.HistoriaViewHolder>() {

    /**
     * View holder v sebe drzi atributy list_itemu2
     * @constructor
     *
     * @param itemView view do ktoreho posielam list_item2
     */
    class HistoriaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ListItem2Binding.bind(itemView)
        var poradoveCisloText: TextView = binding.poradoveCTextView
        var pocetBodov: TextView = binding.skoreHistoriaTextView
        var datum: TextView = binding.datumHistoriaTextView2
        var mode: TextView = binding.modeTextView


        /**
         * Funkica ktora nastavi textView itemov na hodnoty ziskane z databazy
         *
         * @param skore
         * @param poradoveCislo
         */
        @SuppressLint("SetTextI18n")
        fun nastavHodnoty(skore: HistoryPlayerModelClass, poradoveCislo: Int) {
            pocetBodov.text = skore.score.toString()
            poradoveCisloText.text = "$poradoveCislo."
            datum.text = skore.date
            mode.text = skore.mode

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

        holder.nastavHodnoty(historiaHraca[position], position + 1)
    }

    /**
     * vrati velkost zoznamu
     *
     * @return
     */
    override fun getItemCount(): Int = historiaHraca.size

}