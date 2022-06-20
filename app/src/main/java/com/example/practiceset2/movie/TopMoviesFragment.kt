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
import androidx.lifecycle.Observer
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.practiceset2.R
import com.example.practiceset2.databinding.FragmentListBinding
import com.example.practiceset2.databinding.FragmentMovieBinding
import com.example.practiceset2.list.ListDetailViewModelFactory
import com.example.practiceset2.list.ListViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalPagingApi
class TopMoviesFragment : Fragment() {

    var binding: FragmentMovieBinding? = null
    var adapter: MovieAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (adapter==null){
            adapter = MovieAdapter()
        }
        val fragmentBinding = FragmentMovieBinding.inflate(inflater, container, false)
        val viewModel by viewModels<MovieViewModel>{
            MovieViewModelFactory((requireContext().applicationContext))
        }
        binding = fragmentBinding

        binding?.apply {
            //for header and footer with retry button
            movieRecycle.adapter = adapter!!.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter{adapter!!.retry()},
                footer = MovieLoadStateAdapter{adapter!!.retry()}
            )

            lifecycleOwner = viewLifecycleOwner
            movieViewModel = viewModel

            retryMovieButton.setOnClickListener {
                adapter!!.retry()
            }
        }

            viewModel.movies.observe(viewLifecycleOwner, Observer { data->
                if (data!=null){
                    Log.e("this", "data ${data}")
                    viewLifecycleOwner.lifecycleScope.launch {
                        adapter?.submitData(data)
                    }
                }
            })

        adapter?.loadStateFlow?.asLiveData(viewLifecycleOwner.lifecycleScope.coroutineContext)?.observe(viewLifecycleOwner) { loadState ->
            //for progress bar when loading
            binding?.movieRecycle?.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            binding?.movieProgressBar?.isVisible = loadState.mediator?.refresh is LoadState.Loading
            binding?.retryMovieButton?.isVisible = loadState.mediator?.refresh is LoadState.Error
            handleError(loadState)
        }
        setHasOptionsMenu(true)
        return fragmentBinding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
        adapter = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
    }

    private fun FragmentMovieBinding.bindState(movieViewModelParam: MovieViewModel){

        this.apply {
            //for header and footer with retry button
            movieRecycle.adapter = adapter!!.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter{adapter!!.retry()},
                footer = MovieLoadStateAdapter{adapter!!.retry()}
            )

            adapter?.addLoadStateListener {loadState->
                //for progress bar when loading
                movieRecycle.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
                movieProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
                retryMovieButton.isVisible = loadState.mediator?.refresh is LoadState.Error
                handleError(loadState)
            }
            lifecycleOwner = viewLifecycleOwner
            movieViewModel = movieViewModelParam

            retryMovieButton.setOnClickListener {
                adapter!!.retry()
            }
        }
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.mediator?.append as? LoadState.Error
            ?: loadState.mediator?.prepend as? LoadState.Error

        errorState?.let {
            Toast.makeText(activity?.applicationContext, "${it.error}", Toast.LENGTH_LONG).show()
        }
    }
}