package es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.FirebaseUtils
import es.udc.tfg.delossantos.coronapass.androidApp.R

class Intro2Fragment : Fragment() {
    lateinit var btEther: Button
    lateinit var tvSendEmail: TextView

    private fun sendEmailVerification() {
        FirebaseUtils.firebaseAuth.currentUser?.let {
            it.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this.context, "email sent to " + FirebaseUtils.firebaseAuth.currentUser.email.toString(), Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this.context, task.exception.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.activity_intro2_fragment, container, false)
           tvSendEmail = view.findViewById(R.id.tv_sendEmail)
           tvSendEmail.setText("Click here to send a verification email")
           tvSendEmail.setOnClickListener(){
               sendEmailVerification()
           }
        return view
    }
}