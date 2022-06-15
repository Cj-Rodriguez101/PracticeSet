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
                    //binding.checkbox.isChecked = selectALlMode == "All"
                    binding.userDto!!.isChecked = selectALlMode == "All"
                } else {

                }
            } else {
                binding.checkbox.visibility = View.GONE
            }

        binding.executePendingBindings()
    }

    fun checkOrUnCheckView(userDto: UserDto){
        //binding.checkbox.isChecked = !binding.checkbox.isChecked
        binding.userDto!!.isChecked = !userDto.isChecked
        //binding.executePendingBindings()
    }

    fun checkAlwaysView(userDto: UserDto){
        //binding.checkbox.isChecked = !binding.checkbox.isChecked
        //if(!binding.userDto!!.isChecked) binding.userDto!!.isChecked = true
        binding.userDto!!.isChecked = true
        //binding.executePendingBindings()
    }

    fun checkIfCheckBoxChecked(status: Boolean){
        binding.userDto!!.isChecked = status
    }

    fun resetCheck(){
        binding.checkbox.isChecked = false
        binding.userDto!!.isChecked = false
    }

    companion object {
        fun create(view: ViewGroup): UserViewHolder {
            val inflater = LayoutInflater.from(view.context)
            val binding = UserListItemBinding.inflate(inflater, view, false)
            return UserViewHolder(binding)
        }
    }
}