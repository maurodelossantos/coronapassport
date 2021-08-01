package es.udc.tfg.delossantos.coronapass.androidApp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

//copiar o modelo de dosis para estas clases
class ReaccionesAdversas : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reacciones_adversas)
        val tv_dosis: TextView = findViewById(R.id.tv_reaccsAdversasDetail)
        val intent: Intent = getIntent()
        try{
            val dosis = intent.getStringArrayListExtra("list") as ArrayList<String>
            var dosisStr = ""
            for (dosisX in dosis) {
                dosisStr += dosisX.toString() + "\n"
            }
            tv_dosis.setText(dosisStr)
        } catch (e: Exception) {
            tv_dosis.setText("No hay o algo ha ido mal.")
        }
    }
}