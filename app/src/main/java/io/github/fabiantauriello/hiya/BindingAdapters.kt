package io.github.fabiantauriello.hiya

import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.app.Hiya
import io.github.fabiantauriello.hiya.domain.FirestoreResponse
import io.github.fabiantauriello.hiya.domain.QueryStatus
import io.github.fabiantauriello.hiya.domain.Story


@BindingAdapter("img")
fun bindImg(iv: ImageView, profilePic: String) {
    val options: RequestOptions = RequestOptions()
//            .override(450, 600)
        .error(R.drawable.ic_profile_filled)
    Glide.with(iv.context)
        .load(profilePic)
        .circleCrop()
        .apply(options)
        .into(iv)
}