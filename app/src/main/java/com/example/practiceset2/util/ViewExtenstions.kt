package com.example.practiceset2.util

import android.content.Context
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.practiceset2.R
import com.example.practiceset2.network.UserDto

fun Context.buildDialog(title: String = "Success", isError: Boolean, description: String, onDismiss: ()-> Unit){
    AlertDialog.Builder(this).apply {
        setTitle(title)
        setMessage(description)
        setOnDismissListener { onDismiss() }
        setIcon(if (!isError) R.drawable.ic_success_check_circle_24 else R.drawable.ic_baseline_error_24)
    }.show()
}

fun Context.buildAreYouSureDialog(selectList: List<UserDto>,
                                  onDismiss: ()-> Unit, onSuccess: (selectList: List<UserDto>)-> Unit){
    val selectSize = selectList.size
    val deleteString = when{
        selectSize>1->{
            "these $selectSize items"
        }

        selectSize == 0 -> {
            "No Items to Delete"
        }

        else -> {
            "this ${selectList[0].name}"
        }
    }

    if (!selectList.isEmpty()){
        AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
            setTitle("Are You Sure")
            setMessage("Are you sure you want to delete $deleteString")
            setOnDismissListener { onDismiss() }
                .setPositiveButton("Yes", DialogInterface.OnClickListener { dialogInterface, i ->
                    onSuccess.invoke(selectList)
                })
                .setNegativeButton("No", DialogInterface.OnClickListener { dialogInterface, i ->
                    onDismiss.invoke()
                })
            setIcon(R.drawable.ic_baseline_info_24)
        }.show()
    } else {
        AlertDialog.Builder(this, R.style.AlertDialogTheme).apply {
            setTitle("No Items")
            setMessage(deleteString)
            setOnDismissListener { onDismiss() }
            setIcon(R.drawable.ic_baseline_info_24)
        }.show()
    }

}