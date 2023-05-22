package com.example.videoanimation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.viewbinding.ViewBinding

abstract class ViewBindingListAdapter<T>(diffCallback: DiffUtil.ItemCallback<T>) :
        ListAdapter<T, ViewBindingViewHolder<T, ViewBinding>>(diffCallback) {

    protected abstract fun getViewBinding(viewType: Int): ((LayoutInflater, ViewGroup?, Boolean) -> ViewBinding)

    abstract fun getViewHolder(viewType: Int, viewBinding: ViewBinding):
            ViewBindingViewHolder<T, ViewBinding>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewBindingViewHolder<T, ViewBinding> {
        val binding = getViewBinding(viewType).invoke(LayoutInflater.from(parent.context), parent, false)
        return getViewHolder(viewType, binding)
    }

    override fun onBindViewHolder(holder: ViewBindingViewHolder<T, ViewBinding>, position: Int) {
        getItem(position)?.let {
            holder.setData(it)
        }
    }
}
