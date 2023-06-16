package com.example.android.apiapp

import android.graphics.Color
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about.*

class AboutFragment : Fragment(R.layout.fragment_about) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "About"

        tvApiLink.movementMethod = LinkMovementMethod.getInstance()
        tvApiLink.setLinkTextColor(Color.BLUE)
    }


}