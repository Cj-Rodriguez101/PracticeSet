package com.example.practiceset2.about

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.practiceset2.R
import com.example.practiceset2.about.aboutList.AboutListFragment
import com.example.practiceset2.about.enter.EnterFragment
import com.example.practiceset2.about.notifcation.NotificationFragment
import com.example.practiceset2.databinding.FragmentBaseAboutBinding
import com.example.practiceset2.network.UserDto
import timber.log.Timber

//import com.example.practiceset2.databinding.FragmentAboutBinding
//private var viewUserDto: UserDto? = null
private const val ARG_PARAM1 = "param1"
private var param1: UserDto? = null

@ExperimentalPagingApi
class BaseAboutFragment : Fragment() {

    private var demoCollectionAdapter: DemoCollectionAdapter? = null
    private var fragBinding: FragmentBaseAboutBinding? = null
    private var callback: ViewPager2.OnPageChangeCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        val viewModel = ViewModelProvider(this,
            BaseAboutViewModelFactory((requireContext().applicationContext))).get(BaseAboutViewModel::class.java)
        arguments?.let {
            param1 = it.getParcelable(ARG_PARAM1)
            param1?.let { ram->
                viewModel.setUserDto(ram)
            }
            Timber.e("yup ${it.getParcelable<UserDto>(ARG_PARAM1)}")
            arguments?.clear()
        }
        if (demoCollectionAdapter == null) {
            demoCollectionAdapter = DemoCollectionAdapter(
                childFragmentManager,
                viewLifecycleOwner.lifecycle
            )
        }



        // Inflate the layout for this fragment
        val binding = FragmentBaseAboutBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        fragBinding = binding
        //binding.pager.isUserInputEnabled = false
        binding.pager.adapter = demoCollectionAdapter

        //setOnNavigationItemSelectedListener

        binding.bottomNavigationView.setOnItemSelectedListener {item->
            when(item.itemId){
                R.id.enterFragment-> {
                    binding.pager.setCurrentItem(0, true)
                }

                R.id.aboutListFragment-> binding.pager.setCurrentItem(1, true)

                R.id.notificationFragment-> binding.pager.setCurrentItem(2, true)
            }
            true
        }
        if (callback == null){
            callback = object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    if (position == 0){
                        if (binding.bottomNavigationView.selectedItemId != R.id.enterFragment){
                            binding.bottomNavigationView.selectedItemId = R.id.enterFragment
                        }
                    } else if(position == 1) {
                        if (binding.bottomNavigationView.selectedItemId != R.id.aboutListFragment){
                            binding.bottomNavigationView.selectedItemId = R.id.aboutListFragment
                        }
                    } else {
                        if (binding.bottomNavigationView.selectedItemId != R.id.notificationFragment){
                            binding.bottomNavigationView.selectedItemId = R.id.notificationFragment
                        }
                    }
                    super.onPageSelected(position)
                }
            }
        }
        binding.pager.registerOnPageChangeCallback(callback!!)

        return binding.root

    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.about_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){

            R.id.menu_authenticate-> {
                true
            }

            else -> {
                NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                        super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onDestroyView() {
        demoCollectionAdapter = null
        callback?.let {
            fragBinding?.pager?.unregisterOnPageChangeCallback(it)
        }
        callback = null
        fragBinding = null
        super.onDestroyView()
    }

    fun navigateToScreen(index: Int){
        fragBinding?.pager?.setCurrentItem(index, true)
    }

//    fun setUserDto(selectedUserDto: UserDto?){
//        //viewUserDto = selectedUserDto
//        viewModel.setUserDto(selectedUserDto?:UserDto())
//        Log.e("setUserDto", "${selectedUserDto}")
//        fragBinding?.pager?.setCurrentItem(0, true)
//    }
}

class DemoCollectionAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 3

    @OptIn(ExperimentalPagingApi::class)
    override fun createFragment(position: Int): Fragment {

        return when(position){

            0-> {
//                viewUserDto?.let {
//                    //notifyItemChanged(0)
//                    EnterFragment.newInstance(it)
//                }?:EnterFragment()
                EnterFragment()
            }

            1 -> AboutListFragment()

            else -> NotificationFragment()
        }
    }
}