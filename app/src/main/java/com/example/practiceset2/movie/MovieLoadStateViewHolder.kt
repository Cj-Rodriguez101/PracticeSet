package com.example.practiceset2.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.databinding.ItemLoadStateBinding
import com.example.practiceset2.databinding.MovieListItemBinding

class MovieLoadStateViewHolder(val binding: ItemLoadStateBinding, retry: ()-> Unit): RecyclerView.ViewHolder(binding.root) {

    init {
        binding.retryButton.setOnClickListener { retry.invoke() }
    }

    fun bind(loadState: LoadState){
        if (loadState is LoadState.Error){
            binding.errorMsg.text = loadState.error.localizedMessage
        }
    }
    companion object {
        fun create(view: ViewGroup, retry: ()-> Unit): MovieLoadStateViewHolder{
            val inflater = LayoutInflater.from(view.context)
            val binding = ItemLoadStateBinding.inflate(inflater, view, false)
            return MovieLoadStateViewHolder(binding, retry)
        }
    }
}