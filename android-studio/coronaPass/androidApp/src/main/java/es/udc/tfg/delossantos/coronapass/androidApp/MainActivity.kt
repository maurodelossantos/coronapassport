package es.udc.tfg.delossantos.coronapass.androidApp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.ContextMenu.ContextMenuInfo
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.cdc.CdcMain
import es.udc.tfg.delossantos.coronapass.androidApp.encryption.Aes256Class.*
import es.udc.tfg.delossantos.coronapass.androidApp.patient.AddPersonalData
import es.udc.tfg.delossantos.coronapass.androidApp.patient.Blockchain
import es.udc.tfg.delossantos.coronapass.androidApp.patient.PatientWallet
import es.udc.tfg.delossantos.coronapass.androidApp.patient.ShowPassport
import es.udc.tfg.delossantos.coronapass.androidApp.smartcontracts.*
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.FirebaseUtils.firebaseAuth
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.SignUp
import es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders.IntroSliderActivity
import es.udc.tfg.delossantos.coronapass.shared.Greeting
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.json.JSONObject
import org.web3j.crypto.Credentials
import org.web3j.crypto.ECKeyPair
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.DefaultBlockParameterName
import org.web3j.protocol.core.methods.response.EthGetBalance
import org.web3j.protocol.core.methods.response.TransactionReceipt
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import org.web3j.tx.gas.StaticGasProvider
import org.web3j.utils.Convert
import java.io.*
import java.math.BigInteger
import java.net.URLConnection
import java.security.Provider
import java.security.Security
import java.util.*
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

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

