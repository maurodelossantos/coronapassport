package es.udc.tfg.delossantos.coronapass.androidApp.cdc

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.zxing.integration.android.IntentIntegrator
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord4
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tuples.generated.Tuple3
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import java.security.Provider
import java.security.Security

class CdcMain : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cdc_main)

        val bt_Scan: Button = findViewById(R.id.bt_scanCDC)
        bt_Scan.setOnClickListener() {
            /* Create a Zxing IntentIntegrator and start the QR code scan */
            val integrator = IntentIntegrator(this)
            integrator.setRequestCode(IntentIntegrator.REQUEST_CODE)
            integrator.setOrientationLocked(true)
            //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
            //integrator.setPrompt("Scanning Code")
            integrator.initiateScan()
        }

        val bt_perfil: Button = findViewById(R.id.bt_profileCdc)
        bt_perfil.setOnClickListener() {
            val intent = Intent(this, AddProfileDataCdc::class.java)
            startActivityForResult(intent, 0)
        }

        val et_accountCdcGetInfo: EditText = findViewById(R.id.et_accountGetCdc)

        val bt_cdcInfo: Button = findViewById(R.id.bt_otherCdcInfo)
        bt_cdcInfo.setOnClickListener() {
            if (et_accountCdcGetInfo.text.toString().length == 0) {
                et_accountCdcGetInfo.setError("Este campo no puede estar vacío");
            }else if (et_accountCdcGetInfo.text.toString().length != 42) {
                et_accountCdcGetInfo.setError("La dirección no parece correcta");
            }else{
                getCdcInfo(et_accountCdcGetInfo.text.toString())
            }
        }
    }

    private fun getCdcInfo(addr: String) {
        try {
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
            Log.d(
                "cospeito",
                "Getting CDC record information about account " + addr
            )
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
                if (key == "X") {
                    toast("You don't have an actual private address. Go back to add one.!")
                }
                val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
                Log.d("cospeito", "ACCOUNT: " + credentials.address.toString())
                val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"
                val gasProvider = StaticGasProvider(
                    Convert.toWei("20000", Convert.Unit.WEI).toBigInteger(),
                    BigInteger("3000000")
                )

                val medicalRecord = MedicalRecord4.load(
                    contractAddress,
                    web3,
                    credentials,
                    gasProvider
                )

                val transactionReceipt0: Tuple3<String, String, String>? =
                    medicalRecord.getCdcInfo(addr).sendAsync().get()

                if (transactionReceipt0 != null) {
                    Log.d("cospeito", transactionReceipt0.component1())
                    Log.d("cospeito", transactionReceipt0.component2())
                    Log.d("cospeito", transactionReceipt0.component3())
                    val tv: TextView = findViewById(R.id.tv_resultScanned)
                    tv.setText(transactionReceipt0.component1() + " / " + transactionReceipt0.component2() +
                            " / " + transactionReceipt0.component3())
                }
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Failed reading cdc record: " + e.message)
                toast("No hay información CDC para esta dirección")
            }
        }catch (e: java.lang.Exception) {
            Log.d("cospeito", "Failed reading all cdc record: " + e.message)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IntentIntegrator.REQUEST_CODE) { //si venimos de leer QR
            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            if (result != null) {
                if (result.getContents() == null) {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
                } else {
                    val intent = Intent(this, ScanPassportCdc::class.java)
                    intent.putExtra("info", result.contents)
                    startActivityForResult(intent, 69)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        } else if (requestCode == 69) { //transaccion succesfull
            val returnedResult = data?.extras
            val tv_result: TextView = findViewById(R.id.tv_resultScanned)
            if (returnedResult != null) {
                tv_result.setText(returnedResult.get("result").toString())
            }
        } else if (requestCode == 79) {
            val returnedResult = data?.extras
            val tv_result: TextView = findViewById(R.id.tv_resultScanned)
            if (returnedResult != null) {
                tv_result.setText(returnedResult.get("result").toString())
            }
        } else if (requestCode == 89) {
            val returnedResult = data?.extras
            val tv_result: TextView = findViewById(R.id.tv_resultScanned)
            if (returnedResult != null) {
                tv_result.setText(returnedResult.get("result").toString())
            }
        } else if (requestCode == 99) {
            val returnedResult = data?.extras
            val tv_result: TextView = findViewById(R.id.tv_resultScanned)
            if (returnedResult != null) {
                tv_result.setText(returnedResult.get("result").toString())
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
}