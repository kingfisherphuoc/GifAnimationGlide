package com.example.videoanimation

import android.os.Bundle
import android.view.LayoutInflater
import androidx.annotation.MenuRes
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class ViewBindingActivity<VB : ViewBinding> : AppCompatActivity() {
    private var _binding: ViewBinding? = null
    protected abstract val getBindingInflater: (LayoutInflater) -> VB
    protected abstract fun onViewCreated(savedInstanceState: Bundle?)

    @MenuRes
    protected open val menuResourceId = 0

    protected val binding: VB
        get() = _binding as VB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = getBindingInflater.invoke(LayoutInflater.from(this))
        setContentView(_binding!!.root)
        onViewCreated(savedInstanceState)
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}