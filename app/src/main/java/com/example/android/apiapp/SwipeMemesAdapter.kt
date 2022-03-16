package com.example.android.apiapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_swipe_memes.view.*
import kotlinx.android.synthetic.main.item_memes_feed.view.*
import org.json.JSONObject

class SwipeMemesAdapter( private val listener : ISwipeMemesAdapter )
    : RecyclerView.Adapter<SwipeMemesAdapter.SwipeMemesViewHolder>() {

        inner class SwipeMemesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            val memeTitle : TextView = itemView.tvTitleSMF
        }

    private val diffCallback = object : DiffUtil.ItemCallback<JSONObject>() {
        override fun areItemsTheSame(oldItem: JSONObject, newItem: JSONObject): Boolean {
            return oldItem.getString("url") == newItem.getString("url")
        }

        override fun areContentsTheSame(oldItem: JSONObject, newItem: JSONObject): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(list : MutableList<JSONObject>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwipeMemesViewHolder {
        val viewHolder = SwipeMemesViewHolder(
                LayoutInflater.from(parent.context).inflate( R.layout.fragment_swipe_memes, parent, false)
        )
        viewHolder.memeTitle.setOnClickListener {

        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SwipeMemesViewHolder, position: Int) {
        val jsonObject = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(holder.itemView.context).load(jsonObject.getString("url")).into(ivItemMemesFeed)
            tvTitleSMF.text = jsonObject.getString("title")
            tvUpsSMF.text = jsonObject.getString("ups")
            tvSubredditSMF.text = jsonObject.getString("subreddit")
            tvAuthorSMF.text = jsonObject.getString("author")
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

interface ISwipeMemesAdapter{

}