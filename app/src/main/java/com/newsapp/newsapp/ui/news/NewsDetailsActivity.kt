package com.newsapp.newsapp.ui.news

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import com.newsapp.newsapp.databinding.ActivityNewsDetailsBinding
import com.newsapp.newsapp.modal.Article
import com.newsapp.newsapp.server.AppConstants.DETAIL_NEWS
import com.newsapp.newsapp.ui.BaseActivity


class NewsDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityNewsDetailsBinding
    var article: Article? = null
    var strNewsURL: String? = null
    var strTitle: String? = null
    var strSubTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.progressBar.max = 100

        //get data intent
        article = intent.getSerializableExtra(DETAIL_NEWS) as Article?
        article?.let {

            strNewsURL = article?.url
            strTitle = article?.title
            strSubTitle = article?.url

            binding.tvTitle.text = strTitle
            binding.tvSubTitle.text = strSubTitle

            //show news
            showWebView()
        }
    }

    private fun showWebView() {
        binding.webView.settings.loadsImagesAutomatically = true
        binding.webView.settings.javaScriptEnabled = true
        binding.webView.settings.domStorageEnabled = true
        binding.webView.settings.setSupportZoom(true)
        binding.webView.settings.builtInZoomControls = true
        binding.webView.settings.displayZoomControls = false
        binding.webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        binding.webView.loadUrl(strNewsURL!!)

        binding.progressBar.progress = 0

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, newUrl: String): Boolean {
                view.loadUrl(newUrl)
                binding.progressBar.progress = 0
                return true
            }

            override fun onPageStarted(view: WebView, urlStart: String, favicon: Bitmap?) {
                strNewsURL = urlStart
//                invalidateOptionsMenu()
            }

            override fun onPageFinished(view: WebView, urlPage: String) {
                binding.progressBar.visibility = View.GONE
//                invalidateOptionsMenu()
            }
        }
    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        if (item.itemId == android.R.id.home) {
//            finish()
//            return true
//        }
//        return super.onOptionsItemSelected(item)
//    }
}