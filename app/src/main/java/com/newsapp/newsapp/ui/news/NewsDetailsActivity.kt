package com.newsapp.newsapp.ui.news

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
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
    var strNewsURL: String? = null
    private var strTitle: String? = null
    private var strSubTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get data intent
        article = intent.getSerializableExtra(DETAIL_NEWS) as Article?
        article?.let {

            strNewsURL = article?.url
            strTitle = article?.title
            strSubTitle = article?.url

            binding.tvTitle.text = strTitle
            binding.tvSubTitle.text = strSubTitle

            //show news
            if(Utils.isInternetAvailable(this@NewsDetailsActivity)) {
                showWebView()
            } else{
                commonInternetAlert(this@NewsDetailsActivity, baseContext.getString(R.string.internet_not_avl))
            }
        }


    }

    private fun commonInternetAlert(context: Context, message: String) {
        var customDialog: AlertDialog? = null
        customDialog?.dismiss()

        val binding: CommonAlertBinding = CommonAlertBinding
            .inflate(LayoutInflater.from(context))
        val customAlertBuilder = AlertDialog.Builder(context)
        customAlertBuilder.setView(binding.root)
        customDialog = customAlertBuilder.create()
        customDialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.tvCustomAlertMessage.text= message
        binding.cardCustomAlertOk.setOnClickListener {
            customDialog.dismiss()
            finish()
        }
        customDialog.show()
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

        showProgressBar()

        binding.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, newUrl: String): Boolean {
                view.loadUrl(newUrl)
//                binding.progressBar.progress = 0
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

    private fun hideProgressBar (){
        binding.shimmerFullViewContainer.visibility = View.GONE
        binding.shimmerFullViewContainer.stopShimmer()
        binding.webView.visibility = View.VISIBLE
    }

    private fun showProgressBar () {
        binding.shimmerFullViewContainer.visibility = View.VISIBLE
        binding.shimmerFullViewContainer.startShimmer()
        binding.webView.visibility = View.INVISIBLE
    }


}