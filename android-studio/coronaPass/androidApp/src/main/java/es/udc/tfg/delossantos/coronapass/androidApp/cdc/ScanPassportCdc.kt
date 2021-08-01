package es.udc.tfg.delossantos.coronapass.androidApp.cdc

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import es.udc.tfg.delossantos.coronapass.androidApp.DosisRecibidas
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.ReaccionesAdversas
import es.udc.tfg.delossantos.coronapass.androidApp.TestsRealizados
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord3
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
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import java.security.Provider
import java.security.Security
import java.util.concurrent.Future

class ScanPassportCdc : AppCompatActivity() {

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
        if ((key == "X") or (key == "")){
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
            Log.d("cospeito", "ACCOUNT: " + acc)
            val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"
            //gasProvider with gasPrice
            val gasProvider = StaticGasProvider(
                Convert.toWei("20000", Convert.Unit.WEI).toBigInteger(),
                BigInteger("3000000")
            )

            val medicalRecord = MedicalRecord3.load(
                contractAddress,
                web3,
                credentials,
                gasProvider
            )

            val transactionReceipt0: Boolean = medicalRecord.isPatient(acc).sendAsync().get()
            return transactionReceipt0
        } catch (e: java.lang.Exception) {
            //Display an Error
            Log.d("cospeito", "Error during isPatient response: " + e.message)
            return false
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scan_passport_cdc)

        val tv_vacunado: TextView = findViewById(R.id.tv_rellenaVacunado)
        val tv_añadirSis: TextView = findViewById(R.id.tv_añadirSis)

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
        val bt_seeDosis: Button = findViewById(R.id.bt_dosisRec)
        val bt_seeTests: Button = findViewById(R.id.bt_testReal)
        val bt_seeReacc: Button = findViewById(R.id.bt_reaccAd)

        var print10 = ""
        val intentDosis = Intent(this, DosisRecibidas::class.java)
        val intentTests = Intent(this, TestsRealizados::class.java)
        val intentReaccs = Intent(this, ReaccionesAdversas::class.java)

        val bt_addDosis: ImageButton = findViewById(R.id.bt_addDosisCdc)
        val bt_addTest: ImageButton = findViewById(R.id.bt_addTestCdc)
        val bt_addReacc: ImageButton = findViewById(R.id.bt_addReaccCdc)

        val bt_addToBc: ImageButton = findViewById(R.id.ib_addToBc);

        val bt_returnHome: ImageButton = findViewById(R.id.ib_returnHome);
        bt_returnHome.setOnClickListener() {
            finish()
        }

