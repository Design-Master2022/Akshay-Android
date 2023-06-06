package com.newsapp.newsapp.ui.news

import android.app.Application
import androidx.lifecycle.*
import com.newsapp.newsapp.modal.TopHeadLinesResponse
import com.newsapp.newsapp.server.AppConstants.DEFAULT_CATEGORY
import com.newsapp.newsapp.server.AppConstants.DEFAULT_COUNTRY
import com.newsapp.newsapp.server.NetworkRepository
import com.newsapp.newsapp.server.Resource
import kotlinx.coroutines.*
import retrofit2.Response

class MainViewModel constructor(
    application: Application,
    private val networkRepository: NetworkRepository
) : AndroidViewModel(application) {

    val newsList: MutableLiveData<Resource<TopHeadLinesResponse>> = MutableLiveData()
    var topHeadLinesNewsPage = 1
    var topHeadLinesNewsResponse: TopHeadLinesResponse? = null

    init {
        // Fetch initial data when the ViewModel is created
        getTopHeadLines(DEFAULT_COUNTRY, DEFAULT_CATEGORY)
    }

    /**
     * Fetches the top headlines based on the given [countryCode] and [category].
     */
    fun getTopHeadLines(countryCode: String, category: String) = viewModelScope.launch {
        // Post loading state before making the API request
        newsList.postValue(Resource.Loading())
        val response = networkRepository.getTopHeadLines(countryCode, category, topHeadLinesNewsPage)
        newsList.postValue(handleTopHeadlineNewsResponse(response))
    }

    /**
     * Handles the response received from the top headlines API request.
     * Updates the [topHeadLinesNewsResponse] with the new data and returns a [Resource] object.
     */
    private fun handleTopHeadlineNewsResponse(response: Response<TopHeadLinesResponse>): Resource<TopHeadLinesResponse> {
        if (response.isSuccessful) {
            response.body()?.let { resultResponse ->
                topHeadLinesNewsPage++
                if (topHeadLinesNewsResponse == null) {
                    topHeadLinesNewsResponse = resultResponse
                } else {
                    val oldArticles = topHeadLinesNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(topHeadLinesNewsResponse ?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }
}
