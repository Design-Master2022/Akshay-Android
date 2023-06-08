package com.newsapp.newsapp.ui.news

import android.app.Application
import androidx.lifecycle.*
import com.newsapp.newsapp.modal.TopHeadLinesResponse
import com.newsapp.newsapp.server.AppConstants.DATA_NOT_CATCHED_CODE
import com.newsapp.newsapp.server.NetworkRepository
import com.newsapp.newsapp.server.Resource
import com.newsapp.newsapp.utils.CommonSharedPreferences
import kotlinx.coroutines.*
import retrofit2.Response

class MainViewModel constructor(
    application: Application,
    private val networkRepository: NetworkRepository
) : AndroidViewModel(application) {

    val newsList: MutableLiveData<Resource<TopHeadLinesResponse>> = MutableLiveData()
    var topHeadLinesNewsPage = 1
    var topHeadLinesNewsResponse: TopHeadLinesResponse? = null

    /**
     * Fetches the top headlines based on the given [countryCode] and [category].
     */
    fun getTopHeadLines(countryCode: String, category: String) = viewModelScope.launch {
        // Post loading state before making the API request
        if(countryCode.isNotEmpty() && category.isNotEmpty()) {
            newsList.postValue(Resource.Loading())
            val response =
                networkRepository.getTopHeadLines(countryCode, category, topHeadLinesNewsPage)
            newsList.postValue(handleTopHeadlineNewsResponse(response))
        }
    }

    fun getDefaultCategory() : Int {
        // Retrieve the selected category from shared preferences
        return CommonSharedPreferences.readInt(CommonSharedPreferences.SELECTED_CATEGORY, 0)
    }

    fun getDefaultCountry() : Int {
        // Retrieve the selected country from shared preferences
        return  CommonSharedPreferences.readInt(CommonSharedPreferences.SELECTED_COUNTRY, 0)
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
        } else {
            val errorCode = response.code()
            val errorMessage = response.message()

            if (errorCode == DATA_NOT_CATCHED_CODE) {
                if (topHeadLinesNewsResponse == null) {
                    return Resource.DataNotCached(errorMessage, topHeadLinesNewsResponse)
                } else if (topHeadLinesNewsResponse?.articles?.isEmpty() == false) {
                    return Resource.Error(errorMessage, topHeadLinesNewsResponse)
                }
            }
        }
        return Resource.Error(response.message(), topHeadLinesNewsResponse)
    }

}
