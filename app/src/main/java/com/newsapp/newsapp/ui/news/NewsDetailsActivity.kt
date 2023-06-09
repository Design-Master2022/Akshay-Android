package com.newsapp.newsapp.ui.news

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
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
    private var strNewsURL: String? = null
    private var strTitle: String? = null
    private var strSubTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get data from intent
        article = intent.getSerializableExtra(DETAIL_NEWS) as? Article
        article?.let { populateArticleData(it) }
    }

    /**
     * Populates the UI with data from the provided [article].
     * Sets the article's URL, title, and subtitle.
     * Shows the web view if internet is available, otherwise shows a common internet alert.
     */
    private fun populateArticleData(article: Article) {
        strNewsURL = article.url
        strTitle = article.title
        strSubTitle = article.url

        binding.tvTitle.text = strTitle
        binding.tvSubTitle.text = strSubTitle

        if (Utils.isInternetAvailable(this@NewsDetailsActivity)) {
            showWebView()
        } else {
            commonInternetAlert(getString(R.string.internet_not_avl))
        }
    }

    /**
     * Shows a common internet alert dialog with the provided [message].
     * Dismisses the dialog and finishes the activity when the OK button is clicked.
     */
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

    /**
     * Shows the web view with the provided [strNewsURL].
     * Configures web view settings, loads the URL, and sets up a web view client.
     * Shows and hides the progress bar during page loading.
     */
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

    /**
     * Hides the progress bar and shows the web view.
     */
    private fun hideProgressBar() {
        binding.apply {
            shimmerFullViewContainer.visibility = View.GONE
            shimmerFullViewContainer.stopShimmer()
            webView.visibility = View.VISIBLE
        }
    }

    /**
     * Shows the progress bar and hides the web view.
     */
    private fun showProgressBar() {
        binding.apply {
            shimmerFullViewContainer.visibility = View.VISIBLE
            shimmerFullViewContainer.startShimmer()
            webView.visibility = View.INVISIBLE
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // Handle back button click here
                onBackPressed() // or any other desired action
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }
}
