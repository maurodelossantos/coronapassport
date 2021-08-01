package es.udc.tfg.delossantos.coronapass.androidApp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val dataSet: Array<Vacuna>) :
RecyclerView.Adapter<MyAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nLote: TextView
        val proveedor: TextView
        val timestamp: TextView
        val lugar: TextView
        val cdc: TextView

        init {
            // Define click listener for the ViewHolder's View.
            nLote = view.findViewById(R.id.nLotedosis)
            proveedor = view.findViewById(R.id.providerDosis)
            timestamp = view.findViewById(R.id.fechaDosis)
            lugar = view.findViewById(R.id.lugarDosis)
            cdc = view.findViewById(R.id.cdc)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.dosislist_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        //viewHolder.textView.text = dataSet[position].toString()
        viewHolder.nLote.text = dataSet[position].nLote
        viewHolder.proveedor.text = dataSet[position].proveedor
        viewHolder.timestamp.text = dataSet[position].timestamp
        viewHolder.lugar.text = dataSet[position].lugar
        viewHolder.cdc.text = dataSet[position].cdc

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}