        val intent: Intent = getIntent()
        val infoPasaporte: String = intent.getStringExtra("info")
        val json = JSONObject(infoPasaporte)
        Log.d("cospeito", "isIntoTheSystem " + json.getString("Wallet"))
        if (!intoTheSystem(json.getString("Wallet"))){
            tv_vacunado.setText("NO ESTÁ EN EL SISTEMA")

            bt_addDosis.visibility = View.INVISIBLE
            bt_addTest.visibility = View.INVISIBLE
            bt_addReacc.visibility = View.INVISIBLE


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
        }else {
            tv_vacunado.setText("SÍ ESTÁ EN EL SISTEMA")

            bt_addToBc.visibility = View.INVISIBLE
            tv_añadirSis.visibility = View.INVISIBLE

            val walletJson = json.getString("Wallet")

            try {
                Log.d("cospeito", "Connecting to the Blockchain...")
                //CONNECT TO BLOCKCHAIN NODE
                var web3: Web3j =
                    Web3j.build(HttpService("http://192.168.0.14:9545"))
                //TEST THE CONNECTION
                try {
                    val clientVersion: Web3ClientVersion =
                        web3.web3ClientVersion().sendAsync().get()
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
                    if ((key == "X") || (key == "")){
                        toast("You don't have an actual private address. Go back to add one.!")
                    }
                    val credentials: Credentials =
                        Credentials.create(ECKeyPair.create(BigInteger(key)))
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
                    val result =
                        "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash}. Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                                " Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                    Log.d("cospeito", result)

                    Log.d("cospeito", "Getting free record info about: " + walletJson)
                    val transactionReceipt: Tuple12<BigInteger, BigInteger, String, String, String,
                            String, String, String, String, MutableList<MedicalRecord4.Dosis>,
                            MutableList<MedicalRecord4.Prueba>, MutableList<String>>? =
                            medicalRecord.getFreeRecord(walletJson).sendAsync().get()
                    if (transactionReceipt != null) {
                        Log.d("cospeito", transactionReceipt.component1().toString())
                        Log.d("cospeito", transactionReceipt.component2().toString())
                        Log.d("cospeito", transactionReceipt.component3())
                        Log.d("cospeito", transactionReceipt.component4() + " " +
                                transactionReceipt.component5())
                        Log.d("cospeito", transactionReceipt.component6())
                        Log.d("cospeito", transactionReceipt.component7())
                        Log.d("cospeito", transactionReceipt.component8())
                        Log.d("cospeito", transactionReceipt.component9())
                        tv_idN.setText(transactionReceipt.component1().toString())
                        tv_idM.setText(transactionReceipt.component2().toString())
                        tv_nombre.setText(transactionReceipt.component3())
                        tv_ap1.setText(transactionReceipt.component4() + " " + transactionReceipt.component5())
                        tv_genero.setText(transactionReceipt.component7())
                        tv_pais.setText(transactionReceipt.component8())
                        tv_fechaNac.setText(transactionReceipt.component6())
                        tv_contacto.setText(transactionReceipt.component9())

                        if (transactionReceipt.component10().size > 0) {
                            for (item in transactionReceipt.component10()) {
                                print10 += transactionReceipt.component10().size.toString() + " dosis inoculadas: \n"
                                print10 += "Recibida dosis con número de lote: " + item.nLote + " de " +
                                        item.proveedor + " el " + item.timestamp + " en " + item.lugar + ". \n"
                            }

                            intentDosis.putParcelableArrayListExtra(
                                "list",
                                ArrayList(transactionReceipt.component10())
                            )
                        } else {
                            print10 = "No hay dosis todavía."
                        }
                        Log.d("cospeito", print10)
                        tv_dosis.setText("Número de dosis recibidas: " + transactionReceipt.component10().size)

                        var print11 = ""
                        if (transactionReceipt.component11().size > 0) {
                            print11 += transactionReceipt.component11().size.toString() + " pruebas realizadas: \n"
                            print11 +=
                                "Hecha prueba tipo " + transactionReceipt.component11()[0].tipo + " con resultado " +
                                        transactionReceipt.component11()[0].resultado + " el dia " +
                                        transactionReceipt.component11()[0].timestamp + "\n"
                            intentTests.putParcelableArrayListExtra(
                                "list",
                                ArrayList(transactionReceipt.component11())
                            )
                        } else {
                            print11 = "No hay pruebas todavía."
                        }
                        Log.d("cospeito", print11)
                        tv_tests.setText("Número de tests realizados: " + transactionReceipt.component11().size)

                        var print12 = ""
                        if (transactionReceipt.component12().size > 0) {
                            print12 =
                                "Ha habido " + transactionReceipt.component12().size.toString() + " reacciones adversas."
                            intentReaccs.putStringArrayListExtra(
                                "list",
                                ArrayList(transactionReceipt.component12())
                            )
                        } else {
                            print12 = "No ha habido reacciones adversas."
                        }
                        Log.d("cospeito", print12)
                        tv_reaccs.setText(print12)
                    }
                } catch (e: java.lang.Exception) {
                    Log.d("cospeito", "Failed reading record: " + e.message)
                }
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Failed reading passport case of use: " + e.message)
            }


            bt_addDosis.setOnClickListener() {
                val intent = Intent(this, AddDosisCdc::class.java)
                intent.putExtra("acc", json.getString("Wallet"))
                intent.putExtra("name", tv_nombre.text.toString() + " " + tv_ap1.text.toString())
                startActivityForResult(intent, 79)
            }

            bt_seeDosis.setOnClickListener() {
                startActivity(intentDosis)
            }

            bt_addTest.setOnClickListener() {
                val intent = Intent(this, AddTestCdc::class.java)
                intent.putExtra("acc", json.getString("Wallet"))
                intent.putExtra("name", tv_nombre.text.toString() + " " + tv_ap1.text.toString())
                startActivityForResult(intent, 89)
            }

            bt_seeTests.setOnClickListener() {
                startActivity(intentTests)
            }

            bt_addReacc.setOnClickListener() {
                val intent = Intent(this, AddReaccionCdc::class.java)
                intent.putExtra("acc", json.getString("Wallet"))
                intent.putExtra("name", tv_nombre.text.toString() + " " + tv_ap1.text.toString())
                startActivityForResult(intent, 99)
            }

            bt_seeReacc.setOnClickListener() {
                startActivity(intentReaccs)
            }

        }

        bt_addToBc.setOnClickListener() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Corrobore concienzudamente todos los datos a introducir en el sistema")

