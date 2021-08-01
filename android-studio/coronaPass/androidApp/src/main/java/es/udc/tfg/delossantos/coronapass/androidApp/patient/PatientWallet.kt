package es.udc.tfg.delossantos.coronapass.androidApp.patient

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import es.udc.tfg.delossantos.coronapass.android.R
import es.udc.tfg.delossantos.coronapass.androidApp.*
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.cdc.CdcMain
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.Coronacoin
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord
import es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders.IntroSliderActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
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


class PatientWallet : AppCompatActivity() {

    var walletId : String = "_"
    var idMedico : String = "_"
    var idNacional : String = "_"
    var nombre : String = "_"
    var ap1 : String = "_"
    var ap2 : String = "_"
    var genero : String = "_"
    var fechNac : String = "_"
    var pais : String = "_"
    var contacto : String = "_"

    lateinit var toolbar: ActionBar
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigation_qrCode -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
                es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigation_qrScanner -> {
                    /* Create a Zxing IntentIntegrator and start the QR code scan */
                    val integrator = IntentIntegrator(this)
                    integrator.setRequestCode(IntentIntegrator.REQUEST_CODE)
                    integrator.setOrientationLocked(true)
                    //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    //integrator.setPrompt("Scanning Code")
                    integrator.initiateScan()
                    return@OnNavigationItemSelectedListener true
                }
                es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigation_userProfile -> {
                    return@OnNavigationItemSelectedListener true
                }
                es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigation_cdcProfile -> {
                    //esta imagen es de lo mas cutre que hay, MEJORAR = NAVBAR¿?
                    val intent = Intent(this, CdcMain::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun changePrivateKey(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Introduzca su clave privada")

        // set the custom layout
        val customLayout = getLayoutInflater().inflate(R.layout.dialog_add_account, null);
        val et_new_key: EditText = customLayout.findViewById(R.id.et_add_new_privKey)
        builder.setView(customLayout);

        // add OK and Cancel buttons
        builder.setPositiveButton("OK") { dialog, which ->
            if (et_new_key.text.length == 0) {
                toast("Introduce una clave privada válida")
            } else {
                // user clicked OK
                val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
                    "pasaporte",
                    masterKeyAlias,
                    applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )
                var editor = sharedPref.edit()
                editor.putString("key", et_new_key.text.toString())
                editor.commit()
                val defaultValue = "X"
                var key = sharedPref.getString("key", defaultValue).toString()
                var tv_privKey: TextView = findViewById(R.id.tv_privKeyNew)
                Log.d("cospeito", "WEY: " + key)
                if (key != "X"){
                    val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
                    tv_privKey.setText("Su cartera actual es la: " + credentials.address.toString())
                }else{
                    tv_privKey.setText("You don't have an actual private address. Click to the right to add one.!")
                }
                if (key != "X") {
                    val credentials: Credentials =
                        Credentials.create(ECKeyPair.create(BigInteger(key)))
                    val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                    val sharedPreference: SharedPreferences = EncryptedSharedPreferences.create(
                        "pasaporte",
                        masterKeyAlias,
                        applicationContext,
                        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                    )
                    var editor = sharedPreference.edit()
                    editor.putString("account", credentials.address.toString())
                    editor.putString("pubkey", credentials.ecKeyPair.publicKey.toString())
                    editor.commit()
                    tv_privKey.setText("Your acual account is: " + credentials.address.toString())

                    getBalanceCoronacoins()
                } else {
                    tv_privKey.setText("You don't have an actual private address. Click to the right to add one.!")
                }
            }
        }
        builder.setNegativeButton("Cancel", null)

        // create and show the alert dialog
        val dialog = builder.create()
        dialog.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_patient_wallet)

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.selectedItemId = es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigation_userProfile
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        var ib_changeKey: ImageButton = findViewById(es.udc.tfg.delossantos.coronapass.androidApp.R.id.ib_changePrivateKey)
        ib_changeKey.setOnClickListener {
            changePrivateKey()
        }

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
        var tv_privKey: TextView = findViewById(R.id.tv_privKeyNew)
        Log.d("cospeito", "WEY: " + key)
        if (key != "X"){
            val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
            tv_privKey.setText("Your acual account is: " + credentials.address.toString())
        }else{
            tv_privKey.setText("You don't have an actual private address. Click to the right to add one.!")
        }

        getBalanceCoronacoins()

        val sendCoins: ImageButton = findViewById(R.id.ib_sendCoronacoins)
        sendCoins.setOnClickListener() {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Introduzca la wallet del destinatario y la cantidad de coins a transferir")
            // set the custom layout
            val customLayout = getLayoutInflater().inflate(R.layout.dialog_confirm_send_coin,
                null
            );
            val et_acc: EditText =
                customLayout.findViewById(R.id.et_cf_account)
            val et_cant: EditText =
                customLayout.findViewById(R.id.et_cf_cantidad)
            builder.setView(customLayout);

            // add OK and Cancel buttons
            builder.setPositiveButton("OK") { dialog, which ->
                // user clicked OK
                sendCoronacoins(et_acc.text.toString(), et_cant.text.toString())
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun getBalanceCoronacoins(){
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

            //GETTING BALANCES
            val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
                "pasaporte",
                masterKeyAlias,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            var privKey = sharedPref.getString("key", "none").toString()

            Log.d(
                "cospeito",
                "Getting Balances of account " + Credentials.create(
                    ECKeyPair.create(
                        BigInteger(
                            privKey
                        )
                    )
                ).address
            )
            try {
                val credentials: Credentials = Credentials.create(
                    ECKeyPair.create(
                        BigInteger(
                            privKey
                        )
                    )
                )
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

                //ETHER BALANCE
                val balance: EthGetBalance = web3.ethGetBalance(
                    credentials.address,
                    DefaultBlockParameterName.LATEST
                ).sendAsync().get()
                Log.d("cospeito", "Balance of Ether: " + balance.balance.toString())

                //CORONACOIN BALANCE
                val transactionReceipt2: BigInteger? =
                    medicalRecord.balaceOfCoronacoins(credentials.address)
                        .sendAsync().get()
                Log.d(
                    "cospeito",
                    "Balance of Coronacoins: " + transactionReceipt2.toString()
                )
                val coronacoins: TextView = findViewById(R.id.tv_numberOfCoronacoins)
                coronacoins.setText(transactionReceipt2.toString())

                //web3.shutdown() -  revisar pechar conexións desta maneira
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Error during wallet balances response: " + e.message)
            }
        } catch (e: java.lang.Exception) {
            Log.d("cospeito", "Error during all saving stuff: " + e.message)
        }
    }

    private fun sendCoronacoins(acc: String, amount: String){
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

        //Get priv Key
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val defaultValue = "none"
        var privKey = sharedPref.getString("key", defaultValue).toString()
        if (privKey == "none"){
            toast("You aren't yet into the system")
            //opcion a meter clave privada¿?
        }else {
            //GLOBAL VARS - wip
            var createdWallet = ""
            //GET ETHER
            Log.d("cospeito", "Logging to get Coronacoin address")
            try {
                val credentials: Credentials =
                    Credentials.create(ECKeyPair.create(BigInteger(privKey)))

                //GETTING ADDRESS
                Log.d("cospeito", "Getting coin address: " + createdWallet)
                val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"

                //gasProvider with gasPrice
                val gasProvider = StaticGasProvider(
                    Convert.toWei("20000", Convert.Unit.WEI).toBigInteger(),
                    BigInteger("3000000")
                )

                val medicalRecord = MedicalRecord.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasProvider
                )
                //CORONACOIN BALANCE
                val transactionReceipt2: BigInteger? = medicalRecord.balaceOfCoronacoins(credentials.address).sendAsync().get()
                Log.d(
                    "cospeito",
                    "Balance of Coronacoins of " + credentials.address + ": " + transactionReceipt2.toString()
                )

                if (transactionReceipt2 != null) {
                    if (transactionReceipt2 < BigInteger(amount)){
                        Log.d("cospeito", "You don't have such amount of coins to send")
                        toast("You don't have such amount of coins to send")
                        return
                    }
                }

                val coronacoin_addr: String? =
                    medicalRecord.coronacoin().sendAsync().get()

                //LOAD COIN CONTRACT
                val coronaCoin = Coronacoin.load(
                    coronacoin_addr,
                    web3,
                    credentials,
                    gasProvider
                )

                Log.d("cospeito", "Loading and transferring coins")
                val transactionReceipt0 = coronaCoin.transfer(acc, BigInteger(amount)).sendAsync()
                val result = "Successful transaction. Hash:  ${transactionReceipt0?.get()?.transactionHash} . Block number: ${transactionReceipt0?.get()?.blockNumber}" +
                        " Gas used: ${transactionReceipt0?.get()?.gasUsed}"
                Log.d("cospeito", result)
                Log.d("cospeito", "Coins transferred!!!!!!")
                getBalanceCoronacoins()
            } catch (e: java.lang.Exception) {
                //Display an Error
                Log.d(
                    "cospeito",
                    "Error during wallet creation or either ether transaction: " + e.message
                )
            }
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

    override fun onResume() {
        super.onResume()
        val bottomNavigation: BottomNavigationView =
            findViewById(es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigationView)
        bottomNavigation.selectedItemId =
            es.udc.tfg.delossantos.coronapass.androidApp.R.id.navigation_userProfile
    }
}