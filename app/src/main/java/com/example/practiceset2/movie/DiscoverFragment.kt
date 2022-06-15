package com.example.practiceset2.movie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import com.example.practiceset2.R
import com.example.practiceset2.databinding.FragmentDiscoverBinding
import kotlinx.coroutines.launch


@ExperimentalPagingApi
class DiscoverFragment : Fragment() {

    var adapter: MovieAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        if (adapter==null){
            adapter = MovieAdapter()
        }
        val binding = FragmentDiscoverBinding.inflate(inflater, container, false)

        val viewModel by viewModels<DiscoverMovieViewModel>{
            DiscoverMovieViewModelFactory((requireContext().applicationContext))
        }
        binding.apply {

            discoverMovieRecycle.adapter = adapter!!.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter{adapter!!.retry()},
                footer = MovieLoadStateAdapter{adapter!!.retry()}
            )

            lifecycleOwner = viewLifecycleOwner
            discoverMovieViewModel = viewModel

            discoverRetryMovieButton.setOnClickListener {
                adapter!!.retry()
            }
        }

        viewModel.movies.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    adapter?.submitData(data)
                }
            }
        }

        adapter?.loadStateFlow?.asLiveData(viewLifecycleOwner.lifecycleScope.coroutineContext)?.observe(viewLifecycleOwner) { loadState ->
            //for progress bar when loading
            binding?.discoverMovieRecycle?.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            binding?.discoverMovieProgressBar?.isVisible = loadState.mediator?.refresh is LoadState.Loading
            binding?.discoverRetryMovieButton?.isVisible = loadState.mediator?.refresh is LoadState.Error
            handleError(loadState)
        }
        setHasOptionsMenu(true)
        return binding.root
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.mediator?.append as? LoadState.Error
            ?: loadState.mediator?.prepend as? LoadState.Error

        errorState?.let {
            Toast.makeText(activity?.applicationContext, "${it.error}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
        //adapter?.removeLoadStateListener {  }
        adapter = null
    }
}