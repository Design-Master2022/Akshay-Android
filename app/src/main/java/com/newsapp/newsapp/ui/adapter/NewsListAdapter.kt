package com.newsapp.newsapp.ui.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.newsapp.newsapp.R
import com.newsapp.newsapp.modal.Article
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.min

class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in the item layout
        var tvNewsSource: TextView = itemView.findViewById(R.id.newsSource)
        var tvNewsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        var ivNewsImage: ImageView = itemView.findViewById(R.id.iv_news_image)
        var tvTimeTag: TextView = itemView.findViewById(R.id.tv_timetag)
    }

    // DiffUtil callback for efficient list updates
    private val diffCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    // AsyncListDiffer for handling list updates asynchronously
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        // Inflate the item layout and return a NewsViewHolder
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.news_item_layout, parent, false)
        return NewsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]

        // Set the data to views in the NewsViewHolder
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.ivNewsImage)
            holder.tvNewsTitle.text = article.title
            holder.tvNewsSource.text = article.source.name
            holder.tvTimeTag.text = "Headline: ${convertTimeToHours(article.publishedAt)}"

            setOnClickListener {
                // Invoke the item click listener if set
                onItemClickListener?.let { listener ->
                    listener(article)
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    // Set the item click listener
    fun setOnItemClickListener(listener: (Article) -> Unit) {
        onItemClickListener = listener
    }

    private fun convertTimeToHours(timeString: String?): String {
        timeString?.let {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
            val date = inputFormat.parse(timeString)
            val calendar = Calendar.getInstance()
            calendar.time = date
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)
            if (hour in 2..23)
                return "$hour Hrs Ago"
            else if (hour < 1)
                return "$minute Min Ago"
            else if (hour > 24)
                return getFormatedDate(timeString)
            else
                return ""
        }
        return ""
    }

    private fun getFormatedDate(timeString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault())
        val date = inputFormat.parse(timeString)

        val outputFormat = SimpleDateFormat("d MMMM yyyy", Locale.getDefault())
        return outputFormat.format(date)
    }
}