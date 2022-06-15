package com.example.practiceset2.about.notifcation

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.paging.ExperimentalPagingApi
import com.example.practiceset2.R
import com.example.practiceset2.about.BaseAboutViewModel
import com.example.practiceset2.databinding.FragmentBaseAboutBinding
import com.example.practiceset2.databinding.FragmentNotificationBinding
import com.example.practiceset2.movie.MovieViewModel
import com.example.practiceset2.movie.MovieViewModelFactory

@ExperimentalPagingApi
class NotificationFragment : Fragment() {

    private var fragBinding: FragmentNotificationBinding? = null

    private val viewModel: BaseAboutViewModel by viewModels({requireParentFragment()})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentNotificationBinding.inflate(inflater, container, false)
//        val viewModel by viewModels<NotificationViewModel>{
//            NotificationViewModelFactory()
//        }
        fragBinding = binding
        //val pickerValues = arrayOf("2", "4", "6", "8", "10" ,"12", "14", "16", "18", "20", "22", "24")
        binding.apply {
            numPicker.minValue = 0
            numPicker.maxValue = 12
            //numPicker.displayedValues = pickerValues
            lifecycleOwner = viewLifecycleOwner
            notificationViewModel = viewModel
            numPicker.setOnValueChangedListener { numberPicker, i, i2 ->
                //Log.e("test", "no. ${pickerValues[i2]}")
                viewModel.setHours(i2)
            }
            notificationButton.setOnClickListener {
                viewModel.createInstantNotification()
            }

            scheduleButton.setOnClickListener {
                if (binding.scheduleButton.text == "Schedule"){
                    //schedule periodic work
                    val sharedPreferences = context?.applicationContext
                        ?.getSharedPreferences("ShouldSchedule", Context.MODE_PRIVATE)
                    sharedPreferences?.edit()?.let {
                        it.putBoolean("ShouldSchedule", true)
                        it.putString("TimeSchedule", viewModel.selectedHours.value)
                        it.apply()
                    }
                    viewModel.createPeriodicNotification()
                } else{
                    //cancel work
                    val sharedPreferences = context?.applicationContext
                        ?.getSharedPreferences("ShouldSchedule", Context.MODE_PRIVATE)
                    sharedPreferences?.edit()?.let {
                        it.putBoolean("ShouldSchedule", false)
                        it.remove("TimeSchedule")
                        it.apply()
                    }
                    viewModel.cancelPeriodicWork()
                }
            }
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        fragBinding = null
    }
}