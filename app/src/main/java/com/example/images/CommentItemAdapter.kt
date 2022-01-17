package com.example.images

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CommentItemAdapter(private val postsItems: MutableList<CommentsModel>): RecyclerView.Adapter<CommentItemAdapter.PostItemViewHolder>() {

    class PostItemViewHolder(listItemView: View): RecyclerView.ViewHolder(listItemView){
        val emailTextView: TextView = listItemView.findViewById(R.id.textViewEmail)
        val nameTextView: TextView = listItemView.findViewById(R.id.textViewName)
        val bodyTextView: TextView = listItemView.findViewById(R.id.textViewBody)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostItemViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.list_item, parent, false)
        return PostItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PostItemViewHolder, position: Int) {
        val postItem = postsItems[position]
        holder.emailTextView.text = postItem.email
        holder.nameTextView.text = postItem.name
        holder.bodyTextView.text = postItem.body
    }

    override fun getItemCount(): Int {
        return postsItems.size
    }
}