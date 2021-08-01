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
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.MedicalRecord3
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.math.BigInteger
import java.security.Provider
import java.security.Security

class AddDosisCdc : AppCompatActivity() {

    var tmp: Boolean = true

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
        setContentView(R.layout.activity_add_dosis_cdc)

        val nLote: EditText = findViewById(R.id.et_tipoAddTest)
        val provider: EditText = findViewById(R.id.et_resultadoAddTest)
        val timestampDosis: EditText = findViewById(R.id.et_timestampAddTest)
        val lugar: EditText = findViewById(R.id.et_lugarAddTest)

        val bt_confirm: Button = findViewById(R.id.bt_addInfoTestCdc)

        val intent: Intent = getIntent()
        val acc: String = intent.getStringExtra("acc")
        val name: String = intent.getStringExtra("name")
        val tv_acc: TextView = findViewById(R.id.tv_accountAddTest)
        val tv_name: TextView = findViewById(R.id.tv_nameAddTest)
        tv_acc.setText(acc)
        tv_name.setText(name)

        nLote.setHint("Introduzca el número de lote")
        provider.setHint("Introduzca el proveedor de la vacuna")
        timestampDosis.setHint("Introduzca el día de la inoculación")
        lugar.setHint("Introduzca el lugar de la inoculación")

        bt_confirm.setOnClickListener() {
            if (testeaString(nLote) && testeaString(provider) && testeaString(timestampDosis)
                && testeaString(lugar)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Corrobore concienzudamente todos los datos a introducir en el sistema")

                // set the custom layout
                val customLayout = getLayoutInflater().inflate(R.layout.dialog_confirm_dosis, null);
                val tv_cf_nLote: TextView = customLayout.findViewById(R.id.tv_cf_nLote)
                val tv_cf_proveedor: TextView = customLayout.findViewById(R.id.tv_cf_proveedor)
                val tv_cf_timestamp: TextView = customLayout.findViewById(R.id.tv_cf_timestamp)
                val tv_cf_lugar: TextView = customLayout.findViewById(R.id.tv_cf_lugar)
                tv_cf_nLote.setText("Número de lote: "+ nLote.text.toString())
                tv_cf_proveedor.setText("Proveedor: "+ provider.text.toString())
                tv_cf_timestamp.setText("Fecha: "+ timestampDosis.text.toString())
                tv_cf_lugar.setText("Lugar: "+ lugar.text.toString())
                builder.setView(customLayout);

                // add OK and Cancel buttons
                builder.setPositiveButton("OK") { dialog, which ->
                    // user clicked OK
                    addDosisToBlockchain(acc, nLote.text.toString(), provider.text.toString(),
                        timestampDosis.text.toString(), lugar.text.toString())
                }
                builder.setNegativeButton("Cancel", null)

                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
            }
        }
    }

    private fun addDosisToBlockchain(acc: String, nLote: String, provider: String, times: String, lugar: String) {
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

            //ADDING DOSIS
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
                if (key == "X"){
                    toast("You don't have an actual private address. Go back to add one.!")
                }
                val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(key)))
                Log.d(
                    "cospeito",
                    "Adding medical record of account: " + acc +
                            " to the system. Sending transaction from account: " + credentials.address
                )
                val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"

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

                val transactionReceipt0: TransactionReceipt? = medicalRecord.addDosis(acc, BigInteger(nLote),
                    provider, lugar, times).sendAsync().get()
                val result = "Successful transaction -> Hash:  ${transactionReceipt0?.transactionHash}. Block number: ${transactionReceipt0?.blockNumber}." +
                        " Gas used: ${transactionReceipt0?.gasUsed}."

                Log.d("cospeito", result)
                Log.d("cospeito", "Added new dose to the system, in account: " + acc + ".")

                val data: Intent = Intent()
                data.putExtra("result", result)
                setResult(79, data)
                finish()
            } catch (e: java.lang.Exception) {
                Log.d("cospeito", "Failed adding record: " + e.message)
                toast("Fallo al introducir la información. Repase los datos introducidos y su tipo.")
            }
        } catch (e: java.lang.Exception) {
            Log.d("cospeito", "Failed w/ al process of adding record: " + e.message)
            toast("Fallo genérico. Inténtelo de nuevo.")
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