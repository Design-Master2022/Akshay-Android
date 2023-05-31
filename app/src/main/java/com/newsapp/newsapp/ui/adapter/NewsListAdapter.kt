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
import com.newsapp.newsapp.ui.news.MainViewModel

class NewsListAdapter: RecyclerView.Adapter<NewsListAdapter.NewsViewHolder>()
{

//    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): NewsViewHolder = NewsViewHolder(
//        LayoutInflater.from(context).inflate(R.layout.news_item_layout,p0,false))
//
//    override fun getItemCount()= newsList.size
//
//    override fun onBindViewHolder(viewHolder: NewsViewHolder, p1: Int) {
//
//        val article = newsList.get(p1)
//        viewHolder.tv_newsTitle.setText(article.title)
//        viewHolder.tv_newsTag.setText(article.publishedAt)
////        article.urlToImage?.also {
////            Glide.with(context).load(it).placeholder(R.drawable.ic_dummy_image).into(viewHolder.iv_newsImage)
////        }
//
////        if(article.isFavourite)
////            viewHolder.iv_newsFavourite.setImageResource(R.drawable.ic_bookmark_black_24dp)
////        else
////            viewHolder.iv_newsFavourite.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
//
//        viewHolder.tv_newsSource.setText(article.source?.name)
//
//    }
//
//    fun updateAdapter(newsList:List<Article>){
//        this.newsList = newsList
//        notifyDataSetChanged()
//    }
//
//    fun throwIntent(adapterPos:Int){
////        val intent = Intent(context,NewsDetailActivity::class.java)
////        intent.putExtra("source",UrlConstant.HOME_NEWS_REDIRECTION)
////        intent.putExtra("articleInfo",newsList.get(adapterPos))
////        context.startActivity(intent)
//    }
//
    inner class NewsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_newsSource: TextView
        var tv_newsTitle:TextView
        var iv_newsImage: ImageView
        var tv_newsTag:TextView
//        var iv_newsFavourite:ImageView

        init {
//            tv_newsSource = itemView.newsSource
            tv_newsSource = itemView.findViewById(R.id.newsSource)
//            tv_newsTitle = itemView.newsTitle
            tv_newsTitle = itemView.findViewById(R.id.newsTitle)
//            iv_newsImage = itemView.iv_news_image
            iv_newsImage = itemView.findViewById(R.id.iv_news_image)
//            tv_newsTag = itemView.tv_timetag
            tv_newsTag = itemView.findViewById(R.id.tv_timetag)
//            iv_newsFavourite = itemView.iv_favourite
//            iv_newsFavourite = itemView.findViewById(R.id.iv_favourite)


//            iv_newsFavourite.setOnClickListener {
//                val article:Article = newsList.get(adapterPosition)
//                article.isFavourite = !article.isFavourite
//                if(article.isFavourite){
//                    viewModel.bookMarkArticle(article)
//                    iv_newsFavourite.setImageResource(R.drawable.ic_bookmark_black_24dp)
//                    (context as Activity).toast(context.resources.getString(R.string.ac_bookmark_added))
//                }else{
//                    viewModel.deleteBookMarkArticle(article)
//                    iv_newsFavourite.setImageResource(R.drawable.ic_bookmark_border_black_24dp)
//                    (context as Activity).toast(context.resources.getString(R.string.ac_bookmark_removed))
//                }
//
//
//            }

//            itemView.setOnClickListener({
//                throwIntent(adapterPosition)
//            })
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