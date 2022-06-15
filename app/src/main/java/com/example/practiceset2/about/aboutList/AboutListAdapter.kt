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
//        val userDto = getItem(position)
//        userDto?.let {
//            holder.itemView.setOnClickListener {
//                onClickListener.onClick(userDto)
//            }
//            holder.bind(userDto)
//        }
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
                if (AboutListFragment.isContextualMode){

                } else {
                    AboutListFragment.isContextualMode = true
                    //holder.checkOrUnCheckView()
                    //snapshot().items.find { it.id!=1000000000 }?.isChecked = false
                    /*
                    edit multiple items in a list
                    https://stackoverflow.com/a/54800376/13412502
                     */
//                    snapshot().items.filter { it.isChecked }.forEach {
//                        it.isChecked = false }
                    //snapshot().find { it!!.isChecked || !it.isChecked}?.isChecked = false
                    //AboutListFragment.selectAllMode = "Default"
                    notifyDataSetChanged()
                }
                holder.checkAlwaysView(dto)
                AboutListFragment.selectAllMode = "Default"
                notifyItemChanged(position)
            true
            }
//            if (AboutListFragment.isContextualMode){
//                holder.itemView as UserListItemBinding
//            } else {
//
//            }
            holder.bind(dto, AboutListFragment.isContextualMode, AboutListFragment.selectAllMode)
        }
    }
}