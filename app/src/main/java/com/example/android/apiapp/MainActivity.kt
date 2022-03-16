package com.example.android.apiapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())
        bottomNavigationView.setOnItemReselectedListener { /* NO-OP */ }

        navHostFragment.findNavController()
                .addOnDestinationChangedListener{ _, destination, _ ->
                    when(destination.id){
                        R.id.quotesFragment, R.id.memesFeedFragment
                            -> {
                            bottomNavigationView.visibility = View.VISIBLE
                            supportActionBar!!.show()
                        }
                        else -> {
                            supportActionBar!!.hide()
                            bottomNavigationView.visibility = View.GONE

                        }
                    }
                }
    }
}