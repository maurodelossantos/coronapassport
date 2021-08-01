package es.udc.tfg.delossantos.coronapass.androidApp.patient

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import es.udc.tfg.delossantos.coronapass.android.R
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.Coronacoin
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord
import es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders.IntroSliderActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.io.File
import java.math.BigInteger
import java.security.Provider
import java.security.Security


class AddPersonalData : AppCompatActivity() {

    var tmp: Boolean = true;

    fun testeaString(editText: EditText) : Boolean {
        if (editText.text.toString().length == 0) {
            editText.setError("Este campo no puede estar vacío");
            tmp = false
            return false
        }else{
            return true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_personal_data)


        val et_idNacional: EditText = findViewById(R.id.et_idNacional)
        val et_idMedico: EditText = findViewById(R.id.et_idMedico)
        val et_nombre: EditText = findViewById(R.id.et_nombre)
        val et_ap1: EditText = findViewById(R.id.et_ap1)
        val et_ap2: EditText = findViewById(R.id.et_ap2)
        val et_fechaNac: EditText = findViewById(R.id.et_fechaNac)
        val et_genero: EditText = findViewById(R.id.et_genero)
        val et_pais: EditText = findViewById(R.id.et_pais)
        val et_contacto: EditText = findViewById(R.id.et_contacto)

        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        et_idMedico.setHint(sharedPref.getString("idMedico", "ID Médico").toString())
        et_idNacional.setHint(sharedPref.getString("idNacional", "ID Nacional").toString())
        et_nombre.setHint(sharedPref.getString("nombre", "Nombre").toString())
        et_ap1.setHint(sharedPref.getString("ap1", "Apellido 1").toString())
        et_ap2.setHint(sharedPref.getString("ap2", "Apellido 2").toString())
        et_genero.setHint(sharedPref.getString("genero", "Género").toString())
        et_pais.setHint(sharedPref.getString("pais", "País").toString())
        et_fechaNac.setHint(sharedPref.getString("fechaNac", "Nacimiento: ddmmyyyy").toString())
        et_contacto.setHint(sharedPref.getString("contacto", "Contacto").toString())

        val bt_okData: Button = findViewById(R.id.bt_okDataCdc);
        bt_okData.setOnClickListener() {
            tmp = true;
            val intent: Intent = Intent(this, AddPersonalData::class.java)
            if (testeaString(et_idNacional)){ intent.putExtra(
                "IDNac",
                et_idNacional.text.toString()
            )}
            if (testeaString(et_idMedico)){ intent.putExtra("IDMed", et_idMedico.text.toString())}
            if (testeaString(et_nombre)){ intent.putExtra("Nombre", et_nombre.text.toString())}
            if (testeaString(et_ap1)){ intent.putExtra("Ap1", et_ap1.text.toString())}
            if (testeaString(et_ap2)){ intent.putExtra("Ap2", et_ap2.text.toString())}
            if (testeaString(et_genero)){ intent.putExtra("Genero", et_genero.text.toString())}
            if (testeaString(et_fechaNac)){ intent.putExtra("FechaNac", et_fechaNac.text.toString())}
            if (testeaString(et_pais)){intent.putExtra("Pais", et_pais.text.toString())}
            if (testeaString(et_contacto)){intent.putExtra("Contacto", et_contacto.text.toString())}

            if (tmp) {
                setResult(0, intent)
                finish()
            }
        }
    }
}