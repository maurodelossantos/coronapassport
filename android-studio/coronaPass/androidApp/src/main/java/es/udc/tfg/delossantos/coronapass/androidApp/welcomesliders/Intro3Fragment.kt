package es.udc.tfg.delossantos.coronapass.androidApp.welcomesliders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import es.udc.tfg.delossantos.coronapass.androidApp.R

class Intro3Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.activity_intro3_fragment, container, false)
    }
}