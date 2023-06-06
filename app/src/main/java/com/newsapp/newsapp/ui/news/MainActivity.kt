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


class MainActivity : BaseActivity() {

    private lateinit var newsListAdapter: NewsListAdapter
    private lateinit var viewModel: MainViewModel
    private lateinit var networkRepository: NetworkRepository
    private lateinit var binding: ActivityMainBinding
    private val TAG = "MainActivity"
    private var countryName: String? = "us"
    private var categoryName: String? = "general"

    private val newsCategories = mutableListOf(
        "general", "business",
        "entertainment", "health",
        "science", "sports", "technology"
    )

    private val countryList = mutableListOf(
        "us", "ar",
        "in"
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.ivAdd.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    ProfileActivity::class.java
                )
            ).also { finish() }
        }

        networkRepository = NetworkRepository(baseContext.applicationContext as Application)
        viewModel = MainViewModel(baseContext.applicationContext as Application, networkRepository)

        val selectedCategory =
            CommonSharedPreferences.readInt(CommonSharedPreferences.SELECTED_CATEGORY)
        val selectedCountry =
            CommonSharedPreferences.readInt(CommonSharedPreferences.SELECTED_COUNTRY)

        addRecyclerView()

        viewModel.newsList.observe(this) { response ->
            when (response) {
                is Resource.Success -> {
                    hideProgressBar()
                    response.data.let { newsResponse ->
                        newsListAdapter.differ.submitList(newsResponse?.articles)
                        Log.e(TAG, "onCreate Internet : " + newsResponse?.articles?.size)
                        val totalPages = ((newsResponse?.totalResults)?.div(PAGE_SIZE) ?: 1) + 2
                        isLastPage = viewModel.topHeadLinesNewsPage == totalPages
                        if (isLastPage) {
                            binding.recyclerView.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    response.message?.let { message ->
                        Utils.showToast(this, "An Error Occured: $message")
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                }
            }
        }

        binding.spCountry.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                countryName = countryList[position]
                countryName?.let {
                        viewModel.topHeadLinesNewsResponse = null
                        viewModel.topHeadLinesNewsPage = 1
                        CommonSharedPreferences.writeInt(
                            CommonSharedPreferences.SELECTED_COUNTRY,
                            position
                        )
                    categoryName?.let { it1 -> viewModel.getTopHeadLines(it, it1) }
                }
            }
        }

        binding.spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                categoryName = newsCategories[position]
                categoryName?.let {
                     viewModel.topHeadLinesNewsPage = 1
                        viewModel.topHeadLinesNewsResponse = null
                        CommonSharedPreferences.writeInt(
                            CommonSharedPreferences.SELECTED_CATEGORY,
                            position
                        )
                    countryName?.let { it1 -> viewModel.getTopHeadLines(it1, it) }
                }

            }
        }

        /*
        * Sets up a SwipeRefreshLayout.OnRefreshListener that is invoked when the user
        * performs a swipe-to-refresh gesture.
        */
        binding.swipeRefresh.setOnRefreshListener {
            Log.i(TAG, "onRefresh called from SwipeRefreshLayout")

            // This method performs the actual data-refresh operation.
            // The method calls setRefreshing(false) when it's finished.
            viewModel.topHeadLinesNewsResponse = null
            viewModel.topHeadLinesNewsPage = 1
            countryName?.let { categoryName?.let { it1 -> viewModel.getTopHeadLines(it, it1) } }
        }

        binding.spCategory.setSelection(selectedCategory)
        binding.spCountry.setSelection(selectedCountry)


    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition + visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= PAGE_SIZE
            val shouldPaginate = isNotLoadingAndNotLastPage && isNotAtBeginning && isAtLastItem &&
                    isTotalMoreThanVisible && isScrolling
            if (shouldPaginate) {
                viewModel.getTopHeadLines(AppConstants.DEFAULT_COUNTRY, AppConstants.DEFAULT_CATEGORY)
                isScrolling = false
            }
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
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
            swipeRefresh.isRefreshing = false
        }
        isLoading = false
    }

    private fun showProgressBar() {
        with(binding) {
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