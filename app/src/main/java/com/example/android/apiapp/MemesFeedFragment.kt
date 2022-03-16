package com.example.android.apiapp

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.ImageView
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_memes_feed.*
import kotlinx.android.synthetic.main.meme_dialog.*
import org.json.JSONObject

class MemesFeedFragment : Fragment(R.layout.fragment_memes_feed), IMemesFeedAdapter {

    private lateinit var memesFeedAdapter: MemesFeedAdapter
    private lateinit var manager : GridLayoutManager
    private var isScrolling = false
    private var memesList = mutableListOf<JSONObject>()
    private var newMemes = mutableListOf<JSONObject>()
    private val subreddits = mutableListOf(
            "wholesomememes", "me_irl", "dankmemes", "memes", "raimimemes", "historymemes",
            "AdviceAnimals", "ComedyCemetery", "terriblefacebookmemes", "funny", "teenagers" )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title = "Memes"
        setUpRecyclerView()
        setUpMemes()

    }

    private fun setUpMemes(){
        newMemes.clear()
        for(i in 0 until subreddits.size){
            if(i != subreddits.size-1){
                getMemesFromSubreddit(subreddits[i], false)
            } else {
                getMemesFromSubreddit(subreddits[i], true)
            }
        }
    }

    private fun setUpRecyclerView() = rvMemesFeed.apply {
        memesFeedAdapter = MemesFeedAdapter(this@MemesFeedFragment)
        adapter = memesFeedAdapter
        adapter!!.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        manager = GridLayoutManager(activity, 3)
        layoutManager = manager

        addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL)
                    isScrolling = true
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if(isScrolling && manager.childCount + manager.findFirstVisibleItemPosition() == manager.itemCount){
                    isScrolling = false
                    pbLoadMoreFeed.visibility = View.VISIBLE
                    setUpMemes()
                }
            }
        })
    }

    private fun getMemesFromSubreddit(subreddit : String, isLast : Boolean){

        val url = "https://meme-api.herokuapp.com/gimme/${subreddit}/4"

        val jsonObjectRequest = JsonObjectRequest( Request.Method.GET, url, null,
                { response ->
                    val memesArray = response.getJSONArray("memes")
                    for(i in 0 until memesArray.length()){
                        val imgType = (memesArray[i] as JSONObject).getString("url").takeLast(3)
                        if(imgType != "gif")
                        newMemes.add(memesArray[i] as JSONObject)
                    }

                    if(isLast){
                        newMemes.shuffle()
                        memesList.addAll(newMemes)
                        memesFeedAdapter.submitList(memesList)
                        memesFeedAdapter.notifyDataSetChanged()
                        pbLoadMoreFeed?.visibility = View.GONE
                    }
                },
                {

                })

        // Add the request to the RequestQueue.
        context?.let { MySingleton.getInstance(it).addToRequestQueue(jsonObjectRequest) }
    }

    override fun navigateToSwipeMemeFragment(meme : JSONObject) {
        val memeStr = meme.toString()
        val action = MemesFeedFragmentDirections.actionMemesFeedFragmentToSwipeMemesFragment(memeStr)
        navHostFragment.findNavController().navigate(action)
    }

    override fun showMemeInDialog(meme : JSONObject){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val dialogLayout = inflater.inflate(R.layout.meme_dialog, null)
        val titleView = dialogLayout.findViewById<TextView>(R.id.tvTitleDialog)
        val imgView = dialogLayout.findViewById<ImageView>(R.id.ivDialogMeme)
        val subredditView = dialogLayout.findViewById<TextView>(R.id.tvSubredditDialog)

        activity?.let { Glide.with(it).load(meme.getString("url")).into(imgView) }
        titleView.text = meme.getString("title")
        subredditView.text = meme.getString("subreddit")

        with(builder){
            setView(dialogLayout)
        }
        val dialog = builder.create()
        imgView.setOnClickListener {
            dialog.cancel()
            navigateToSwipeMemeFragment(meme)
        }
        titleView.setOnClickListener {
            val postLink = meme.getString("postLink")
            val builder2 = CustomTabsIntent.Builder()
            val customTabsIntent = builder2.build()
            customTabsIntent.launchUrl(requireContext(), Uri.parse(postLink))
        }
        dialog.show()
    }


}

//subreddits
//
//wholesomememes
//me_irl
//dankmemes
//memes
//raimimemes
//historymemes
//AdviceAnimals
//ComedyCemetery
//terriblefacebookmemes
//funny
//teenagers












