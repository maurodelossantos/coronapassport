package es.udc.tfg.delossantos.coronapass.androidApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord4

//copiar o modelo de dosis para estas clases
class TestsRealizados : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tests_realizados)
        val tv_dosis: TextView = findViewById(R.id.tv_testsRealizadosDetail)
        val intent: Intent = getIntent()
        try {
            val dosis =
                intent.getParcelableArrayListExtra<MedicalRecord4.Prueba>("list") as ArrayList<MedicalRecord4.Prueba>
            var dosisStr = ""
            for (dosisX in dosis) {
                dosisStr += dosisX.tipo + " / " + dosisX.resultado + " / " + dosisX.timestamp + " / " + dosisX.lugar + " / " + dosisX.idPrueba + " / " +  dosisX.cdc + "\n"
            }
            tv_dosis.setText(dosisStr)
        } catch (e:Exception) {
            tv_dosis.setText("No hay o algo ha ido mal.")
        }
    }
}