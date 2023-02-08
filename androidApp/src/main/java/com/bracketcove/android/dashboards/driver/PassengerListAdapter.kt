package com.bracketcove.android.dashboards.driver

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bracketcove.android.R
import com.bracketcove.android.databinding.ListItemPassengerBinding
import com.bracketcove.domain.User
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView

class PassengerListAdapter : ListAdapter<Pair<User, Double>, PassengerListAdapter.PassengerViewHolder>(
    object: DiffUtil.ItemCallback<Pair<User, Double>>() {
        override fun areItemsTheSame(oldItem: Pair<User, Double>, newItem: Pair<User, Double>): Boolean {
            return oldItem.first.userId == newItem.first.userId
        }

        override fun areContentsTheSame(oldItem: Pair<User, Double>, newItem: Pair<User, Double>): Boolean {
            return oldItem.first == newItem.first
        }
    }
) {

    var handleItemClick: ((User) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        return PassengerViewHolder(
            ListItemPassengerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        getItem(position).apply {
            holder.username.text = first.username
            Glide.with(holder.itemView.context)
                .load(first.avatarPhotoUrl)
                .fitCenter()
                .placeholder(
                    CircularProgressDrawable(holder.itemView.context).apply {
                        setColorSchemeColors(
                            ContextCompat.getColor(holder.itemView.context, R.color.color_light_grey)
                        )

                        strokeWidth = 2f
                        centerRadius = 48f
                        start()
                    }
                )
                .into(holder.avatar)
            holder.layout.setOnClickListener { handleItemClick?.invoke(first) }
        }
    }

    inner class PassengerViewHolder constructor(binding: ListItemPassengerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val username: TextView = binding.username
        val avatar: ShapeableImageView = binding.avatar
        val layout: View = binding.listItemLayout
    }
}