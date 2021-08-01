package es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import es.udc.tfg.delossantos.coronapass.androidApp.R
import es.udc.tfg.delossantos.coronapass.androidApp.usersmanagement.SignUp

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // making the status bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        setContentView(R.layout.activity_splash)
        Handler().postDelayed(object: Runnable {
            override fun run() {
                startActivity(Intent(this@SplashActivity, SignUp::class.java))
                finish()
            }
        }, 2000)
    }
}