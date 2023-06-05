package com.newsapp.newsapp.ui.adapter

import android.app.Activity
import android.content.Intent
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
import com.newsapp.newsapp.server.AppConstants.DETAIL_NEWS
import com.newsapp.newsapp.ui.news.MainViewModel
import com.newsapp.newsapp.ui.news.NewsDetailsActivity

class NewsListAdapter: RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>()
{
    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_newsSource: TextView
        var tv_newsTitle:TextView
        var iv_newsImage: ImageView
        var tv_newsTag:TextView

        init {
            tv_newsSource = itemView.findViewById(R.id.newsSource)
            tv_newsTitle = itemView.findViewById(R.id.newsTitle)
            iv_newsImage = itemView.findViewById(R.id.iv_news_image)
            tv_newsTag = itemView.findViewById(R.id.tv_timetag)

        }

    }

//    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    private val diefferCallback = object: DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, diefferCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.news_item_layout, parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.itemView.apply {
            Glide.with(this).load(article.urlToImage).into(holder.iv_newsImage)
            holder.tv_newsTitle.text = article.title
            holder.tv_newsSource.text = article.source.name
//            holder.tv_newsTag.text = article.author.toString()
            setOnClickListener{
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)?= null

    fun setOnItemClickListener( listener: (Article) -> Unit) {
        onItemClickListener = listener
    }
}