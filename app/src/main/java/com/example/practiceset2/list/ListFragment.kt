package com.example.practiceset2.list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.practiceset2.R
import com.example.practiceset2.VideoApplication
import com.example.practiceset2.databinding.FragmentListBinding
import com.example.practiceset2.local.VideoLocalDataSource
import com.example.practiceset2.network.UserDto
import kotlinx.coroutines.launch

class ListFragment : Fragment() {

    var binding: FragmentListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val fragmentBinding = FragmentListBinding.inflate(inflater, container, false)

        val viewModel by viewModels<ListViewModel>{
            ListDetailViewModelFactory((requireContext().applicationContext))
        }
        binding = fragmentBinding
        binding?.apply {
            lifecycleOwner = viewLifecycleOwner
            listViewModel = viewModel
            listRecycle.adapter = ListRecyclerViewAdapter(ListRecyclerViewAdapter.OnClickListener{
                val navController =  root.findNavController()
                navController.navigate(ListFragmentDirections.actionListFragmentToDetailFragment(it))
            })

            navigateButton.setOnClickListener {
                val navController =  root.findNavController()
                navController.navigate(R.id.action_listFragment_to_detailFragment)
            }
        }

//        viewModel.items.observe(viewLifecycleOwner, Observer {
//            it?.let {list->
//                Log.e("items", "${list.map { it.title }.joinToString(",")}")
//            }
//        })
        setHasOptionsMenu(true)
        return fragmentBinding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}