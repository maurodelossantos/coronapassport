package es.udc.tfg.delossantos.coronapass.androidApp.cdc

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import java.math.BigInteger
import java.security.Provider
import java.security.Security

class AddReaccionCdc : AppCompatActivity() {
    var tmp: Boolean = true;

    private fun testeaString(editText: EditText) : Boolean {
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
        setContentView(R.layout.activity_add_reaccion)

        val reacc: EditText = findViewById(R.id.et_AddReacc)

        val bt_confirm: Button = findViewById(R.id.bt_addInfoReaccCdc)

        val intent: Intent = getIntent()
        val acc: String = intent.getStringExtra("acc")
        val name: String = intent.getStringExtra("name")
        val tv_acc: TextView = findViewById(R.id.tv_accountAddReacc)
        val tv_name: TextView = findViewById(R.id.tv_nameAddReacc)
        tv_acc.setText(acc)
        tv_name.setText(name)

        reacc.setHint("Introduzca las reacciones adversas identificadas")

        bt_confirm.setOnClickListener() {
            if (testeaString(reacc)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Corrobore concienzudamente todos los datos a introducir en el sistema")

                // set the custom layout
                val customLayout = getLayoutInflater().inflate(R.layout.dialog_confirm_reaccion, null);
                val tv_cf_reaccion: TextView = customLayout.findViewById(R.id.tv_cf_reaccionAdversa)
                tv_cf_reaccion.setText("Reacción adversa: "+ reacc.text.toString())

                builder.setView(customLayout);

                // add OK and Cancel buttons
                builder.setPositiveButton("OK") { dialog, which ->
                    // user clicked OK
                    addReaccionToBlockchain(acc, reacc.text.toString())
                }

                builder.setNegativeButton("Cancel", null)

                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun addReaccionToBlockchain(acc: String, reaccion: String) {
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

            //ADDING REACTION
            Log.d(
                "cospeito",
                "Adding reaction to account: " + acc
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
                if ((key == "X") || (key == "")){
                    toast("You don't have an actual private address. Go back to add one.!")
                }
                val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
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
                val idNac: BigInteger = BigInteger.valueOf(11111111)
                val idMed: BigInteger = BigInteger.valueOf(11111111)

                val transactionReceipt0: TransactionReceipt? = medicalRecord.addReaccion(
                    acc,
                    reaccion
                ).sendAsync().get()
                val result =
                    "Successful transaction. Hash:  ${transactionReceipt0?.transactionHash} . Block number: ${transactionReceipt0?.blockNumber}" +
                            " Gas used: ${transactionReceipt0?.gasUsed}"
                Log.d("cospeito", result)
                Log.d(
                    "cospeito",
                    "Added new reaction to the system, in account: " + acc + "."
                )

                val data: Intent = Intent()
                data.putExtra("result", result)
                setResult(99, data)
                finish()
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Failed adding reaction: " + e.message)
            }
        } catch (e: java.lang.Exception) {
            Log.d("cospeito", "Failed adding reaction: " + e.message)
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