class MainActivity : AppCompatActivity() {
    lateinit var toolbar: ActionBar
    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_qrCode -> {
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_qrScanner -> {
                    /* Create a Zxing IntentIntegrator and start the QR code scan */
                    val integrator = IntentIntegrator(this)
                    integrator.setRequestCode(REQUEST_CODE)
                    integrator.setOrientationLocked(true)
                    //integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES)
                    //integrator.setPrompt("Scanning Code")
                    integrator.initiateScan()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_userProfile -> {
                    //esta imagen es de lo mas cutre que hay, MEJORAR = NAVBAR¿?
                    val intent = Intent(this, PatientWallet::class.java)
                    startActivityForResult(intent, 0)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_cdcProfile -> {
                    //esta imagen es de lo mas cutre que hay, MEJORAR = NAVBAR¿?
                    val intent = Intent(this, CdcMain::class.java)
                    startActivity(intent)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    private fun deleteAccData(){
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        var editor = sharedPref.edit()
        editor.putString("key", "X")
        editor.putString("account", "X")
        editor.putString("pubkey", "X")
        with(sharedPref.edit()) {
            putString("idMedico", "X")
            putString("idNacional", "X")
            putString("nombre", "X")
            putString("ap1", "X")
            putString("ap2", "X")
            putString("genero", "X")
            putString("fechaNac", "X")
            putString("pais", "X")
            putString("contacto", "X")
        }
        editor.commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.getItemId()) {
            R.id.send -> {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Introduzca la clave compartida con la que cifrar el pasaporte")
                // set the custom layout
                val customLayout = getLayoutInflater().inflate(R.layout.dialog_add_account, null);
                val et_new_key: EditText = customLayout.findViewById(R.id.et_add_new_privKey)
                builder.setView(customLayout);

                // add OK and Cancel buttons
                builder.setPositiveButton("OK") { dialog, which ->
                    // user clicked OK
                    if (et_new_key.text.toString().length > 0){
                        sendQr(et_new_key.text.toString())
                    }else{
                        et_new_key.setError("Introduzca una clave para cifrar el código.")
                    }
                }
                builder.setNegativeButton("Cancel", null)

                // create and show the alert dialog
                val dialog = builder.create()
                dialog.show()
                return true
            }
            R.id.importqr -> {
                val intent = Intent()
                    .setType("*/*")
                    .setAction(Intent.ACTION_GET_CONTENT)
                startActivityForResult(Intent.createChooser(intent, "Select a file"), 111)
                return true
            }
            R.id.update -> {
                stateQR()
                true
            }
            R.id.logout -> {
                firebaseAuth.signOut()

                deleteAccData()

                startActivity(Intent(this, SignUp::class.java))
                toast("Logged out")
                finish()
                return true
            }
            R.id.tutorial -> {
                val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
                    "pasaporte",
                    masterKeyAlias,
                    applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val intent = Intent(this, IntroSliderActivity::class.java)
                intent.putExtra("privateKey", sharedPref.getString("key", "X").toString())
                intent.putExtra("account", sharedPref.getString("account", "X").toString())
                startActivity(intent)
                return true
            }
            else -> super.onContextItemSelected(item)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.my_toolbar))

        var fb_editQR: ImageButton = findViewById(R.id.fb_editqr)
        fb_editQR.setOnClickListener {
            val intent = Intent(this, AddPersonalData::class.java)
            startActivityForResult(intent, 0)
        }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.selectedItemId = R.id.navigation_qrCode
        bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val defaultValue = "X"

        walletId = sharedPref.getString("account", defaultValue).toString()
        idMedico = sharedPref.getString("idMedico", defaultValue).toString()
        idNacional = sharedPref.getString("idNacional", defaultValue).toString()
        nombre = sharedPref.getString("nombre", defaultValue).toString()
        ap1 = sharedPref.getString("ap1", defaultValue).toString()
        ap2 = sharedPref.getString("ap2", defaultValue).toString()
        genero = sharedPref.getString("genero", defaultValue).toString()
        pais = sharedPref.getString("pais", defaultValue).toString()
        fechNac = sharedPref.getString("fechNac", defaultValue).toString()
        contacto = sharedPref.getString("contacto", defaultValue).toString()

        val jsonPersonalData = JSONObject(
            """{"Wallet":""" + walletId + """, "IDNac":""" + idNacional + """, "IDMed":""" +
                    idMedico + """, "Nombre":'""" + nombre + """', "Ap1":'""" + ap1 +
                    """', "Ap2":'""" + ap2 + """', "Genero":'""" + genero + """', "FechaNac":'""" +
                    fechNac + """', "Pais":'""" + pais + """', "Contacto":'""" + contacto +
                    """'}"""
        )

        val bitmap0 = generateQRCode(jsonPersonalData.toString())
        var iv_codeQR: ImageView = findViewById(R.id.iv_codeQR)
        iv_codeQR.setImageBitmap(bitmap0)

        stateQR()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun sendQr(pass: String){
        val pwd: String = pass
        var message: String = "No account available"
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
        var acc = sharedPref.getString("account", defaultValue).toString()
        if (acc == "none"){
            toast("You don't have saved the account value.")
            //opcion a meter clave privada¿?
        }else {
            message = acc
        }

        //val sr: SecureRandom = SecureRandom.getInstanceStrong()
        val salt = ByteArray(16)
        //sr.nextBytes(salt)

        val spec = PBEKeySpec(pwd.toCharArray(), salt, 1000, 32 * 8)
        val key: SecretKey =
            SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec)

        val directory: File = filesDir //or getExternalFilesDir(null); for external storage

        val file = File(directory, "qr.txt")

        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
            fos.write(encryptMsg(message, key))
            fos.close()
            val intentShareFile = Intent(Intent.ACTION_SEND)

            intentShareFile.type = URLConnection.guessContentTypeFromName(file.name)
            intentShareFile.putExtra(
                Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        applicationContext,
                        "es.udc.tfg.delossantos.coronapass.androidApp.provider",
                        file
                    )
            )

            val intentChooser: Intent = Intent.createChooser(intentShareFile, "Share File")

            val resInfoList =
                packageManager.queryIntentActivities(
                    intentChooser,
                    PackageManager.MATCH_DEFAULT_ONLY
                )

            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    FileProvider.getUriForFile(
                        applicationContext,
                        "es.udc.tfg.delossantos.coronapass.androidApp.provider",
                        file
                    ),
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }
            //if you need
            //intentShareFile.putExtra(Intent.EXTRA_SUBJECT,"Sharing File Subject);
            //intentShareFile.putExtra(Intent.EXTRA_TEXT, "Sharing File Description");
            startActivity(intentChooser)
            Log.d("cospeito", "FUNCIONAAAAAA: " + decryptMsg(file.readBytes(), key))
            Log.d("cospeito", "FUNCIONAAAAAA BYTES: " + file.readBytes())
            Log.d("cospeito", "Todo correcto.")
        } catch (e: IOException) {
            Log.d("cospeito", "ERROR FIRST STEP: " + e.message)
            e.printStackTrace()
        }
    }

    private fun intoTheSystem(acc: String, privKey: String) : Boolean {
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
            val credentials: Credentials = Credentials.create(ECKeyPair.create(BigInteger(privKey)))
            Log.d("cospeito", "ACCOUNT: " + credentials.address.toString())
            val contractAddress = "0x0c8E392EF799F3EADBAe7B4d780716533ad03347"
            val gasLimit: BigInteger = BigInteger.valueOf(20_000_000_000L)
            val gasPrice: BigInteger = BigInteger.valueOf(4300000)

            val medicalRecord = MedicalRecord3.load(
                contractAddress,
                web3,
                credentials,
                gasLimit,
                gasPrice
            )

            val transactionReceipt0: Boolean = medicalRecord.isPatient(acc).sendAsync().get()
            return transactionReceipt0
        } catch (e: java.lang.Exception) {
            //Display an Error
            Log.d("cospeito", "Error during isPatient response: " + e.message)
            return false
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

    private fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text, BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Log.d("Debugging", "generateQRCode: ${e.message}")
        }
        return bitmap
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) { //si venimos de leer QR
            val result = IntentIntegrator.parseActivityResult(resultCode, data)
            if(result != null) {
                if(result.getContents() == null) {
                    Toast.makeText(this, "Scan cancelled", Toast.LENGTH_LONG).show()
                }
                else
                {
                    val intent = Intent(this, ShowPassport::class.java)
                    intent.putExtra("info", result.contents)
                    startActivity(intent)
                }
            } else {
                super.onActivityResult(requestCode, resultCode, data)
            }
        }else if (requestCode == 111 && resultCode == RESULT_OK) {
            var pwd: String = ""
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Introduzca la clave para descifrar el pasaporte")

            // set the custom layout
            val customLayout = getLayoutInflater().inflate(R.layout.dialog_add_account, null);
            val et_new_key: EditText = customLayout.findViewById(R.id.et_add_new_privKey)
            builder.setView(customLayout);

            // add OK and Cancel buttons
            builder.setPositiveButton("OK") { dialog, which ->
                // user clicked OK
                if (et_new_key.text.toString().length > 0){
                    pwd = et_new_key.text.toString()
                    //val sr: SecureRandom = SecureRandom.getInstanceStrong()
                    val salt = ByteArray(16)
                    //sr.nextBytes(salt)

                    val spec = PBEKeySpec(pwd.toCharArray(), salt, 1000, 32 * 8)
                    val key: SecretKey =
                        SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1").generateSecret(spec)

                    val selectedFilename = data?.data //The uri with the location of the file
                    if (selectedFilename != null) {
                        val inp: InputStream? = contentResolver.openInputStream(selectedFilename)
                        val acc = decryptMsg(contentResolver.openInputStream(selectedFilename)?.buffered()?.use { it.readBytes() }, key)
                        Log.d("cospeito", "Sending get Record of: " + acc)
                        val intent = Intent(this, ShowPassport::class.java)
                        intent.putExtra("info", """{"Wallet":""" + acc + "}")
                        startActivity(intent)
                    }
                }else{
                    et_new_key.setError("Introduzca una clave para cifrar el pasaporte.")
                }
            }
            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }else{ //si venimos de añadir datos
            if (data != null) {
                idMedico = data.getStringExtra("IDMed")
                idNacional = data.getStringExtra("IDNac")
                nombre = data.getStringExtra("Nombre")
                ap1 = data.getStringExtra("Ap1")
                ap2 = data.getStringExtra("Ap2")
                genero = data.getStringExtra("Genero")
                fechNac = data.getStringExtra("FechaNac")
                pais = data.getStringExtra("Pais")
                contacto = data.getStringExtra("Contacto")

                val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
                val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
                    "pasaporte",
                    masterKeyAlias,
                    applicationContext,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                with(sharedPref.edit()) {
                    putString("idMedico", idMedico)
                    putString("idNacional", idNacional)
                    putString("nombre", nombre)
                    putString("ap1", ap1)
                    putString("ap2", ap2)
                    putString("genero", genero)
                    putString("fechaNac", fechNac)
                    putString("pais", pais)
                    putString("contacto", contacto)
                    commit()
                }

                val jsonPersonalData = JSONObject(
                    """{"Wallet":""" + sharedPref.getString(
                        "account",
                        "none"
                    ) + """, "IDNac":""" + idNacional + """, "IDMed":""" + idMedico +
                            """, "Nombre":'""" + nombre + """', "Ap1":'""" + ap1 + """', "Ap2":'""" +
                            ap2 + """', "Genero":'""" + genero + """', "FechaNac":'""" + fechNac +
                            """', "Pais":'""" + pais + """', "Contacto":'""" + contacto + """'}"""
                )
                val bitmap = generateQRCode(jsonPersonalData.toString())
                var iv_codeQR: ImageView = findViewById(R.id.iv_codeQR)
                iv_codeQR.setImageBitmap(bitmap)

                stateQR()
            }
        }
    }

    private fun stateQR(){
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        val tv: TextView = findViewById(R.id.tv_infoQR)
        var account = sharedPref.getString("account", "none").toString()
        var privKey = sharedPref.getString("key", "none").toString()
        if (account == "none" || privKey == "none"){
            tv.setText("Tu clave privada no está en el sistema! Añádela.")
        }else if(sharedPref.getString("idNacional", "X").toString()=="X") {
            tv.setText("No hay datos personales aún. Introdúcelos en perfil.")
        }else {
            var system = intoTheSystem(account, privKey)
            if (system == true) {
                tv.setText("Tus datos están seguros en el sistema.")
            } else {
                tv.setText("Acude a una entidad certificada para entrar en el sistema.")
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigation: BottomNavigationView = findViewById(R.id.navigationView)
        bottomNavigation.selectedItemId = R.id.navigation_qrCode

        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref: SharedPreferences = EncryptedSharedPreferences.create(
            "pasaporte",
            masterKeyAlias,
            applicationContext,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val jsonPersonalData = JSONObject(
            """{"Wallet":""" + sharedPref.getString(
                "account",
                "none"
            ) + """, "IDNac":""" + idNacional + """, "IDMed":""" + idMedico + """, "Nombre":'""" +
                    nombre + """', "Ap1":'""" + ap1 + """', "Ap2":'""" + ap2 + """', "Genero":'""" +
                    genero + """', "FechaNac":'""" + fechNac + """', "Pais":'""" + pais +
                    """', "Contacto":'""" + contacto + """'}"""
        )
        val bitmap = generateQRCode(jsonPersonalData.toString())
        var iv_codeQR: ImageView = findViewById(R.id.iv_codeQR)
        iv_codeQR.setImageBitmap(bitmap)

        stateQR()
    }
}
