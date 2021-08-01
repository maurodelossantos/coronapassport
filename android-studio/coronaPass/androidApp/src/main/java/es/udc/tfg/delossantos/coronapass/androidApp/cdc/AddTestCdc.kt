package es.udc.tfg.delossantos.coronapass.androidApp.cdc

import android.content.Context
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

class AddTestCdc : AppCompatActivity() {
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
        setContentView(R.layout.activity_add_test_cdc)

        val tipoTest: EditText = findViewById(R.id.et_tipoAddTest)
        val resultadoTest: EditText = findViewById(R.id.et_resultadoAddTest)
        val timestampTest: EditText = findViewById(R.id.et_timestampAddTest)
        val lugarTest: EditText = findViewById(R.id.et_lugarAddTest)
        val idTest: EditText = findViewById(R.id.et_idAddTest)

        val bt_confirm: Button = findViewById(R.id.bt_addInfoTestCdc)

        val intent: Intent = getIntent()
        val acc: String = intent.getStringExtra("acc")
        val name: String = intent.getStringExtra("name")
        val tv_acc: TextView = findViewById(R.id.tv_accountAddTest)
        val tv_name: TextView = findViewById(R.id.tv_nameAddTest)
        tv_acc.setText(acc)
        tv_name.setText(name)

        tipoTest.setHint("Introduzca el tipo de prueba realizada")
        resultadoTest.setHint("Introduzca el resultado de la prueba")
        timestampTest.setHint("Introduzca el día de la prueba")
        lugarTest.setHint("Introduzca el lugar de la prueba")
        idTest.setHint("Introduzca el identificador de la prueba")

        bt_confirm.setOnClickListener() {
            if (testeaString(tipoTest) && testeaString(resultadoTest) && testeaString(timestampTest)
                && testeaString(lugarTest)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Corrobore concienzudamente todos los datos a introducir en el sistema")

                // set the custom layout
                val customLayout = getLayoutInflater().inflate(R.layout.dialog_confirm_test, null);
                val tv_cf_tipo: TextView = customLayout.findViewById(R.id.tv_cf_tipoTest)
                val tv_cf_result: TextView = customLayout.findViewById(R.id.tv_cf_resultado)
                val tv_cf_timestamp: TextView = customLayout.findViewById(R.id.tv_cf_timestampT)
                val tv_cf_lugar: TextView = customLayout.findViewById(R.id.tv_cf_lugarT)
                val tv_cf_id: TextView = customLayout.findViewById(R.id.tv_cf_idT)
                tv_cf_tipo.setText("Tipo de prueba: "+ tipoTest.text.toString())
                tv_cf_result.setText("Resultado: "+ resultadoTest.text.toString())
                tv_cf_timestamp.setText("Timestamp: "+ timestampTest.text.toString())
                tv_cf_lugar.setText("Lugar: "+ lugarTest.text.toString())
                tv_cf_id.setText("Identificador: "+ idTest.text.toString())

                builder.setView(customLayout);

                // add OK and Cancel buttons
                builder.setPositiveButton("OK") { dialog, which ->
                    // user clicked OK
                    addTestToBlockchain(acc, tipoTest.text.toString(), resultadoTest.text.toString(),
                        timestampTest.text.toString(),
                        lugarTest.text.toString(), idTest.text.toString())
                }

                builder.setNegativeButton("Cancel", null)

                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun addTestToBlockchain(acc: String, tipo: String, resultado: String, times: String, lugar: String, id:String) {
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

            //ADDING TEST
            Log.d(
                "cospeito",
                "Adding new test to account: " + acc
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
                if ((key == "X") or (key == "")){
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

                //METHODS CALLS
                val transactionReceipt0: TransactionReceipt? = medicalRecord.addTest(
                    acc,
                    BigInteger(id), tipo, lugar, times, resultado
                ).sendAsync().get()
                val result =
                    "Successful transaction. Hash:  ${transactionReceipt0?.transactionHash} . Block number: ${transactionReceipt0?.blockNumber}" +
                            "Gas used: ${transactionReceipt0?.gasUsed}"
                Log.d("cospeito", result)
                Log.d(
                    "cospeito",
                    "Added new test to the system, in account: " + acc + "."
                )

                val data: Intent = Intent()
                data.putExtra("result", result)
                setResult(89, data)
                finish()
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Failed adding test: " + e.message)
            }
        }catch (e: java.lang.Exception) {
            Log.d("cospeito", "Failed adding test: " + e.message)
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