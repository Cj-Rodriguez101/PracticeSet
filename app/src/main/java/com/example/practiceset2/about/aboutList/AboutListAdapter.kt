package com.example.practiceset2.about.aboutList

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.databinding.UserListItemBinding
import com.example.practiceset2.movie.MovieViewHolder
import com.example.practiceset2.network.UserDto

@ExperimentalPagingApi
class AboutListAdapter(private val onClickListener: OnClickListener, private val onLongClickListener: OnLongClickListener):
    PagingDataAdapter<UserDto, UserViewHolder>(DiffCallback){

    companion object DiffCallback: DiffUtil.ItemCallback<UserDto>(){
        override fun areItemsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: UserDto, newItem: UserDto): Boolean {
            return oldItem == newItem
        }

    }
    class OnClickListener(val clickListener: (userDto: UserDto, position: Int)-> Unit){
        fun onClick(userDto: UserDto, position: Int) = clickListener(userDto, position)
    }

    class OnLongClickListener(val longClickListener: (userDto: UserDto)-> Unit){
        fun onLongClick(userDto: UserDto) = longClickListener(userDto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        return UserViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        getItem(position)?.let { dto->
            holder.itemView.setOnClickListener {
                onClickListener.onClick(dto, position)
                if (AboutListFragment.isContextualMode){
                    holder.checkOrUnCheckView(dto)
                    AboutListFragment.selectAllMode = "Default"
                    notifyItemChanged(position)
                }
            }

            holder.binding.checkbox.setOnClickListener {
                onClickListener.onClick(dto, position)
                if (AboutListFragment.isContextualMode){
                    holder.checkIfCheckBoxChecked(holder.binding.checkbox.isChecked)
                    AboutListFragment.selectAllMode = "Default"
                    notifyItemChanged(position)
                }
            }
            holder.itemView.setOnLongClickListener {
                onLongClickListener.onLongClick(dto)
                if(!AboutListFragment.isContextualMode) {
                    AboutListFragment.isContextualMode = true
                    notifyDataSetChanged()
                }
                holder.checkAlwaysView(dto)
                AboutListFragment.selectAllMode = "Default"
                notifyItemChanged(position)
            true
            }
            holder.bind(dto, AboutListFragment.isContextualMode, AboutListFragment.selectAllMode)
        }
    }
}