            // set the custom layout
            val customLayout = getLayoutInflater().inflate(R.layout.dialog_confirm_bc_data, null);
            val tv_cf_idN: TextView = customLayout.findViewById(R.id.tv_cd_idN)
            val tv_cf_idM: TextView = customLayout.findViewById(R.id.tv_cf_idM)
            val tv_cf_nombre: TextView = customLayout.findViewById(R.id.tv_cd_nombre)
            val tv_cf_ap1: TextView = customLayout.findViewById(R.id.tv_cf_ap1)
            val tv_cf_fechaNac: TextView = customLayout.findViewById(R.id.tv_cf_fechaNac)
            val tv_cf_gen: TextView = customLayout.findViewById(R.id.tv_cf_gen)
            val tv_cf_pais: TextView = customLayout.findViewById(R.id.tv_cf_pais)
            val tv_cf_contacto: TextView = customLayout.findViewById(R.id.tv_cf_contac)
            tv_cf_idN.setText("ID Nacional: "+ json.getString("IDNac"))
            tv_cf_idM.setText("ID Médico: "+ json.getString("IDMed"))
            tv_cf_nombre.setText("Nombre: "+ json.getString("Nombre"))
            tv_cf_ap1.setText("Apellidos: "+ json.getString("Ap1") + " " + json.getString("Ap2"))
            tv_cf_fechaNac.setText("FechaNac: "+ json.getString("FechaNac"))
            tv_cf_gen.setText("Género: "+ json.getString("Genero"))
            tv_cf_pais.setText("Pais: "+ json.getString("Pais"))
            tv_cf_contacto.setText("Contacto: "+ json.getString("Contacto"))
            builder.setView(customLayout);

            // add OK and Cancel buttons
            builder.setPositiveButton("OK") { dialog, which ->
                // user clicked OK
                addToBlockchain(json.getString("Wallet"), json.getString("IDNac"),
                    json.getString("IDMed"), json.getString("Nombre"),
                    json.getString("Ap1"), json.getString("Ap2"),
                    json.getString("FechaNac"), json.getString("Genero"),
                    json.getString("Pais"), json.getString("Contacto"))
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun addToBlockchain(
        dir: String,
        idN: String,
        idM: String,
        nombre: String,
        ap1: String,
        ap2: String,
        fechaNac: String,
        gen: String,
        pais: String,
        contacto: String
    ) {
        var walletJson = dir

        try {
            Log.d("cospeito", "Connecting to the Blockchain...")
            //CONNECT TO BLOCKCHAIN NODE
            var web3: Web3j =
                Web3j.build(HttpService("http://192.168.0.14:9545")) //wip with exceptions: timeouts, spinner and DESCONNECT

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

            //ADDING RECORD
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
                if ((key == "X") || (key == "")){
                    toast("You don't have an actual private address. Go back to add one.!")
                }
                val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
                Log.d(
                    "cospeito",
                    "Adding medical record of account:" + walletJson + " to the system. Sending transaction from account: " + credentials.address)
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

                // id's casted to BigIntegers
                val idNac = BigInteger(idN)
                val idMed = BigInteger(idM)

                val transactionReceipt0: TransactionReceipt? = medicalRecord.addRecord(
                    idNac, idMed, walletJson,
                    nombre, ap1, ap2, fechaNac, gen, pais, contacto
                ).sendAsync().get()
                val result =
                    "Successful transaction. Hash:  ${transactionReceipt0?.transactionHash} .Block number: ${transactionReceipt0?.blockNumber}" +
                            " Gas used: ${transactionReceipt0?.gasUsed}"
                Log.d("cospeito", result)

                val data: Intent = Intent()
                data.putExtra("result", result)
                setResult(69, data)
                finish()
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Failed adding record: " + e.message)
            }
        } catch (e: java.lang.Exception) {
            Log.d("cospeito", "Failed w/ al process of adding record: " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 69) {
            val data: Intent = Intent()
            data.putExtra("result", data.dataString)
            setResult(69, data)
            finish()
        } else if (requestCode == 79) {
            val returnedResult = data?.extras
            var result: String = ""
            if (returnedResult != null) {
                result = returnedResult.get("result").toString()
            }
            val data: Intent = Intent()
            data.putExtra("result", result)
            setResult(79, data)
            finish()
        }else if (requestCode == 89) {
            val returnedResult = data?.extras
            var result: String = ""
            if (returnedResult != null) {
                result = returnedResult.get("result").toString()
            }
            val data: Intent = Intent()
            data.putExtra("result", result)
            setResult(89, data)
            finish()
        }else if (requestCode == 99) {
            val returnedResult = data?.extras
            var result: String = ""
            if (returnedResult != null) {
                result = returnedResult.get("result").toString()
            }
            val data: Intent = Intent()
            data.putExtra("result", result)
            setResult(89, data)
            finish()
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
