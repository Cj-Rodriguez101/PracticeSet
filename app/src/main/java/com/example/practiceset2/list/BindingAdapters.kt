package com.example.practiceset2.list

import android.view.View
import android.widget.*
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.practiceset2.R
import com.example.practiceset2.domain.VideoDevItem

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<VideoDevItem>?) {
    val adapter = recyclerView.adapter as ListRecyclerViewAdapter
    adapter.submitList(data)
}

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context).load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
            ).into(imgView)
    }
}

@BindingAdapter("displayedValues")
fun bindDisplayedValues(numberPicker: NumberPicker, arrayVals: Array<String>){
    numberPicker.displayedValues = arrayVals
}

@BindingAdapter("shouldDisplayProgress")
fun displayProgress(progressBar: ProgressBar, shouldDisplay: Boolean){
    if (shouldDisplay){
        progressBar.visibility = View.VISIBLE
    } else {
        progressBar.visibility = View.GONE
    }
}


@BindingAdapter("subTitleText")
fun bingListSubTitle(textView: TextView, subTitle: String?){
    subTitle?.let {
        val trimmedText = it.split("\n")[0]
        textView.text = trimmedText
    }
}
