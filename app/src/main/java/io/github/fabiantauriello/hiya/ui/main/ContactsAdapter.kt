package io.github.fabiantauriello.hiya.ui.main

import android.database.Cursor
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.ContactItemBinding
import java.util.*
import kotlin.collections.ArrayList

class ContactsAdapter(
    private val list: ArrayList<Pair<String, String>>,
    private val listener: ContactClickListener
) : RecyclerView.Adapter<ContactsAdapter.ContactItemViewHolder>() {

    private val LOG_TAG = this::class.simpleName

    class ContactItemViewHolder(
        val binding: ContactItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val binding = DataBindingUtil.inflate<ContactItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.contact_item,
            parent,
            false
        )
        return ContactItemViewHolder(binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
//        cursor.moveToPosition(position)

//        val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//        val number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))


        // set image
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_broken_image)
        Glide.with(holder.binding.ivContactPicture.context)
            .load(list[position].second)
            .apply(options)
            .into(holder.binding.ivContactPicture)

        // set name
        holder.binding.tvContactName.text = list[position].first

        // set click listener
        holder.binding.layoutContactItem.setOnClickListener {
            listener.onContactClick()
        }

    }

    fun updateList(item: Pair<String, String>) {
        list.add(item)
        list.sortBy { it.first }
        notifyDataSetChanged()
    }

}
