package es.udc.tfg.delossantos.coronapass.androidApp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord4

data class Vacuna(val nLote: String, val proveedor: String, val timestamp: String, val lugar: String, val cdc: String)

class DosisRecibidas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dosis_recibidas)

        val numDosis: TextView = findViewById(R.id.tv_numeroDosisRecibidas)

        var vacunas: ArrayList<Vacuna> = arrayListOf()
        val intent: Intent = getIntent()
        try{
            val dosis =
                intent.getParcelableArrayListExtra<MedicalRecord4.Dosis>("list") as ArrayList<MedicalRecord4.Dosis>
            numDosis.text = dosis.size.toString()
            var dosisStr = ""
            for (dosisX in dosis) {
                val vaccine: Vacuna = Vacuna(dosisX.nLote.toString(), dosisX.proveedor,
                    dosisX.timestamp, dosisX.lugar, dosisX.cdc)
                vacunas.add(vaccine)
                dosisStr += dosisX.proveedor + " / " + dosisX.timestamp + " / " + dosisX.nLote +
                        " / " + dosisX.lugar + " / " + dosisX.cdc + "\n"
            }
        } catch (e: Exception) {
            Log.d("cospeito", e.message)
        }

        val myAdapter = MyAdapter((vacunas.toTypedArray()))
        val recyclerView: RecyclerView = findViewById(R.id.rv_dosisList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = myAdapter
    }
}