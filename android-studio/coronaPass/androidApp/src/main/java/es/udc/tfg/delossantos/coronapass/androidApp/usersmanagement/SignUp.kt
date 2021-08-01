package es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.firebase.auth.FirebaseUser
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.MainActivity
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.FirebaseUtils.firebaseAuth
import es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders.IntroSliderActivity
import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.web3j.crypto.Credentials
import org.web3j.crypto.WalletUtils
import org.web3j.protocol.Web3j
import org.web3j.protocol.core.methods.response.Web3ClientVersion
import org.web3j.protocol.http.HttpService
import java.io.File
import java.security.Provider
import java.security.Security

class SignUp : AppCompatActivity() {
    lateinit var userEmail: String
    lateinit var userPassword: String
    lateinit var createAccountInputsArray: Array<EditText>

    lateinit var etEmail: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var spinner: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)


        createAccountInputsArray = arrayOf(findViewById(R.id.etEmail), findViewById(R.id.etPassword), findViewById(
            R.id.etConfirmPassword
        ))

        var btnCreateAccount: Button = findViewById(R.id.btnCreateAccount)
        btnCreateAccount.setOnClickListener {
            signIn()
        }

        var btnSignIn2: Button = findViewById(R.id.btnSignIn2)
        btnSignIn2.setOnClickListener {
            startActivity(Intent(this, LogIn::class.java))
            toast("Please sign into your account")
            finish()
        }

        spinner = findViewById(R.id.spinnerSignUp)
    }

    override fun onStart() {
        super.onStart()
        val user: FirebaseUser? = firebaseAuth.currentUser
        user?.let {
            startActivity(Intent(this, MainActivity::class.java))
            toast("Welcome back!")
        }
    }

    private fun notEmpty(): Boolean = etEmail.text.toString().trim().isNotEmpty() &&
            etPassword.text.toString().trim().isNotEmpty() &&
            etConfirmPassword.text.toString().trim().isNotEmpty()

    private fun identicalPassword(): Boolean {
        var identical = false
        if (notEmpty() &&
            etPassword.text.toString().trim() == etConfirmPassword.text.toString().trim()
        ) {
            identical = true
        } else if (!notEmpty()) {
            createAccountInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        } else {
            toast("Passwords are not matching !")
        }
        return identical
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

    private fun createWallet(pwd: String): Credentials? {
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

        //GLOBAL VARS - wip
        var fileName = ""
        val walletPath = filesDir.absolutePath
        var walletDir = File(walletPath)
        var createdWallet = ""
        Log.d("cospeito", "Creating new wallet")
        try {
            //wip: path where to create, where to store it, reuse without device dependency...
            fileName = WalletUtils.generateLightNewWalletFile(pwd, walletDir)
            walletDir = File("$walletPath/$fileName")
            var credentials: Credentials = WalletUtils.loadCredentials(pwd, walletDir)
            val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
            val sharedPreference: SharedPreferences = EncryptedSharedPreferences.create(
                "pasaporte",
                masterKeyAlias,
                applicationContext,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
            var editor = sharedPreference.edit()
            editor.putString("key", credentials.ecKeyPair.privateKey.toString())
            editor.putString("account", credentials.address)
            editor.putString("pubkey", credentials.ecKeyPair.publicKey.toString())
            editor.commit()
            Log.d("cospeito", "Created new Wallet!")
            Log.d("cospeito", "address: " + credentials.address)
            Log.d("cospeito", "private key: " + credentials.ecKeyPair.privateKey)
            Log.d("cospeito", "public key: " + credentials.ecKeyPair.publicKey)
            return credentials
        } catch (e: java.lang.Exception) {
            //Display an Error
            Log.d("cospeito", "Error during wallet creation: " + e.message)
            return null
        }
    }

    private fun signIn() {
        if (identicalPassword()) {
            spinner.visibility = View.VISIBLE
            // identicalPassword() returns true only  when inputs are not empty and passwords are identical
            userEmail = etEmail.text.toString().trim()
            userPassword = etPassword.text.toString().trim()

            /*create a user*/
            firebaseAuth.createUserWithEmailAndPassword(userEmail, userPassword)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        toast("Created account successfully!")
                        val credentialsReturned = createWallet(userPassword)
                        //sendEmailVerification()
                        val intent = Intent(this, IntroSliderActivity::class.java)
                        intent.putExtra("privateKey", credentialsReturned?.ecKeyPair?.privateKey.toString())
                        intent.putExtra("account", credentialsReturned?.address)
                        startActivity(intent)
                        finish()
                    } else {
                        toast(task.exception.toString())
                        spinner.visibility = View.INVISIBLE
                    }
                }
        }
    }
}