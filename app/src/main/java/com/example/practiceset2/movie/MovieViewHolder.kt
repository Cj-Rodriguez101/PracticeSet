package com.example.practiceset2.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.databinding.MovieListItemBinding
import com.example.practiceset2.domain.MovieDevItem

class MovieViewHolder(val binding: MovieListItemBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(movieDevItem: MovieDevItem){
        binding.movieDevItem = movieDevItem
        binding.executePendingBindings()
    }

    companion object {
        fun create(view: ViewGroup): MovieViewHolder{
            val inflater = LayoutInflater.from(view.context)
            val binding = MovieListItemBinding.inflate(inflater, view, false)
            return MovieViewHolder(binding)
        }
    }
}