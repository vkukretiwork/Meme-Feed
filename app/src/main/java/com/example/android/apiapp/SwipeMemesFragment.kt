package com.example.android.apiapp

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_swipe_memes.*
import org.json.JSONObject


class SwipeMemesFragment : Fragment(R.layout.fragment_swipe_memes), ISwipeMemesAdapter{

    private lateinit var themeUrl : String
    private val memeList = mutableListOf<JSONObject>()
    private lateinit var swipeMemesAdapter: SwipeMemesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val obj = JSONObject(arguments?.let { SwipeMemesFragmentArgs.fromBundle(it).memeStringFromMemeFeed }!!)
        themeUrl = "https://meme-api.herokuapp.com/gimme/${obj.getString("subreddit")}/10"

        setUpViewPager2()

        memeList.add(obj)
        addMemesToMemeList()

    }
    private fun setUpViewPager2(){
        swipeMemesAdapter = SwipeMemesAdapter(this@SwipeMemesFragment)

        vpSwipeMemes.apply {
            // Set offscreen page limit to at least 1, so adjacent pages are always laid out
            offscreenPageLimit = 1
            val recyclerView = getChildAt(0) as RecyclerView
            recyclerView.apply {
                val padding = resources.getDimensionPixelOffset(R.dimen.halfPageMargin) +
                        resources.getDimensionPixelOffset(R.dimen.peekOffset)
                // setting padding on inner RecyclerView puts overscroll effect in the right place
//                setPadding(padding, 0, padding, 0)
                setPadding(0, 0, 0, padding)
                clipToPadding = false
            }
            adapter = swipeMemesAdapter
        }
    }

    override fun addMemesToMemeList(){
        Log.i("util", "add memes called")

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, themeUrl, null,
                { response ->
                    val memesArray = response.getJSONArray("memes")
                    for(i in 0 until memesArray.length()){
                        val meme = memesArray[i] as JSONObject
                        val imgType = meme.getString("url").takeLast(3)
                        if(imgType != "gif")
                        memeList.add(memesArray[i] as JSONObject)
                    }

                    swipeMemesAdapter.submitList(memeList)
                    swipeMemesAdapter.notifyDataSetChanged()
                },
                {

                })
//        // Add the request to the RequestQueue.
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

    override fun shareMeme(meme: JSONObject) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        val title = meme.getString("title")
        val memeImgUrl = meme.getString("url")
        intent.putExtra(Intent.EXTRA_TEXT, "$title : \n $memeImgUrl")
        val chooser = Intent.createChooser(intent, "Share this meme using...")
        startActivity(chooser)
    }

    override fun openInCustomTab(meme: JSONObject) {
        val postLink = meme.getString("postLink")
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(requireContext(), Uri.parse(postLink))
    }

    override fun showMemeInDialog(meme : JSONObject){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.meme_dialog, null)
        val titleView = dialogLayout.findViewById<TextView>(R.id.tvTitleDialog)
        val imgView = dialogLayout.findViewById<ImageView>(R.id.ivDialogMeme)
        val subredditInfo = dialogLayout.findViewById<TextView>(R.id.tvSubredditInfoDialog)

        subredditInfo.visibility = View.GONE
        activity?.let { Glide.with(it).load(meme.getString("url")).into(imgView) }
        titleView.text = meme.getString("title")

        with(builder){
            setView(dialogLayout)
        }
        titleView.setOnClickListener {
            val postLink = meme.getString("postLink")
            val builder2 = CustomTabsIntent.Builder()
            val customTabsIntent = builder2.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(postLink))
        }
        val dialog = builder.create()
        dialog.show()
    }

}




//class SwipeMemesFragment : Fragment(R.layout.fragment_swipe_memes) {

//    private lateinit var themeUrl : String
//    private val memeList = mutableListOf<JSONObject>()
//    private var currMemeInd = 0
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        val obj = JSONObject(arguments?.let { SwipeMemesFragmentArgs.fromBundle(it).memeStringFromMemeFeed }!!)
//
//        memeList.add(obj)
//        themeUrl = "https://meme-api.herokuapp.com/gimme/${obj.getString("subreddit")}"
//        loadMeme(obj)
//        subscribeToClickable()
//    }
//
//    private fun subscribeToClickable() {
//        ibNextSMF.setOnClickListener {
//            currMemeInd++
//            if(currMemeInd >= memeList.size){
//                currMemeInd = memeList.size
//                makeMemeRequest()
//            }else{
//                loadMeme(memeList[currMemeInd])
//            }
//        }
//
//        ibPrevSMF.setOnClickListener {
//            currMemeInd--
//            if(currMemeInd < 0){
//                currMemeInd = 0
//            }
//            loadMeme(memeList[currMemeInd])
//        }
//
//        ibShareSMF.setOnClickListener {
//            shareMeme()
//        }
//
//        tvTitleSMF.setOnClickListener {
//            val postLink = memeList[currMemeInd].getString("postLink")
//            val builder = CustomTabsIntent.Builder()
//            val customTabsIntent = builder.build()
//            customTabsIntent.launchUrl(requireContext(), Uri.parse(postLink))
//        }
//
//    }
//
//    private fun makeMemeRequest(){
//
//        val jsonObjectRequest = JsonObjectRequest( Request.Method.GET, themeUrl, null,
//                { response ->
//                    val imgType = response.getString("url").takeLast(3)
//                    if(imgType == "gif"){
//                        makeMemeRequest()
//                    }else {
//                        loadMeme(response)
//                        memeList.add(response)
//                    }
//                },
//                {
//
//                })
//
//        // Add the request to the RequestQueue.
//        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
//    }
//
//    private fun loadMeme(obj : JSONObject){
//        activity?.let { Glide.with(it).load(obj.getString("url")).into(ivSwipeMeme) }
//        tvTitleSMF?.text = obj.getString("title")
//        tvUpsSMF?.text = obj.getString("ups")
//        tvSubredditSMF?.text = obj.getString("subreddit")
//        tvAuthorSMF?.text = obj.getString("author")
//    }
//
//    private fun shareMeme() {
//        if(memeList.isEmpty())return
//
//        val intent = Intent(Intent.ACTION_SEND)
//        intent.type = "text/plain"
//        val currMemeImgUrl = memeList[currMemeInd].getString("url")
//        intent.putExtra(Intent.EXTRA_TEXT, "Hey, checkout this cool meme $currMemeImgUrl")
//        val chooser = Intent.createChooser(intent, "Share this meme using...")
//        startActivity(chooser)
//    }
//
//}