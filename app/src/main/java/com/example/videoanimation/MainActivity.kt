package com.example.videoanimation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewbinding.ViewBinding
import com.example.videoanimation.databinding.ActivityMainBinding
import com.example.videoanimation.databinding.ItemPreviewBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pl.droidsonroids.gif.GifDrawable
import java.io.IOException
import java.lang.ref.SoftReference
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels


class MainActivity : ViewBindingActivity<ActivityMainBinding>() {
    override val getBindingInflater: (LayoutInflater) -> ActivityMainBinding
        get() = ActivityMainBinding::inflate

    private val adapter = GifAdapter()

    override fun onDestroy() {
        adapter.stop()
        super.onDestroy()
    }

    override fun onViewCreated(savedInstanceState: Bundle?) {
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(this@MainActivity, 3)
            adapter = this@MainActivity.adapter.apply {
                submitList(getListGifUrls())
            }
        }

//        val url =
//            "https://gridproofofconcepttest.s3.eu-central-1.amazonaws.com/Gif+2+seconds+fps6/1.gif"
//
//        GlobalScope.launch {
//            try {
//                val buffer = downloadGif(url)
//                runOnUiThread {
//                    val drawable = GifDrawable(buffer)
//                    binding.ivTest2.setImageDrawable(drawable)
//                    drawable.start()
//                }
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//        }
    }

    private fun getListGifUrls(): List<String> {
        val list = mutableListOf<String>()
        for (i in 1..163) {
            list.add("https://gridproofofconcepttest.s3.eu-central-1.amazonaws.com/Gif+2+seconds+fps6/$i.gif")
        }
//        list.apply {
//            add("https://media.giphy.com/media/l0MYFgIkxG9rFiN5C/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKNzP3V6mQfUzhC/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKLCt1jjvNrF3a0/giphy.gif")
//            add("https://media.giphy.com/media/OBDPf1XJ7oGgE/giphy.gif")
//            add("https://media.giphy.com/media/rjgbmeBqAEiXF6FCyD/giphy.gif")
//            add("https://media.giphy.com/media/RCDeoJpz6zYglRxvQM/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKLTVfVp95MDmIo/giphy.gif")
//            add("https://media.giphy.com/media/l0MYSNiwPgncS3wNq/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKsiL9LS4ixuRyg/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKQ7KHOK8b3YJ1u/giphy.gif")
//            add("https://media.giphy.com/media/l0MYtzTM0DMjEJZ7y/giphy.gif")
//            add("https://media.giphy.com/media/l0MYtzTM0DMjEJZ7y/giphy.gif")
//            add("https://media.giphy.com/media/l0MYRkucgpRJlkFPO/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKAWVvqEliCTdh6/giphy.gif")
//            add("https://media.giphy.com/media/3o7TKqDoRYYymET4Pe/giphy.gif")
//        }
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
    val giftDrawableReferences = hashMapOf<String, SoftReference<GifDrawable>>()

    val downloadingUrls = mutableListOf<String>()
    var job: Job? = null
    private var isStop = false

    private fun downloadImage(url: String) {
        if (!downloadingUrls.contains(url)) {
            downloadingUrls.add(url);
        }
        Log.e("Test", "add url: $url")
        startDownloadingQueue()
    }

    private fun cancelDownloadingUrl(url: String) {
        downloadingUrls.remove(url)
        Log.e("Test", "remove url: $url")
    }

    private fun startDownloadingQueue() {
        if (isStop) {
            return
        }
        // downloading other url or drawable was downloaded before
        if (job != null || downloadingUrls.isEmpty()) {
            return
        }

        val urlToDownload = downloadingUrls.removeFirst()
        // dont download url if it's available already
        if (giftDrawableReferences[urlToDownload]?.get() != null) {
            startDownloadingQueue();
            return
        }
        Log.e("Test", "startDownloadingQueue - url = $urlToDownload")
        job = GlobalScope.launch {
            val buffer = downloadGif(urlToDownload)
            giftDrawableReferences[urlToDownload] = SoftReference(GifDrawable(buffer))
            val index = currentList.indexOf(urlToDownload)
            if (index in 0 until itemCount) {
                withContext(Dispatchers.Main) {
                    notifyItemChanged(index)
                }
            }
            job = null
            startDownloadingQueue()
        }
    }

    fun stop() {
        job?.cancel()
        job = null
        isStop = true
    }

    override fun getViewBinding(viewType: Int): (LayoutInflater, ViewGroup?, Boolean) -> ViewBinding {
        return ItemPreviewBinding::inflate
    }

    override fun getViewHolder(viewType: Int, viewBinding: ViewBinding): ViewBindingViewHolder<String, ViewBinding> {
        return ViewHolder(viewBinding)
    }

    inner class ViewHolder(_binding: ViewBinding, onViewHolderClickListener: OnItemViewClickListener? = null) :
        ViewBindingViewHolder<String, ItemPreviewBinding>(_binding, onViewHolderClickListener) {
        override fun setData(data: String) {
            (binding.ivPreview.tag as? String)?.let {
                cancelDownloadingUrl(it)
            }
            binding.ivPreview.setImageDrawable(null)
            giftDrawableReferences[data]?.get()?.let {
                binding.progressbar.visibility = View.GONE
                binding.ivPreview.setImageDrawable(it)
                it.start()
                binding.ivPreview.tag = data
            } ?: run {
                binding.progressbar.visibility = View.VISIBLE
                downloadImage(data)
                binding.ivPreview.tag = data
            }

//            binding.progressbar.visibility = View.VISIBLE
//            Glide.with(itemView.context)
//                .asGif()
//                .load(data)
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .centerCrop()
//                .listener(object : RequestListener<GifDrawable> {
//                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<GifDrawable>?,
//                        isFirstResource: Boolean): Boolean {
//                        binding.progressbar.visibility = View.GONE
//                        return false
//                    }
//
//                    override fun onResourceReady(resource: GifDrawable?, model: Any?, target: Target<GifDrawable>?,
//                        dataSource: DataSource?, isFirstResource: Boolean): Boolean {
//                        resource!!.start()
//                        binding.progressbar.visibility = View.GONE
//                        return false
//                    }
//
//                })
//                .into(binding.ivPreview)
        }
    }

}

fun downloadGif(url: String): ByteBuffer {
    val urlConnection = URL(url).openConnection()
    urlConnection.connect()
    val contentLength = urlConnection.contentLength
    if (contentLength < 0) {
        throw IOException("Content-Length header not present")
    }
    urlConnection.getInputStream().use {
        val buffer = ByteBuffer.allocateDirect(contentLength)
        Channels.newChannel(it).use { channel ->
            while (buffer.remaining() > 0) {
                channel.read(buffer)
            }
            return buffer
        }
    }
}