package com.example.videoanimation

import android.content.Context
import android.content.res.Resources
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

abstract class ViewBindingViewHolder<T, out VB : ViewBinding>(private val _binding: ViewBinding,
    onViewHolderClickListener: (OnItemViewClickListener)? = null) : RecyclerView.ViewHolder(_binding.root) {

    var mContext: Context = itemView.context
    protected val binding: VB
        get() = _binding as VB

    init {
        itemView.setOnClickListener {
            onViewHolderClickListener?.invoke(adapterPosition)
        }
    }

    abstract fun setData(data: T)

    protected fun getString(@StringRes id: Int) =
        mContext.getString(id)

    val resources: Resources
        get() = mContext.resources
}

typealias OnItemViewClickListener = (Int) -> Unit