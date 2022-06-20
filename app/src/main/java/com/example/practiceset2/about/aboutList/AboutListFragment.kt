package com.example.practiceset2.about.aboutList

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.widget.Toolbar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.practiceset2.R
import com.example.practiceset2.about.BaseAboutFragment
import com.example.practiceset2.about.BaseAboutViewModel
import com.example.practiceset2.databinding.FragmentAboutListBinding
import com.example.practiceset2.movie.MovieLoadStateAdapter
import com.example.practiceset2.network.UserDto
import com.example.practiceset2.util.buildAreYouSureDialog
import com.example.practiceset2.util.buildDialog
import kotlinx.coroutines.launch


@ExperimentalPagingApi
class AboutListFragment : Fragment() {

    var adapter: AboutListAdapter? = null
    var fragBinding: FragmentAboutListBinding? = null
    private val viewModel: BaseAboutViewModel by viewModels({requireParentFragment()})
    var backCallback: OnBackPressedCallback? = null
    private lateinit var toolBar: Toolbar

    companion object {
        var isContextualMode : Boolean = false
        var selectAllMode: String = "Default"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentAboutListBinding.inflate(inflater, container, false)
        activity?.let {
            toolBar = it.findViewById(R.id.toolbar)
        }

        if (adapter == null){
            adapter = AboutListAdapter(onClickListener = AboutListAdapter.OnClickListener {dto, position->
                if (!isContextualMode){
                    viewModel.setUserDto(dto)
                    (requireParentFragment() as BaseAboutFragment).navigateToScreen(0)
                } else {
                    viewModel.addOrRemoveSelectedUsers(dto)
                }
            }, onLongClickListener = AboutListAdapter.OnLongClickListener{
                viewModel.addOrRemoveSelectedUsers(it)
                if (!isContextualMode) {
                    toolBar.menu.clear()
                    toolBar.inflateMenu(R.menu.about_contextual_menu)
                    val checkbox = toolBar.menu.findItem(R.id.select_all).actionView as CheckBox
                    checkbox.setOnCheckedChangeListener { _,_ ->
                        selectDeselectItems()
                    }
                }
            })

            binding.fab.setOnClickListener {
                viewModel.resetUserDto()
                (requireParentFragment() as BaseAboutFragment).navigateToScreen(0)
            }

        }

        viewModel.selectedUsers.observe(viewLifecycleOwner) { selectedList ->
            selectedList?.let {
                if (it.isNotEmpty()){
                    toolBar.title = "${it.size} Item Selected"
                } else{
                    if (isContextualMode){
                        toolBar.title = "${it.size} Item Selected"
                    } else {
                        toolBar.title = "About"
                    }
                }
            }
        }

        if (backCallback == null){
            backCallback = object : OnBackPressedCallback(true){
                override fun handleOnBackPressed() { resetContextualMode() }
            }
        }



        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backCallback!!)

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            aboutListViewModel = viewModel
            aboutListRecycle.adapter = adapter!!.withLoadStateHeaderAndFooter(
                header = MovieLoadStateAdapter{adapter!!.retry()},
                footer = MovieLoadStateAdapter{adapter!!.retry()}
            )

            aboutRetryButton.setOnClickListener {
                adapter!!.retry()
            }
        }

        viewModel.aboutList.observe(viewLifecycleOwner) { data ->
            if (data != null) {
                viewLifecycleOwner.lifecycleScope.launch {
                    adapter?.submitData(data)
                    binding.aboutListRecycle.scrollToPosition(0)
                }
            }
        }
        fragBinding = binding

        adapter?.loadStateFlow?.asLiveData(viewLifecycleOwner.lifecycleScope.coroutineContext)?.observe(viewLifecycleOwner) { loadState ->
            //for progress bar when loading
            binding.aboutListRecycle.isVisible = loadState.mediator?.refresh is LoadState.NotLoading
            binding.aboutProgressBar.isVisible = loadState.mediator?.refresh is LoadState.Loading
            binding.aboutRetryButton.isVisible = loadState.mediator?.refresh is LoadState.Error
            handleError(loadState)

            loadState.mediator?.prepend?.let {
                binding.aboutListRecycle.scrollToPosition(0)
            }
        }

        viewModel.info.observe(viewLifecycleOwner) { info ->
            info?.let { dataState ->
                dataState.message?.let { message->
                    if (message.id.contains("DeleteUser")){
                        requireContext().buildDialog(title = if(dataState.data!=null) "Success" else "Error",
                            isError = dataState.data==null, description = dataState.message?.description?:"desc",
                            onDismiss = {
                                viewModel.resetInfo()
                            })
                        resetContextualMode()
                        viewModel.resetInfo()
                    }
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        fragBinding = null
        adapter = null
        backCallback = null
    }

    private fun handleError(loadState: CombinedLoadStates) {
        val errorState = loadState.mediator?.append as? LoadState.Error
            ?: loadState.mediator?.prepend as? LoadState.Error

        errorState?.let {
            Toast.makeText(activity?.applicationContext, "${it.error}", Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            // Handle the UP button here
            resetContextualMode()
            return true
        } else if (item.itemId == R.id.select_all){
            return true
        } else if (item.itemId == R.id.delete_all){
            //create dialog to ask are you sure
            val userList = viewModel.selectedUsers.value?: listOf<UserDto>()
            requireContext().buildAreYouSureDialog(selectList = userList,
                onSuccess = {viewModel.deleteUsers(userList)})
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectDeselectItems() {
        viewModel.selectedUsers.value?.let {
            val size = adapter?.snapshot()?.items?.size?:0

            val list = adapter?.snapshot()?.items

            if(it.size == size){
                selectAllMode = "None"
                viewModel.clearList()
            } else{
                selectAllMode = "All"
                viewModel.clearList()
                list?.let { list1->
                    viewModel.addAllSelectedItems(list1)
                }
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun resetContextualMode() {
        if (isContextualMode) {
            isContextualMode = false

            adapter?.snapshot()?.items?.filter { it.isChecked }?.forEach {
                it.isChecked = false }
            toolBar.menu.clear()
            toolBar.inflateMenu(R.menu.overflow_menu)
        } else {
            backCallback?.remove()
            backCallback?.isEnabled = false
            findNavController().popBackStack()
        }
        adapter?.notifyDataSetChanged()
        viewModel.clearList()
        selectAllMode = "Default"
    }
}