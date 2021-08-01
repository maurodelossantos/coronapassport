package es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import es.udc.tfg.delossantos.coronapass.androidApp.Extensions.toast
import es.udc.tfg.delossantos.coronapass.androidApp.MainActivity
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.FirebaseUtils.firebaseAuth

class LogIn : AppCompatActivity() {
    lateinit var signInEmail: String
    lateinit var signInPassword: String
    lateinit var signInInputsArray: Array<EditText>

    lateinit var etSignInEmail: EditText
    lateinit var etSignInPassword: EditText
    lateinit var btnCreateAccount2: Button
    lateinit var btnSignIn: Button
    lateinit var forgotPwd: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        etSignInEmail = findViewById(R.id.etSignInEmail)
        etSignInPassword = findViewById(R.id.etSignInPassword)
        btnCreateAccount2 = findViewById(R.id.btnCreateAccount2)
        btnSignIn = findViewById(R.id.btnSignIn)

        forgotPwd = findViewById(R.id.tv_forgot_password)
        forgotPwd.setOnClickListener() {
            // setup the alert builder
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Introduzca su email para enviarle la nueva contraseña")

            // set the custom layout
            val customLayout = getLayoutInflater().inflate(R.layout.dialog_forgotten_pwd, null);

            val email: EditText = customLayout.findViewById(R.id.et_email_forgotten_pwd)

            builder.setView(customLayout);
            // add OK and Cancel buttons
            builder.setPositiveButton("Send") { dialog, which ->
                // user clicked OK
                //addReaccionToBlockchain(reacc.text.toString())
                val emailStr = email.text.toString()
                if (!TextUtils.isEmpty(emailStr)) {
                    FirebaseAuth.getInstance()
                        .sendPasswordResetEmail(emailStr)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val message = "Email sent."
                                toast(message)
                                updateUI()
                            } else {
                                Log.d("cospeito", task.exception!!.message)
                                toast("No user found with this email.")
                            }
                        }
                } else {
                    toast("Enter Email")
                }
            }

            builder.setNegativeButton("Cancel", null)

            // create and show the alert dialog
            val dialog = builder.create()
            dialog.show()
        }

        signInInputsArray = arrayOf(etSignInEmail, etSignInPassword)
        btnCreateAccount2.setOnClickListener {
            startActivity(Intent(this, SignUp::class.java))
            finish()
        }

        btnSignIn.setOnClickListener {
            signInUser()
        }
    }

    private fun updateUI() {
        val intent = Intent(this, LogIn::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun notEmpty(): Boolean = signInEmail.isNotEmpty() && signInPassword.isNotEmpty()

    private fun signInUser() {
        signInEmail = etSignInEmail.text.toString().trim()
        signInPassword = etSignInPassword.text.toString().trim()

        if (notEmpty()) {
            firebaseAuth.signInWithEmailAndPassword(signInEmail, signInPassword)
                .addOnCompleteListener { signIn ->
                    if (signIn.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            toast("Signed in successfully")
                            finish()
                    } else {
                        toast("Sign in failed")
                    }
                }
        } else {
            signInInputsArray.forEach { input ->
                if (input.text.toString().trim().isEmpty()) {
                    input.error = "${input.hint} is required"
                }
            }
        }
    }
}