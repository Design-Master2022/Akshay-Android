package com.newsapp.newsapp.ui.news

import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AbsListView
import android.widget.AdapterView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.util.Util
import com.google.android.material.snackbar.Snackbar
import com.newsapp.newsapp.R
import com.newsapp.newsapp.databinding.ActivityMainBinding
import com.newsapp.newsapp.modal.Article
import com.newsapp.newsapp.server.AppConstants
import com.newsapp.newsapp.server.AppConstants.PAGE_SIZE
import com.newsapp.newsapp.server.NetworkRepository
import com.newsapp.newsapp.server.Resource
import com.newsapp.newsapp.ui.BaseActivity
import com.newsapp.newsapp.ui.adapter.NewsListAdapter
import com.newsapp.newsapp.ui.home.ProfileActivity
import com.newsapp.newsapp.utils.CommonSharedPreferences
import com.newsapp.newsapp.utils.Utils
import com.newsapp.newsapp.utils.Utils.showSnackbar


class MainActivity : BaseActivity() {

    private lateinit var newsListAdapter: NewsListAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var networkRepository: NetworkRepository
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private var countryName: String? = "" // Default country name
    private var categoryName: String? = ""  // Default category name

    // List of news categories
    private val newsCategories = mutableListOf(
        "general", "business",
        "entertainment", "health",
        "science", "sports", "technology"
    )

