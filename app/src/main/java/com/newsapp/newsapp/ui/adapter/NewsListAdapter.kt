package com.newsapp.newsapp.ui.adapter

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

class NewsListAdapter : RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>() {

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Views in the item layout
        var tvNewsSource: TextView = itemView.findViewById(R.id.newsSource)
        var tvNewsTitle: TextView = itemView.findViewById(R.id.newsTitle)
        var ivNewsImage: ImageView = itemView.findViewById(R.id.iv_news_image)
        var tvNewsTag: TextView = itemView.findViewById(R.id.tv_timetag)
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
}