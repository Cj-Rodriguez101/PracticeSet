package com.example.practiceset2.movie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.paging.ExperimentalPagingApi
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.practiceset2.R
import com.example.practiceset2.databinding.FragmentBaseMovieBinding
import com.google.android.material.tabs.TabLayoutMediator

/**
 * A simple [Fragment] subclass.
 * Use the [BaseMovieFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BaseMovieFragment : Fragment() {

    private var demoCollectionAdapter: DemoCollectionAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        if (demoCollectionAdapter == null){
            demoCollectionAdapter = DemoCollectionAdapter(childFragmentManager, viewLifecycleOwner.lifecycle)
        }
        val binding = FragmentBaseMovieBinding.inflate(inflater, container, false)
        binding.pager.adapter = demoCollectionAdapter
        val tabTitles = arrayOf(resources.getString(R.string.top_rated),
            resources.getString(R.string.discover))

        TabLayoutMediator(binding.tabLayout, binding.pager) { tab, position ->
            tab.text = tabTitles[position]
            binding.pager.setCurrentItem(tab.position, true)
        }.attach()
        return binding.root
    }

    override fun onDestroyView() {
        demoCollectionAdapter = null
        super.onDestroyView()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, findNavController()) ||
                super.onOptionsItemSelected(item)
    }
}

class DemoCollectionAdapter(fragmentManager: FragmentManager, lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {

    override fun getItemCount(): Int = 2

    @OptIn(ExperimentalPagingApi::class)
    override fun createFragment(position: Int): Fragment {

        return when(position){

            0-> TopMoviesFragment()

            else -> DiscoverFragment()
        }
    }
}
