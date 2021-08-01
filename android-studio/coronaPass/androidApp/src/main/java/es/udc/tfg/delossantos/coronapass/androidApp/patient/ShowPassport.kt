package es.udc.tfg.delossantos.coronapass.androidApp.patient

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import es.udc.tfg.delossantos.coronapass.android.R
import es.udc.tfg.delossantos.coronapass.androidApp.DosisRecibidas
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.ReaccionesAdversas
import es.udc.tfg.delossantos.coronapass.androidApp.TestsRealizados
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord4
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tuples.generated.Tuple12
import java.io.File
import java.math.BigInteger
import java.security.Provider
import java.security.Security
import java.util.concurrent.Future

class ShowPassport : AppCompatActivity() {
    private fun intoTheSystem(acc: String) : Boolean {
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val defaultValue = "X"
        var key = sharedPref.getString("key", defaultValue).toString()
        if (key == "X"){
            toast("You don't have an actual private address. Go back to add one.!")
        }

        Log.d("cospeito", "Connecting to the Blockchain...")
        //CONNECT TO BLOCKCHAIN NODE
        var web3: Web3j =
            Web3j.build(HttpService("http://192.168.0.14:9545"))
        //TEST THE CONNECTION
        try {
            val clientVersion: Web3ClientVersion = web3.web3ClientVersion().sendAsync().get()
            if (!clientVersion.hasError()) {
                //Connected
                Log.d("cospeito", "Connected to the Blockchain!")
            } else {
                //Show Error
                Log.d("cospeito", "Error ocurred during the connection attempt.")
            }
        } catch (e: Exception) {
            //Show Error
            Log.d("cospeito", "Exception thrown in connection attempt: " + e.message)
        }

        Log.d("cospeito", "Setting up Bouncy Castle")
        setupBouncyCastle();

        try {
            val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
            Log.d("cospeito", "ACCOUNT: " + credentials.address.toString())
            val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"
            val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
            val gasPrice: BigInteger = BigInteger.valueOf(4300000)

            val medicalRecord = MedicalRecord.load(
                contractAddress,
                web3,
                credentials,
                gasLimit,
                gasPrice
            )

            val transactionReceipt0: Boolean = medicalRecord.isPatient(acc).sendAsync().get()
            return transactionReceipt0
        } catch (e: java.lang.Exception) {
            Log.d("cospeito", "Error during isPatient response: " + e.message)
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_passport)

        val tv_vacunado: TextView = findViewById(R.id.tv_rellenaVacunado)

        val tv_idN: TextView = findViewById(R.id.tv_idNac)
        val tv_idM: TextView = findViewById(R.id.tv_idMedico)
        val tv_nombre: TextView = findViewById(R.id.tv_Nombre)
        val tv_ap1: TextView = findViewById(R.id.tv_idAp1)
        val tv_genero: TextView = findViewById(R.id.tv_idGenero)
        val tv_pais: TextView = findViewById(R.id.tv_idPais)
        val tv_fechaNac: TextView = findViewById(R.id.tv_idFechaNac)
        val tv_contacto: TextView = findViewById(R.id.tv_idContacto)
        val tv_dosis: TextView = findViewById(R.id.tv_dosis)
        val tv_tests: TextView = findViewById(R.id.tv_tests)
        val tv_reaccs: TextView = findViewById(R.id.tv_reacciones)
        val bt_dosis: Button = findViewById(R.id.bt_dosisRec)
        val bt_tests: Button = findViewById(R.id.bt_testReal)
        val bt_reaccs: Button = findViewById(R.id.bt_reaccAd)

        var print10 = ""
        val intentDosis = Intent(this, DosisRecibidas::class.java)
        val intentTests = Intent(this, TestsRealizados::class.java)
        val intentReaccs = Intent(this, ReaccionesAdversas::class.java)

        val intent: Intent = getIntent()
        val infoPasaporte: String = intent.getStringExtra("info")

        val json = JSONObject(infoPasaporte)
        val walletJson = json.getString("Wallet")
        if (!intoTheSystem(json.getString("Wallet"))){
            tv_vacunado.setText("NO ESTÁ EN EL SISTEMA")

            tv_dosis.setText("No hay constancia de dosis recibidas")
            tv_tests.setText("No hay constancia de tests realizados")
            tv_reaccs.setText("No hay constancia de reacciones adversas")
            tv_idN.setText(json.getString("IDNac"))
            tv_idM.setText(json.getString("IDMed"))
            tv_nombre.setText(json.getString("Nombre"))
            tv_ap1.setText(json.getString("Ap1") + " " + json.getString("Ap2"))
            tv_genero.setText(json.getString("Genero"))
            tv_pais.setText(json.getString("Pais"))
            tv_fechaNac.setText(json.getString("FechaNac"))
            tv_contacto.setText(json.getString("Contacto"))
        }else{
            tv_vacunado.setText("SÍ ESTÁ EN EL SISTEMA")

            try{
                Log.d("cospeito", "Connecting to the Blockchain...")
                //CONNECT TO BLOCKCHAIN NODE
                var web3: Web3j =
                    Web3j.build(HttpService("http://192.168.0.14:9545"))
                //TEST THE CONNECTION
                try {
                    val clientVersion: Web3ClientVersion = web3.web3ClientVersion().sendAsync().get()
                    if (!clientVersion.hasError()) {
                        //Connected
                        Log.d("cospeito", "Connected to the Blockchain!")
                    } else {
                        //Show Error
                        Log.d("cospeito", "Error ocurred during the connection attempt.")
                    }
                } catch (e: Exception) {
                    //Show Error
                    Log.d("cospeito", "Exception thrown in connection attempt: " + e.message)
                }

                Log.d("cospeito", "Setting up Bouncy Castle")
                setupBouncyCastle();

                //GETTING RECORD
                Log.d("cospeito", "Getting record information about account " + walletJson)
                try {
                    val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                    val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
                        "pasaporte",
                        masterKeyAlias,
                        applicationContext,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                    val defaultValue = "X"
                    var key = sharedPref.getString("key", defaultValue).toString()
                    if ((key == "X") or (key == "")){
                        toast("You don't have an actual private address. Go back to add one.!")
                    }
                    val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
                    val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"
                    val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
                    val gasPrice: BigInteger = BigInteger.valueOf(4300000)

                    val medicalRecord = MedicalRecord4.load(
                        contractAddress,
                        web3,
                        credentials,
                        gasLimit,
                        gasPrice
                    )

                    val transactionReceipt0: Future<TransactionReceipt>? = medicalRecord.getRecord(
                        walletJson
                    ).sendAsync()
                    val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                            "Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                    Log.d("cospeito", result)

                    Log.d("cospeito", "Getting free record info")
                    val transactionReceipt: Tuple12<BigInteger, BigInteger, String, String, String,
                            String, String, String, String, MutableList<MedicalRecord4.Dosis>,
                            MutableList<MedicalRecord4.Prueba>, MutableList<String>>? =
                        medicalRecord.getFreeRecord(walletJson).sendAsync().get()
                    if (transactionReceipt != null) {
                        Log.d("cospeito", transactionReceipt.component1().toString())
                        Log.d("cospeito", transactionReceipt.component2().toString())
                        Log.d("cospeito", transactionReceipt.component3())
                        Log.d("cospeito", transactionReceipt.component4())
                        Log.d("cospeito", transactionReceipt.component5())
                        Log.d("cospeito", transactionReceipt.component6())
                        Log.d("cospeito", transactionReceipt.component7())
                        Log.d("cospeito", transactionReceipt.component8())
                        Log.d("cospeito", transactionReceipt.component9())
                        tv_idN.setText(transactionReceipt.component1().toString())
                        tv_idM.setText(transactionReceipt.component2().toString())
                        tv_nombre.setText(transactionReceipt.component3())
                        tv_ap1.setText(transactionReceipt.component4() + " " + transactionReceipt.component5())
                        tv_genero.setText(transactionReceipt.component6())
                        tv_pais.setText(transactionReceipt.component7())
                        tv_fechaNac.setText(transactionReceipt.component8())
                        tv_contacto.setText(transactionReceipt.component9())

                        if (transactionReceipt.component10().size > 0){
                            for(item in transactionReceipt.component10()){
                            print10 += "Recibida dosis con número de lote: " + item.nLote + " de " +
                                    item.proveedor + " el " + item.timestamp + " en " + item.lugar + ". "
                            }
                            intentDosis.putParcelableArrayListExtra("list", ArrayList(transactionReceipt.component10()))
                        }else{
                            print10 ="No hay dosis todavía."
                        }
                        Log.d("cospeito", print10)
                        tv_dosis.setText("Número de dosis recibidas: " + transactionReceipt.component10().size)

                        var print11 = ""
                        if (transactionReceipt.component11().size > 0){
                            print11 = "Hecha prueba tipo " + transactionReceipt.component11()[0].tipo +
                                    " con resultado " + transactionReceipt.component11()[0].resultado +
                                    " el dia " + transactionReceipt.component11()[0].timestamp
                            intentTests.putParcelableArrayListExtra("list", ArrayList(transactionReceipt.component11()))
                        }else{print11 ="No hay pruebas todavía."}
                        Log.d("cospeito", print11)
                        tv_tests.setText("Número de tests realizados: " + transactionReceipt.component11().size)

                        var print12 = ""
                        if (transactionReceipt.component12().size > 0){
                            print12 = "Ha habido " + transactionReceipt.component12().size + " reacciones adversas."
                            intentReaccs.putStringArrayListExtra("list", ArrayList(transactionReceipt.component12()))
                        }else{print12 ="No ha habido reacciones adversas."}
                        Log.d("cospeito", print12)
                        tv_reaccs.setText(print12)
                    }
                }catch (e: java.lang.Exception){
                    Log.d("cospeito", "Failed reading record: " + e.message)
                }
            }
            catch (e: java.lang.Exception){
                Log.d("cospeito", "Failed reading passport case of use: " + e.message)
            }
        }

        val bt_returnHome: ImageButton = findViewById(R.id.ib_returnHome);
        bt_returnHome.setOnClickListener() {
            finish()
        }

        bt_dosis.setOnClickListener(){
            startActivity(intentDosis)
        }

        bt_tests.setOnClickListener(){
            startActivity(intentTests)
        }

        bt_reaccs.setOnClickListener(){
            startActivity(intentReaccs)
        }
    }

    //CRYPTOGRAPHIC REQUIRED FUNCTION
    private fun setupBouncyCastle() {
        val provider: Provider = Security.getProvider(BouncyCastleProvider.PROVIDER_NAME)
            ?: // Web3j will set up a provider  when it's used for the first time.
            return
        if (provider.javaClass.equals(BouncyCastleProvider::class.java)) {
            return
        }
        //There is a possibility  the bouncy castle registered by android may not have all ciphers
        //so we  substitute with the one bundled in the app.
        Security.removeProvider(BouncyCastleProvider.PROVIDER_NAME)
        Security.insertProviderAt(BouncyCastleProvider(), 1)
    }
}