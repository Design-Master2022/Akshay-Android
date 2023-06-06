package com.newsapp.newsapp.ui.news

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AlertDialog
import com.newsapp.newsapp.R
import com.newsapp.newsapp.databinding.ActivityNewsDetailsBinding
import com.newsapp.newsapp.databinding.CommonAlertBinding
import com.newsapp.newsapp.modal.Article
import com.newsapp.newsapp.server.AppConstants.DETAIL_NEWS
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.utils.Utils


class NewsDetailsActivity : BaseActivity() {

    private lateinit var binding: ActivityNewsDetailsBinding

    private var article: Article? = null
    private var strNewsURL: String? = null
    private var strTitle: String? = null
    private var strSubTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get data from intent
        article = intent.getSerializableExtra(DETAIL_NEWS) as? Article
        article?.let { populateArticleData(it) }
    }

    private fun populateArticleData(article: Article) {
        strNewsURL = article.url
        strTitle = article.title
        strSubTitle = article.url

        binding.tvTitle.text = strTitle
        binding.tvSubTitle.text = strSubTitle

        if (Utils.isInternetAvailable(this@NewsDetailsActivity)) {
            showWebView()
        } else {
            commonInternetAlert(baseContext.getString(R.string.internet_not_avl))
        }
    }

    private fun commonInternetAlert(message: String) {
        val binding: CommonAlertBinding = CommonAlertBinding.inflate(layoutInflater)
        val customAlertBuilder = AlertDialog.Builder(this@NewsDetailsActivity)
        customAlertBuilder.setView(binding.root)
        val customDialog = customAlertBuilder.create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvCustomAlertMessage.text = message
        binding.cardCustomAlertOk.setOnClickListener {
            customDialog.dismiss()
            finish()
        }
        customDialog.show()
    }

    private fun showWebView() {
        binding.webView.apply {
            settings.loadsImagesAutomatically = true
            settings.javaScriptEnabled = true
            settings.domStorageEnabled = true
            settings.setSupportZoom(true)
            settings.builtInZoomControls = true
            settings.displayZoomControls = false
            scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            loadUrl(strNewsURL!!)
        }

        showProgressBar()

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, newUrl: String): Boolean {
                view.loadUrl(newUrl)
                return true
            }

            override fun onPageStarted(view: WebView, urlStart: String, favicon: Bitmap?) {
                strNewsURL = urlStart
            }

            override fun onPageFinished(view: WebView, urlPage: String) {
                hideProgressBar()
            }
        }
    }

    private fun hideProgressBar() {
        binding.apply {
            shimmerFullViewContainer.visibility = View.GONE
            shimmerFullViewContainer.stopShimmer()
            webView.visibility = View.VISIBLE
        }
    }

    private fun showProgressBar() {
        binding.apply {
            shimmerFullViewContainer.visibility = View.VISIBLE
            shimmerFullViewContainer.startShimmer()
            webView.visibility = View.INVISIBLE
        }
    }
}