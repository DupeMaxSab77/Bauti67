package com.bautiapp.tv.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bautiapp.tv.Channel
import com.bautiapp.tv.R
import com.squareup.picasso.Picasso

class ChannelsAdapter(
    private val onChannelClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelsAdapter.ChannelViewHolder>(ChannelDiffCallback()) {
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view, onChannelClick)
    }
    
    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    class ChannelViewHolder(
        itemView: android.view.View,
        private val onChannelClick: (Channel) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        
        private val logoView: ImageView = itemView.findViewById(R.id.channel_logo)
        private val nameView: TextView = itemView.findViewById(R.id.channel_name)
        private val groupView: TextView = itemView.findViewById(R.id.channel_group)
        
        fun bind(channel: Channel) {
            nameView.text = channel.name
            groupView.text = channel.group ?: channel.source ?: ""
            
            // Load logo with initial letters as fallback
            if (!channel.logo.isNullOrEmpty()) {
                try {
                    Picasso.get()
                        .load(channel.logo)
                        .placeholder(R.drawable.ic_placeholder)
                        .error(R.drawable.ic_placeholder)
                        .into(logoView)
                } catch (e: Exception) {
                    setInitialLetters(channel.name)
                }
            } else {
                setInitialLetters(channel.name)
            }
            
            itemView.setOnClickListener {
                onChannelClick(channel)
            }
        }
        
        private fun setInitialLetters(text: String) {
            val initials = text.take(2).toUpperCase()
            logoView.scaleType = ImageView.ScaleType.CENTER
            // For text display, we'd need a custom view or use a library
        }
    }
    
    class ChannelDiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.url == newItem.url
        }
        
        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }
}
