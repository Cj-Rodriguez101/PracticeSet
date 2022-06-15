package com.example.practiceset2.about.enter

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.paging.ExperimentalPagingApi
import com.example.practiceset2.about.BaseAboutFragment
import com.example.practiceset2.about.BaseAboutViewModel
import com.example.practiceset2.databinding.FragmentEnterBinding
import com.example.practiceset2.network.UserDto
import com.example.practiceset2.util.DataState
import com.example.practiceset2.util.TextInputHelper
import com.example.practiceset2.util.UIComponentType
import com.example.practiceset2.util.buildDialog
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
@ExperimentalPagingApi
class EnterFragment : Fragment()
    //, TextWatcher
    //, View.OnFocusChangeListener
{

    private var fragBinding: FragmentEnterBinding? = null
    private var generalTextWatcher: TextWatcher? = null
    private var spinnerListener: AdapterView.OnItemSelectedListener? = null

//    private val viewModel by viewModels<EnterViewModel>{
//        EnterViewModelFactory((requireContext().applicationContext))
//    }

//    private val viewModel: BaseAboutViewModel by viewModels({requireParentFragment()})


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        arguments?.let {
//            param1 = it.getParcelable(ARG_PARAM1)
//        }
//        arguments?.let {
//            param1 = it.getParcelable(ARG_PARAM1)
//            param1?.let { ram->
//                viewModel.setUserDto(ram)
//            }
//        }
//        Log.e("arguments", "${arguments?.getParcelable<UserDto>(ARG_PARAM1)?:"yes"}")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel: BaseAboutViewModel by viewModels({requireParentFragment()})
//        arguments?.let {
//            param1 = it.getParcelable(ARG_PARAM1)
//            param1?.let { ram->
//                viewModel.setUserDto(ram)
//            }
//            Timber.e("yup ${it.getParcelable<UserDto>(ARG_PARAM1)}")
//            arguments?.clear()
//        }
        // Inflate the layout for this fragment
        val binding = FragmentEnterBinding.inflate(inflater, container, false)
        fragBinding = binding
        //binding.genderSpinner.setOnClickListener { it.requestFocus() }
        //binding.layoutStatusText.setOnClickListener { it.requestFocus() }
//        inputHelper = TextInputHelper(binding.submitButton).apply {
//            addViews(binding.nameText, binding.emailText)
//        }
//        binding.nameText.addTextChangedListener {
//            viewModel.setNameDto(it.toString())
//        }

        if (generalTextWatcher == null){
            generalTextWatcher = object :TextWatcher{
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                }

                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    if (p0.hashCode() == binding.nameText.text.hashCode()){
                        viewModel.setNameDto(p0.toString())
                    } else {
                        viewModel.setEmailDto(p0.toString())
                    }
                    validateTextInputs()
                }

                override fun afterTextChanged(p0: Editable?) {

                }

            }
        }
        binding.nameText.addTextChangedListener(generalTextWatcher)

        binding.emailText.addTextChangedListener(generalTextWatcher)

        binding.lifecycleOwner = viewLifecycleOwner
        binding.enterViewModel = viewModel

        val courses : ArrayList<String> = arrayListOf<String>("Male", "Female")
        //binding.emailText.onFocusChangeListener = this
        val branchListAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, courses
        )

        branchListAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = branchListAdapter

        viewModel.userDto.observe(viewLifecycleOwner, Observer {userDto->
            userDto?.let {
                //binding.nameText.setText(it.name)
                //binding.emailText.setText(it.email)
                //binding.layoutStatusText.isChecked = it.status == "Active"
//                if(binding.genderSpinner.selectedItemPosition == 0){
//                    binding.genderSpinner.setSelection()
//                }
                if (userDto.gender == "female"){
                    binding.genderSpinner.setSelection(1)
                } else {
                    binding.genderSpinner.setSelection(0)
                }

                binding.layoutStatusText.isChecked = userDto.status == "active"
            }
        })

        viewModel.info.observe(viewLifecycleOwner) { info ->
            info?.let { dataState ->
                dataState.message?.let { message->
                    if (message.id.contains("InsertUpdateUser")){
                        requireContext().buildDialog(title = if(dataState.data!=null) "Success" else "Error",
                            isError = dataState.data==null, description = dataState.message?.description?:"desc",
                            onDismiss = {
                                viewModel.resetInfo()
                                if(dataState.data!=null){
                                    viewModel.resetUserDto()
                                }
                                (requireParentFragment() as BaseAboutFragment).navigateToScreen(1)
                            })
                        viewModel.resetInfo()
                        Log.e("result", "from enter fragment")
                    }
                }
//                requireContext().buildDialog(title = if(dataState.data!=null) "Success" else "Error",
//                isError = dataState.data==null, description = dataState.message?.description?:"desc",
//                onDismiss = {
//                    viewModel.resetInfo()
//                    if(dataState.data!=null){
//                        viewModel.resetUserDto()
//                    }
//                    (requireParentFragment() as BaseAboutFragment).navigateToScreen(1)
//                })
//                viewModel.resetInfo()
//                Log.e("result", "from enter fragment")
            }
        }

        binding.submitButton.setOnClickListener {
            //it.requestFocus()
            //Log.e("result", "${binding.nameText.text.toString()} ${binding.emailText.text.toString()} ${binding.layoutStatusText.isChecked} ${binding.genderSpinner.selectedItem}")
            Log.e("result", "${viewModel.userDto.value}")
            viewModel.userDto.value?.let {dto->
                viewModel.insertUpdateUserDto(dto)
            }
        }

        binding.layoutStatusText.setOnCheckedChangeListener { compoundButton, b ->
                viewModel.setStatus(b)

        }

        if(spinnerListener == null){
            spinnerListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    //parent.requestFocus()
                    val selectedItem =
                        parent.getItemAtPosition(position).toString() //this is your selected item
                    viewModel.setGender(selectedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //parent?.requestFocus()
                }
            }
        }
        binding.genderSpinner.onItemSelectedListener = spinnerListener

        return binding.root
    }

    private fun validateTextInputs() {
        var errorCount = 0
        val emailText = fragBinding?.emailText?.text.toString()
        if (TextUtils.isEmpty(emailText) || !Patterns.EMAIL_ADDRESS.matcher(emailText).matches()) {
            fragBinding?.layoutEmailText?.error = "Enter a valid email"
            fragBinding?.layoutEmailText?.isErrorEnabled = true
            errorCount++
        } else {
            fragBinding?.layoutEmailText?.isErrorEnabled = false
        }

        fragBinding?.submitButton?.isEnabled = errorCount == 0
    }

