package com.example.practiceset2.movie

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.databinding.MovieListItemBinding
import com.example.practiceset2.domain.MovieDevItem

class MovieAdapter: PagingDataAdapter<MovieDevItem, MovieViewHolder>(COMPARATOR) {

    companion object COMPARATOR: DiffUtil.ItemCallback<MovieDevItem>(){
        override fun areItemsTheSame(oldItem: MovieDevItem, newItem: MovieDevItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: MovieDevItem, newItem: MovieDevItem): Boolean {
            return oldItem == newItem
        }

    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
//        val movieDevItem = getItem(position)!!
//        holder.bind(movieDevItem)

        getItem(position)?.let { holder.bind(it) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder.create(parent)
    }

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }
}