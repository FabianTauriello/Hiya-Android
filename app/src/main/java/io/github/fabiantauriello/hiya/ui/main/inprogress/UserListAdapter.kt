package io.github.fabiantauriello.hiya.ui.main.inprogress

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import io.github.fabiantauriello.hiya.R
import io.github.fabiantauriello.hiya.databinding.UserListItemBinding
import io.github.fabiantauriello.hiya.domain.User

class UserListAdapter(
    private val users: ArrayList<User>,
    private val listener: UserClickListener
) : RecyclerView.Adapter<UserListAdapter.UserItemViewHolder>() {

    private val LOG_TAG = this::class.simpleName

    class UserItemViewHolder(
        val binding: UserListItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserItemViewHolder {
        val binding = DataBindingUtil.inflate<UserListItemBinding>(
            LayoutInflater.from(parent.context),
            R.layout.user_list_item,
            parent,
            false
        )
        return UserItemViewHolder(binding)
    }

    override fun getItemCount() = users.size

    override fun onBindViewHolder(holder: UserItemViewHolder, position: Int) {
        holder.binding.user = users[position]

        // set click listener
        holder.binding.userListItemContainer.setOnClickListener {
            listener.onUserClick(users[position])
        }

    }

    fun update(newUsers: ArrayList<User>) {
        users.clear()
        users.addAll(newUsers)
        notifyDataSetChanged()
    }


}
