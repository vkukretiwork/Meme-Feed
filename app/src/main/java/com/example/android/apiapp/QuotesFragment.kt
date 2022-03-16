package com.example.android.apiapp

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import kotlinx.android.synthetic.main.fragment_quotes.*
import org.json.JSONArray
import org.json.JSONObject

class QuotesFragment : Fragment(R.layout.fragment_quotes) {

    var mJsonQuotesArray : JSONArray? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Quotes"

        loadQuotes()
        subscribeToButtons()
    }

    private fun subscribeToButtons() {
        btnNextQuote.setOnClickListener { loadNextQuote() }
    }

    private fun loadQuotes() {

        val url = "https://type.fit/api/quotes"

        val jsonArrayRequest = JsonArrayRequest(
                Request.Method.GET, url, null,
                {
                    mJsonQuotesArray = it
                    setUpQuoteInViews(it)
                }, {
                    Toast.makeText(context,"check internet connection", Toast.LENGTH_SHORT).show()
                })

        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonArrayRequest) }
    }

    private fun loadNextQuote() {
        if(mJsonQuotesArray == null){
            loadQuotes()
            return
        }
        setUpQuoteInViews(mJsonQuotesArray)
    }
    private fun setUpQuoteInViews(quotesArray : JSONArray?){

        val randomIndex = (0 until quotesArray!!.length()).random()
        val jsonObj = quotesArray.get(randomIndex) as JSONObject
        val quote = jsonObj.getString("text")
        var author = jsonObj.getString("author")
        tvQuote?.text = quote
        if(author == "null")
            author = "Anonymous"
        tvAuthor?.text = author
    }

}