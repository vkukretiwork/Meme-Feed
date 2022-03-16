package com.example.android.apiapp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_memes_feed.view.*
import org.json.JSONObject

class MemesFeedAdapter( private val listener : IMemesFeedAdapter) : RecyclerView.Adapter<MemesFeedAdapter.MemesFeedViewHolder> (){

    inner class MemesFeedViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val memesFeedItem : ConstraintLayout = itemView.clItemMemesFeed
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemesFeedViewHolder {
        val viewHolder = MemesFeedViewHolder(
                LayoutInflater.from(parent.context).inflate( R.layout.item_memes_feed, parent, false)
        )

        viewHolder.memesFeedItem.setOnClickListener {
            listener.navigateToSwipeMemeFragment(differ.currentList[viewHolder.bindingAdapterPosition])
        }
        viewHolder.memesFeedItem.setOnLongClickListener {
            listener.showMemeInDialog(differ.currentList[viewHolder.bindingAdapterPosition])
            true
        }

        return viewHolder
    }

    override fun onBindViewHolder(holder: MemesFeedViewHolder, position: Int) {
        val jsonObject = differ.currentList[position]
        val previewArray = jsonObject.getJSONArray("preview")
        val itemUrl = (when(previewArray.length()){
            0 -> jsonObject.getString("url")
            1 -> previewArray[0]
            2 -> previewArray[1]
            else -> previewArray[2]
        }) as String

        holder.itemView.apply {
            Glide.with(holder.itemView.context).load(itemUrl).into(ivItemMemesFeed)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}
interface IMemesFeedAdapter{
    fun navigateToSwipeMemeFragment(meme : JSONObject)
    fun showMemeInDialog(meme : JSONObject)
}