//    override fun onFocusChange(view: View?, hasFocus: Boolean) {
////        if(!hasFocus){
////            var errorCount = 0
////            val emailText = fragBinding?.emailText?.text.toString()
////            if (TextUtils.isEmpty(emailText) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
////                fragBinding?.layoutEmailText?.error = "Enter a valid email"
////                fragBinding?.layoutEmailText?.isErrorEnabled = true
////                errorCount++
////            } else {
////                fragBinding?.layoutEmailText?.isErrorEnabled = false
////            }
////
////            fragBinding?.submitButton?.isEnabled = errorCount == 0
////        }
//    }

    override fun onDestroyView() {
        fragBinding?.nameText?.removeTextChangedListener(generalTextWatcher)
        //fragBinding?.emailText?.removeTextChangedListener(emailTextWatcher)
        fragBinding?.emailText?.removeTextChangedListener(generalTextWatcher)
        fragBinding?.genderSpinner?.onItemSelectedListener = null
//        fragBinding?.constraintEnterLayout?.clearFocus()
//        fragBinding?.emailText?.clearFocus()
//        fragBinding?.nameText?.clearFocus()
        spinnerListener = null
        fragBinding = null
//        nameTextWatcher = null
//        emailTextWatcher = null
        generalTextWatcher = null
//        inputHelper?.removeViews()
//        inputHelper = null
        super.onDestroyView()
    }

//    override fun onDestroy() {
//        super.onDestroy()
//
//    }

//    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//
//    }

//    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
//        //Log.e("onTextChanged", "p0 ${p0.toString()}")
//
//        var errorCount = 0
//        val emailText = fragBinding?.emailText?.text.toString()
//        if (TextUtils.isEmpty(emailText) || !android.util.Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
//            fragBinding?.layoutEmailText?.error = "Enter a valid email"
//            fragBinding?.layoutEmailText?.isErrorEnabled = true
//            errorCount++
//        } else {
//            fragBinding?.layoutEmailText?.isErrorEnabled = false
//        }
//
//        fragBinding?.submitButton?.isEnabled = errorCount == 0
//    }
//
//    override fun afterTextChanged(p0: Editable?) {
//        //Log.e("afterTextChanged", "p0 ${p0.toString()}")
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: UserDto) =
            EnterFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, param1)
                }
            }
    }
}