package com.example.videoanimation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.gif.GifDrawable
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.videoanimation.databinding.ActivityMainBinding
import com.example.videoanimation.databinding.ItemPreviewBinding


class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    override val getBindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate


    override fun onViewCreated(savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = GifAdapter().apply {
                submitList(getListGifUrls())
            }
        }

    }

    private fun getListGifUrls(): List<String> {
        val list = mutableListOf<String>()
//        for (i in 1..163) {
////            list.add("https://gridproofofconcepttest.s3.eu-central-1.amazonaws.com/Gif+2+seconds+fps6/$i.gif")
//        }
        list.apply {
            add("https://media.giphy.com/media/l0MYFgIkxG9rFiN5C/giphy.gif")
            add("https://media.giphy.com/media/3o7TKNzP3V6mQfUzhC/giphy.gif")
            add("https://media.giphy.com/media/3o7TKLCt1jjvNrF3a0/giphy.gif")
            add("https://media.giphy.com/media/OBDPf1XJ7oGgE/giphy.gif")
            add("https://media.giphy.com/media/rjgbmeBqAEiXF6FCyD/giphy.gif")
            add("https://media.giphy.com/media/RCDeoJpz6zYglRxvQM/giphy.gif")
            add("https://media.giphy.com/media/3o7TKLTVfVp95MDmIo/giphy.gif")
            add("https://media.giphy.com/media/l0MYSNiwPgncS3wNq/giphy.gif")
            add("https://media.giphy.com/media/3o7TKsiL9LS4ixuRyg/giphy.gif")
            add("https://media.giphy.com/media/3o7TKQ7KHOK8b3YJ1u/giphy.gif")
            add("https://media.giphy.com/media/l0MYtzTM0DMjEJZ7y/giphy.gif")
            add("https://media.giphy.com/media/l0MYtzTM0DMjEJZ7y/giphy.gif")
            add("https://media.giphy.com/media/l0MYRkucgpRJlkFPO/giphy.gif")
            add("https://media.giphy.com/media/3o7TKAWVvqEliCTdh6/giphy.gif")
            add("https://media.giphy.com/media/3o7TKqDoRYYymET4Pe/giphy.gif")
        }
        return list;
    }
}

class GifAdapter() : ViewBindingListAdapter<String>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
        return oldItem == newItem
    }

}) {
    override fun getViewBinding(viewType: Int): (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding {
        return ItemPreviewBinding::inflate
    }

    override fun getViewHolder(viewType: Int, viewBinding: ViewBinding): ViewBindingViewHolder<String, ViewBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(_binding: ViewBinding, onViewHolderClickListener: OnItemViewClickListener? = null) :
        ViewBindingViewHolder<String, ItemPreviewBinding>(_binding, onViewHolderClickListener) {
        override fun setData(data: String) {
            binding.ivPreview.setImageDrawable(null)
            binding.progressbar.visibility = View.VISIBLE
            Glide.with(itemView.context)
                .asGif()
                .load(data)
                .diskCacheStrategy(DiskCacheStrategy.DATA)
                .centerCrop()
                .listener(object : RequestListener<GifDrawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?,
                        isFirstResource: Boolean): Boolean {
                        binding.progressbar.visibility = View.GONE
                        return false
                    }

                    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        resource!!.start()
                        binding.progressbar.visibility = View.GONE
                        return false
                    }

                })
                .into(binding.ivPreview)
        }
    }

}