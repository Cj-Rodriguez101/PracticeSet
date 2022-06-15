package com.example.practiceset2.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.databinding.DevListItemBinding
import com.example.practiceset2.domain.VideoDevItem

class ListRecyclerViewAdapter(private val onClickListener: OnClickListener):
        ListAdapter<VideoDevItem, ListRecyclerViewAdapter.VideoDevViewHolder>(DiffCallback){

    class VideoDevViewHolder(private val binding: DevListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(videoDevItem: VideoDevItem){
            binding.videoDevItem = videoDevItem
            binding.executePendingBindings()
        }
    }

    companion object DiffCallback: DiffUtil.ItemCallback<VideoDevItem>(){
        override fun areItemsTheSame(oldItem: VideoDevItem, newItem: VideoDevItem): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: VideoDevItem, newItem: VideoDevItem): Boolean {
            return oldItem == newItem
        }

    }
    class OnClickListener(val clickListener: (videoDevItem: VideoDevItem)-> Unit){
        fun onClick(videoDevItem: VideoDevItem) = clickListener(videoDevItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideoDevViewHolder {
        return VideoDevViewHolder(DevListItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: VideoDevViewHolder, position: Int) {
        val videoDevItem = getItem(position)
        holder.itemView.setOnClickListener {
            onClickListener.onClick(videoDevItem)
        }
        holder.bind(videoDevItem)
    }
}