    // List of countries
    private val countryList = mutableListOf(
        "us", "ar",
        "in"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        networkRepository = NetworkRepository(baseContext.applicationContext as Application)
        viewModel = MainViewModel(baseContext.applicationContext as Application, networkRepository)

        setUI()

        addRecyclerView()

        viewModel.newsList.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    // Handle successful response
                    response.data.let { newsResponse ->
                        newsListAdapter.differ.submitList(emptyList())
                        // Update the list adapter with the new articles
                        newsListAdapter.differ.submitList(newsResponse?.articles)
                        // Calculate the total number of pages
                        val totalPages = (newsResponse?.totalResults)?.div(PAGE_SIZE)?.plus(2)
                        // Check if it's the last page based on the current page number
                        isLastPage = viewModel.topHeadLinesNewsPage == totalPages
                        // Set padding if it's the last page
                        if (isLastPage) {
                            binding.recyclerView.setPadding(0, 0, 0, 0)
                        }
                    }
                    hideProgressBar()
                }
                is Resource.Error -> {
                    // Handle error response
                    if(!Utils.isInternetAvailable(baseContext)){
                        showSnackbar(
                            getString(R.string.internet_not_avl), binding.mainRootLayout,
                            Snackbar.LENGTH_SHORT
                        )
                        hideProgressBar()
                        return@observe
                    } else {
                        response.message?.let { message ->
                            showSnackbar(
                                message, binding.mainRootLayout,
                                Snackbar.LENGTH_SHORT
                            )
                        }
                        noDataAvailable()
                    }
                }
                is Resource.Loading -> {
                    // Handle loading state
                    if(Utils.isInternetAvailable(baseContext))
                        newsListAdapter.differ.submitList(emptyList())
                    showProgressBar()
                }
            }
        }

        setDefaultCountry(viewModel.getDefaultCountry())
        setDefaultCategory(viewModel.getDefaultCategory())

    }

    private fun setDefaultCategory(countryPos: Int) {
        // Set the selected category in the Spinner
        binding.spCategory.setSelection(countryPos)
    }


    private fun setDefaultCountry(countryPos: Int) {
        // Set the selected country in the Spinner
        binding.spCountry.setSelection(countryPos)
    }

    private fun setUI(){

        binding.ivAdd.setOnClickListener {
            // Start the ProfileActivity
            startActivity(Intent(this, ProfileActivity::class.java))
            // Finish the current activity
            finish()
        }

        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed when nothing is selected
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // Retrieve the selected category
                categoryName = newsCategories[position]
                categoryName?.let {
                    // Reset page number and response data
                    viewModel.topHeadLinesNewsPage = 1
                    viewModel.topHeadLinesNewsResponse = null
                    // Save the selected category index
                    CommonSharedPreferences.writeInt(CommonSharedPreferences.SELECTED_CATEGORY, position)
                    // Fetch top headlines with the selected country and category
                    countryName?.let { country -> viewModel.getTopHeadLines(country, it) }

                }
            }
        }

        binding.spCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No action needed when nothing is selected
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                // Retrieve the selected country name
                countryName = countryList[position]
                countryName?.let {
                    // Reset response data and page number
                    viewModel.topHeadLinesNewsResponse = null
                    viewModel.topHeadLinesNewsPage = 1
                    // Save the selected country index
                    CommonSharedPreferences.writeInt(CommonSharedPreferences.SELECTED_COUNTRY, position)
                    // Fetch top headlines with the selected country and category
                    categoryName?.let { category -> viewModel.getTopHeadLines(it, category) }
                }
            }
        }

        /*
       * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
       * performs a swipe-to-refresh gesture.
       */
        binding.swipeRefresh.setOnRefreshListener {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout")
            reloadNewsFeed()

        }
    }

    private fun reloadNewsFeed(){
        if(!Utils.isInternetAvailable(baseContext)){
            showSnackbar(
                getString(R.string.internet_not_avl), binding.mainRootLayout,
                Snackbar.LENGTH_SHORT
            )
            binding.swipeRefresh.isRefreshing = false
        } else {
            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            viewModel.topHeadLinesNewsResponse = null
            viewModel.topHeadLinesNewsPage = 1
            countryName?.let { categoryName?.let { it1 -> viewModel.getTopHeadLines(it, it1) } }
        }
    }

    var isLoading = false // Flag indicating if data is currently being loaded
    var isLastPage = false // Flag indicating if it is the last page of data
    var isScrolling = false // Flag indicating if the RecyclerView is currently being scrolled

    // Scroll listener for the RecyclerView
    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            // Check if conditions are met for pagination
            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isNotAtBeginning && isAtLastItem &&
                    isTotalMoreThanVisible && isScrolling

            if (shouldPaginate) {
                countryName?.let {
                    categoryName?.let { it1 ->
                        viewModel.getTopHeadLines(
                            it,
                            it1
                        )
                    }
                }
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            // Update the scrolling flag when the scroll state changes
            if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                isScrolling = true
            }
        }
    }

    private fun addRecyclerView() {
        newsListAdapter = NewsListAdapter().apply {
            setOnItemClickListener(this@MainActivity::onArticleClicked)
        }

        binding.recyclerView.apply {
            adapter = newsListAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addOnScrollListener(scrollListener)
        }
    }

    private fun onArticleClicked(article: Article) {
        val intent = Intent(this, NewsDetailsActivity::class.java)
        intent.putExtra(AppConstants.DETAIL_NEWS, article)
        startActivity(intent)
    }

    private fun hideProgressBar() {
        with(binding) {
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
            noDataAvailbale.visibility = View.GONE
            swipeRefresh.isRefreshing = false
            recyclerView.visibility = View.VISIBLE
        }
        isLoading = false
    }

    private fun noDataAvailable() {
        with(binding) {
            recyclerView.visibility = View.GONE
            shimmerViewContainer.stopShimmer()
            shimmerViewContainer.visibility = View.GONE
            swipeRefresh.isRefreshing = false
            noDataAvailbale.visibility = View.VISIBLE
        }
        isLoading = false
    }

    private fun showProgressBar() {
        with(binding) {
            noDataAvailbale.visibility = View.GONE
            recyclerView.visibility = View.GONE
            shimmerViewContainer.startShimmer()
            shimmerViewContainer.visibility = View.VISIBLE
        }
        isLoading = true
    }

    override fun onPause() {
        hideProgressBar()
        super.onPause()
    }
}