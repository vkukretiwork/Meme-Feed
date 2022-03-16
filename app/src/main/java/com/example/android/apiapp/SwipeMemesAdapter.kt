package com.example.android.apiapp

import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_swipe_meme.view.*
import org.json.JSONObject

class SwipeMemesAdapter( private val listener : ISwipeMemesAdapter)
    : RecyclerView.Adapter<SwipeMemesAdapter.SwipeMemesViewHolder>() {

        inner class SwipeMemesViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
            val memeTitle : TextView = itemView.tvTitleSMF
            val shareMemeButton : ImageButton = itemView.ibShareSMF
            val memeImg : ImageView = itemView.ivSwipeMeme
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
                LayoutInflater.from(parent.context).inflate( R.layout.item_swipe_meme, parent, false)
        )
        viewHolder.memeTitle.setOnClickListener {
            listener.openInCustomTab(differ.currentList[viewHolder.bindingAdapterPosition])
        }
        viewHolder.shareMemeButton.setOnClickListener {
            listener.shareMeme(differ.currentList[viewHolder.bindingAdapterPosition])
        }
        viewHolder.memeImg.setOnLongClickListener {
            listener.showMemeInDialog(differ.currentList[viewHolder.bindingAdapterPosition])
            true
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: SwipeMemesViewHolder, position: Int) {
        val jsonObject = differ.currentList[position]

        holder.itemView.apply {
            Glide.with(holder.itemView.context).load(jsonObject.getString("url")).into(ivSwipeMeme)
            tvTitleSMF.text = jsonObject.getString("title")
            tvUpsSMF.text = jsonObject.getString("ups")
            tvSubredditSMF.text = jsonObject.getString("subreddit")
            tvAuthorSMF.text = jsonObject.getString("author")
        }

        if(position >= differ.currentList.size - 3)listener.addMemesToMemeList()
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

interface ISwipeMemesAdapter{
    fun addMemesToMemeList()
    fun shareMeme(meme : JSONObject)
    fun openInCustomTab(meme : JSONObject)
    fun showMemeInDialog(meme : JSONObject)
}
