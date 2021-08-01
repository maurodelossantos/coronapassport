package es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import es.udc.tfg.delossantos.coronapass.androidApp.R


class Intro1Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(R.layout.activity_intro1_fragment, container, false)

        var tvPrivKey: TextView
        var click: ImageButton = view.findViewById(R.id.ib_copyToClipboard)
        click.setOnClickListener() {
            tvPrivKey = view.findViewById(R.id.tv_fr1_privKey)

            val textToCopy = tvPrivKey.text

            val myClipboard =
                context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val myClip: ClipData = ClipData.newPlainText("Label", textToCopy)
            myClipboard.setPrimaryClip(myClip)

            Toast.makeText(this.context, "Copied Private Key to clipboard", Toast.LENGTH_SHORT)
                .show()
        }
        return view
    }
}