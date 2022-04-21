package sk.uniza.fri.slivovsky.semestralnapraca.playerHistory

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import sk.uniza.fri.slivovsky.semestralnapraca.R
import sk.uniza.fri.slivovsky.semestralnapraca.databinding.ListItem3Binding

/**
 * Adapter pre historia recycler view.
 *
 * @property context
 * @property historiaHraca
 * @property poradoveCislo
 */
class HistoryPlayersAdapter(
    private val context: Context,
    private val historiaHraca: List<HistoryPlayerModelClass>,
    private var poradoveCislo: Int = 0
) : RecyclerView.Adapter<HistoryPlayersAdapter.HistoryPlayersViewHolder>() {

    /**
     * View holder v sebe drzi atributy list_itemu2
     * @constructor
     *
     * @param itemView view do ktoreho posielam list_item2
     */
    class HistoryPlayersViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val binding = ListItem3Binding.bind(itemView)
        private var poradoveCisloText: TextView = binding.poradoveCTextView
        private var pocetBodov: TextView = binding.skoreHistoriaTextView
        private var datum: TextView = binding.datumHistoriaTextView2
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryPlayersViewHolder {
        val layout = LayoutInflater.from(parent.context).inflate(R.layout.list_item3, parent, false)

        return HistoryPlayersViewHolder(layout)
    }

    /**
     * Funkcia ktora zvysi poradove cislo hier a nastavi podla neho hodnoty
     *
     * @param holder
     * @param position
     */
    override fun onBindViewHolder(holder: HistoryPlayersViewHolder, position: Int) {

        holder.nastavHodnoty(historiaHraca[position], position + 1)
    }

    /**
     * vrati velkost zoznamu
     *
     * @return
     */
    override fun getItemCount(): Int = historiaHraca.size

}