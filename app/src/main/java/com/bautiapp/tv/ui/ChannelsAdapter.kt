package com.bautiapp.tv.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.bautiapp.tv.R
import com.bautiapp.tv.data.Channel
import com.squareup.picasso.Picasso

class ChannelsAdapter(
    private val onChannelClick: (Channel) -> Unit
) : ListAdapter<Channel, ChannelsAdapter.ChannelViewHolder>(ChannelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChannelViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_channel, parent, false)
        return ChannelViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChannelViewHolder, position: Int) {
        holder.bind(getItem(position), onChannelClick)
    }

    class ChannelViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        private val channelLogo: ImageView = itemView.findViewById(R.id.channel_logo)
        private val channelName: TextView = itemView.findViewById(R.id.channel_name)
        private val channelGroup: TextView? = itemView.findViewById(R.id.channel_group)

        fun bind(channel: Channel, onChannelClick: (Channel) -> Unit) {
            channelName.text = channel.name
            channelGroup?.text = channel.group
            
            val imageUrl = channel.poster ?: channel.logo
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(channelLogo)
            } else {
                channelLogo.setImageResource(android.R.drawable.ic_media_play)
            }
            
            itemView.setOnClickListener { onChannelClick(channel) }
        }
    }

    class ChannelDiffCallback : DiffUtil.ItemCallback<Channel>() {
        override fun areItemsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem.id == newItem.id && oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Channel, newItem: Channel): Boolean {
            return oldItem == newItem
        }
    }
}
