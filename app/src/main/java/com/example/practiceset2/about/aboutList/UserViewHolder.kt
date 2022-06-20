package com.example.practiceset2.about.aboutList

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.databinding.MovieListItemBinding
import com.example.practiceset2.databinding.UserListItemBinding
import com.example.practiceset2.movie.MovieViewHolder
import com.example.practiceset2.network.UserDto

class UserViewHolder(val binding: UserListItemBinding): RecyclerView.ViewHolder(binding.root){
    fun bind(userDto: UserDto, isContextualMode: Boolean, selectALlMode: String){
        binding.userDto = userDto
            if(isContextualMode){
                binding.checkbox.visibility = View.VISIBLE
                if (selectALlMode!= "Default"){
                    binding.userDto!!.isChecked = selectALlMode == "All"
                }
            } else {
                binding.checkbox.visibility = View.GONE
            }

        binding.executePendingBindings()
    }

    fun checkOrUnCheckView(userDto: UserDto){
        binding.userDto!!.isChecked = !userDto.isChecked
    }

    fun checkAlwaysView(userDto: UserDto){
        binding.userDto!!.isChecked = true
    }

    fun checkIfCheckBoxChecked(status: Boolean){
        binding.userDto!!.isChecked = status
    }

    companion object {
        fun create(view: ViewGroup): UserViewHolder {
            val inflater = LayoutInflater.from(view.context)
            val binding = UserListItemBinding.inflate(inflater, view, false)
            return UserViewHolder(binding)
        }
    }
}