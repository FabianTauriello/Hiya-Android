package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.ContactListItemBinding
import io.github.fabiantauriello.hiya.domain.User

class ContactSelectionAdapter(
    private val contactList: ArrayList<User>,
    private val listener: ContactClickListener
) : RecyclerView.Adapter<ContactSelectionAdapter.ContactItemViewHolder>() {

    private val LOG_TAG = this::class.simpleName

    class ContactItemViewHolder(
        val binding: ContactListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactItemViewHolder {
        val binding = DataBindingUtil.inflate<ContactListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.contact_list_item,
            parent,
            false
        )
        return ContactItemViewHolder(binding)
    }

    override fun getItemCount() = contactList.size

    override fun onBindViewHolder(holder: ContactItemViewHolder, position: Int) {
        // TODO use data binding here
        // set image
        val options: RequestOptions = RequestOptions()
//            .override(450, 600)
            .error(R.drawable.ic_broken_image)

        Glide.with(holder.binding.ivContactPicture.context)
            .load(contactList[position].profileImageUri)
            .apply(options)
            .into(holder.binding.ivContactPicture)

        // set name
        holder.binding.tvContactName.text = contactList[position].name

        // set click listener
        holder.binding.layoutContactItem.setOnClickListener {
            listener.onContactClick(contactList[position])
        }

    }


}
