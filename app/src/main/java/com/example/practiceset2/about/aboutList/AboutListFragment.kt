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
                    //(requireParentFragment() as BaseAboutFragment).setUserDto(it)
                    (requireParentFragment() as BaseAboutFragment).navigateToScreen(0)
                    //Log.e("clicked", "clicked ${it}")
                } else {
                    viewModel.addOrRemoveSelectedUsers(dto)
                    //toolBar.title = "${viewModel.selectedUsers.value?.size} Item Selected"
                    //add or remove this id to list
                    //check and uncheck this item
                        //val yes: UserDto = adapter?.getItem(position)!!
                    //adapter?.updateCheckedState(position)
                }
            }, onLongClickListener = AboutListAdapter.OnLongClickListener{
                Log.e("longClick", "long ${it}")
                viewModel.addOrRemoveSelectedUsers(it)
//                for (count in 0 until binding.aboutListRecycle.childCount){
//                    val holder: UserViewHolder = binding.aboutListRecycle.getChildViewHolder(binding.aboutListRecycle.getChildAt(count)) as UserViewHolder
//                    holder.resetCheck()
//                }

//                if (!isContextualMode && binding.aboutListRecycle.childCount==0) {
//                    for (count in 0 until binding.aboutListRecycle.childCount){
//                        binding.aboutListRecycle.getChildViewHolder(binding.aboutListRecycle.getChildAt(count))?.let { holder->
//                            holder.itemView
//                        }
//                    }
//                }
                if (!isContextualMode) {
                    toolBar.menu.clear()
                    toolBar.inflateMenu(R.menu.about_contextual_menu)
                    val checkbox = toolBar.menu.findItem(R.id.select_all).actionView as CheckBox
                    checkbox.setOnCheckedChangeListener { compoundButton, b ->
                        //Log.e("tag", "select_all")
                        selectDeselectItems()
                    }
                    //toolBar.title = "${viewModel.selectedUsers.value?.size} Item Selected"
                }
            })

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
                override fun handleOnBackPressed() {
                    Log.e("backtrack","back pressed here")
//                    if (isContextualMode){
//                        isContextualMode = false
//                        toolBar.title = "About"
//
////                        val list : List<UserDto?> = adapter!!.snapshot().map { dto->
////                            dto?.copy(isChecked = false)
////                        }
//
//                        //adapter!!.snapshot().items.find { it.isChecked }?.isChecked = false
//
//                        adapter?.notifyDataSetChanged()
//                        toolBar.inflateMenu(R.menu.about_menu)
//                    } else {
//                        backCallback?.remove()
//                        backCallback?.isEnabled = false
//                        adapter?.notifyDataSetChanged()
//                        findNavController().popBackStack()
//                    }
//                    viewModel.clearList()
                    resetContextualMode()
                }

            }
        }



        requireActivity().onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, backCallback!!)



//        val viewModel by viewModels<AboutListViewModel>{
//            AboutListViewModelFactory((requireContext().applicationContext))
//        }
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
                                //resetContextualMode()
                                viewModel.resetInfo()
                            })
                        resetContextualMode()
                        viewModel.resetInfo()
                        Log.e("result", "from enter fragment")
                    }
                }
            }
        }
        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        //binding = null
        //adapter?.removeLoadStateListener {  }
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
            //Log.e("tag", "select_all ${item.isChecked}")
            //selectDeselectItems()
            return true
        } else if (item.itemId == R.id.delete_all){
            Log.e("tag", "delete all")
            //create dialong to ask are you sure
            val userList = viewModel.selectedUsers.value?: listOf<UserDto>()
            requireContext().buildAreYouSureDialog(userList, {

            }, {viewModel.deleteUsers(userList)})
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun selectDeselectItems() {
        viewModel.selectedUsers.value?.let {
//            if (it.isNullOrEmpty()){
//                selectAllMode = "All"
//            } else {
//                selectAllMode = "None"
//            }
            val size = adapter?.snapshot()?.items?.size?:0

            val list = adapter?.snapshot()?.items

            if(it.size == size){
                selectAllMode = "None"
                viewModel.clearList()
                //toolBar.title = "${viewModel.selectedUsers.value?.size} Item Selected"
            } else{
                selectAllMode = "All"
                viewModel.clearList()
                list?.let { list1->
                    viewModel.addAllSelectedItems(list1)
                    //toolBar.title = "${list1.size} Item Selected"
                }
            }
            adapter?.notifyDataSetChanged()
        }
    }

    private fun resetContextualMode() {
        if (isContextualMode) {
            isContextualMode = false
            //toolBar.title = "About"

            adapter?.snapshot()?.items?.filter { it.isChecked }?.forEach {
                it.isChecked = false }
            adapter?.notifyDataSetChanged()
            toolBar.menu.clear()
            toolBar.inflateMenu(R.menu.about_menu)
            toolBar.inflateMenu(R.menu.overflow_menu)
        } else {
            backCallback?.remove()
            backCallback?.isEnabled = false
            adapter?.notifyDataSetChanged()
            findNavController().popBackStack()
        }
        viewModel.clearList()
        selectAllMode = "Default"
    }
}