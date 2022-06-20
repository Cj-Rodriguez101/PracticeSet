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
import com.example.practiceset2.util.buildDialog

@ExperimentalPagingApi
class EnterFragment : Fragment() {

    private var fragBinding: FragmentEnterBinding? = null
    private var generalTextWatcher: TextWatcher? = null
    private var spinnerListener: AdapterView.OnItemSelectedListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val viewModel: BaseAboutViewModel by viewModels({requireParentFragment()})
        // Inflate the layout for this fragment
        val binding = FragmentEnterBinding.inflate(inflater, container, false)
        fragBinding = binding

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

        val branchListAdapter: ArrayAdapter<String> = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item, courses
        )

        branchListAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item)
        binding.genderSpinner.adapter = branchListAdapter

        viewModel.userDto.observe(viewLifecycleOwner, Observer {userDto->
            userDto?.let {
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
                    }
                }
            }
        }

        binding.submitButton.setOnClickListener {
            viewModel.userDto.value?.let {dto->
                viewModel.insertUpdateUserDto(dto)
            }
        }

        binding.layoutStatusText.setOnCheckedChangeListener { _, b ->
                viewModel.setStatus(b)
        }

        if(spinnerListener == null){
            spinnerListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedItem =
                        parent.getItemAtPosition(position).toString() //this is your selected item
                    viewModel.setGender(selectedItem)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

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

    override fun onDestroyView() {
        fragBinding?.nameText?.removeTextChangedListener(generalTextWatcher)
        fragBinding?.emailText?.removeTextChangedListener(generalTextWatcher)
        fragBinding?.genderSpinner?.onItemSelectedListener = null
        spinnerListener = null
        fragBinding = null
        generalTextWatcher = null
        super.onDestroyView()
    }
}