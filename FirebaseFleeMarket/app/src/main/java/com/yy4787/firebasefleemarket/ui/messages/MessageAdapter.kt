package com.yy4787.firebasefleemarket.ui.messages

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yy4787.firebasefleemarket.data.message.Message
import com.yy4787.firebasefleemarket.databinding.MessageItemBinding
import java.time.Instant
import java.time.ZoneId
import java.util.Locale

class MessageAdapter() : ListAdapter<Message, MessageAdapter.ViewHolder>(DiffUtilCallback()) {

    inner class ViewHolder(private val binding: MessageItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(adapterPosition)
                }
            }
        }

        fun bind(model: Message) {
            with(binding) {
                textViewMessageSender.text = model.senderEmail
                textViewMessageContent.text = model.content

                val dateTime = Instant.ofEpochMilli(model.created)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime()

                with(dateTime) {
                    textViewMessageDateTime.text = String.format(
                        Locale.getDefault(),
                        "%d-%02d-%02d %02d:%02d",
                        year, monthValue, dayOfMonth, hour, minute
                    )
                }
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
        val binding = MessageItemBinding.inflate(layoutInflater, parent, false);
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }


    class DiffUtilCallback : DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem == newItem
        }
    }

}