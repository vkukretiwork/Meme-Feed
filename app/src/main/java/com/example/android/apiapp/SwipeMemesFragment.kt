package com.example.android.apiapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_swipe_memes.*
import org.json.JSONObject
import java.util.*

class SwipeMemesFragment : Fragment(R.layout.fragment_swipe_memes) {

    private lateinit var themeUrl : String
    private val memeList = mutableListOf<JSONObject>()
    private var currMemeInd = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject(arguments?.let { SwipeMemesFragmentArgs.fromBundle(it).memeStringFromMemeFeed }!!)

        memeList.add(obj)
        themeUrl = "https://meme-api.herokuapp.com/gimme/${obj.getString("subreddit")}"
        loadMeme(obj)
        subscribeToClickable()
    }

    private fun subscribeToClickable() {
        ibNextSMF.setOnClickListener {
            currMemeInd++
            if(currMemeInd >= memeList.size){
                currMemeInd = memeList.size
                makeMemeRequest()
            }else{
                loadMeme(memeList[currMemeInd])
            }
        }

        ibPrevSMF.setOnClickListener {
            currMemeInd--
            if(currMemeInd < 0){
                currMemeInd = 0
            }
            loadMeme(memeList[currMemeInd])
        }

        ibShareSMF.setOnClickListener {
            shareMeme()
        }

        tvTitleSMF.setOnClickListener {
            val postLink = memeList[currMemeInd].getString("postLink")
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(postLink))
        }

    }

    private fun makeMemeRequest(){

        val jsonObjectRequest = JsonObjectRequest( Request.Method.GET, themeUrl, null,
                { response ->
                    val imgType = response.getString("url").takeLast(3)
                    if(imgType == "gif"){
                        makeMemeRequest()
                    }else {
                        loadMeme(response)
                        memeList.add(response)
                    }
                },
                {

                })

        // Add the request to the RequestQueue.
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

    private fun loadMeme(obj : JSONObject){
        activity?.let { Glide.with(it).load(obj.getString("url")).into(ivSwipeMeme) }
        tvTitleSMF?.text = obj.getString("title")
        tvUpsSMF?.text = obj.getString("ups")
        tvSubredditSMF?.text = obj.getString("subreddit")
        tvAuthorSMF?.text = obj.getString("author")
    }

    private fun shareMeme() {
        if(memeList.isEmpty())return

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val currMemeImgUrl = memeList[currMemeInd].getString("url")
        intent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this cool meme $currMemeImgUrl")
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }

}