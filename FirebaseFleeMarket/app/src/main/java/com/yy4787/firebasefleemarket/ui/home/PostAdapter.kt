package com.yy4787.firebasefleemarket.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yy4787.firebasefleemarket.data.post.Post
import com.yy4787.firebasefleemarket.databinding.PostItemBinding
import java.util.Locale

class PostAdapter : ListAdapter<Post, PostAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: PostItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(adapterPosition)
                }
            }
        }

        fun bind(model: Post) {
            with(binding) {
                textViewPostTitle.text = model.title
                textViewPostIsSoldOut.text = if (model.soldOut) "판매완료" else "판매중"
                val price = String.format(Locale.getDefault(), "%d원", model.price)
                textViewPostPrice.text = price
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.onItemClickListener = listener;
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context);
        val binding = PostItemBinding.inflate(layoutInflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class DiffUtilCallback : DiffUtil.ItemCallback<Post>() {
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem == newItem
        }
    }

}