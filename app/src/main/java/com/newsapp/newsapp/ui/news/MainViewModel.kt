package com.newsapp.newsapp.ui.news

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.core.content.getSystemService
import androidx.lifecycle.*
import com.newsapp.newsapp.R
import com.newsapp.newsapp.modal.Article
import com.newsapp.newsapp.modal.TopHeadLinesResponse
import com.newsapp.newsapp.server.AppConstants.DEFAULT_CATEGORY
import com.newsapp.newsapp.server.AppConstants.DEFAULT_COUNTRY
import com.newsapp.newsapp.server.NetworkRepository
import com.newsapp.newsapp.server.Resource
import com.newsapp.newsapp.utils.NewsApp
import com.newsapp.newsapp.utils.Utils
import kotlinx.coroutines.*
import retrofit2.Response
import java.io.IOException

class MainViewModel constructor(application: Application, private val networkRepository: NetworkRepository) : AndroidViewModel(application) {

    private val errorMessage = MutableLiveData<String>()
    val newsList: MutableLiveData<Resource<TopHeadLinesResponse>> = MutableLiveData()
    var job: Job? = null
    var topHeadLinesNewsPage = 1
    var topHeadLinesNewsResponse : TopHeadLinesResponse? = null

    val loading = MutableLiveData<Boolean>()

    init {
        getTopHeadLines(DEFAULT_COUNTRY, DEFAULT_CATEGORY)
    }

    fun getTopHeadLines(countryCode: String, category: String) = viewModelScope.launch {
        newsList.postValue(Resource.Loading())
        val response = networkRepository.getTopHeadLines(countryCode, category, topHeadLinesNewsPage )
        newsList.postValue(handleToHeadelineNewsResponse(response))
    }

    private fun handleToHeadelineNewsResponse(response: Response<TopHeadLinesResponse>) : Resource<TopHeadLinesResponse> {
        if(response.isSuccessful){
            response.body()?.let { resultResponse ->
                topHeadLinesNewsPage++
                if(topHeadLinesNewsResponse == null){
                    topHeadLinesNewsResponse = resultResponse
                } else {
                    val oldArticles = topHeadLinesNewsResponse?.articles
                    val newArticles = resultResponse.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(topHeadLinesNewsResponse?: resultResponse)
            }
        }
        return Resource.Error(response.message())
    }

//    private suspend fun safeTopHeadlinesNews(countryCode: String, category: String){
//        newsList.postValue(Resource.Loading())
////        try{
////            if(Utils.isInternetAvailable(getApplication<NewsApp>())){
//                val response = networkRepository.getTopHeadLines(countryCode, category, topHeadLinesNewsPage )
//                newsList.postValue(handleToHeadelineNewsResponse(response))
//
////
////        } catch (t: Throwable){
////            when(t){
////                is IOException -> newsList.postValue(Resource.Error(getApplication<NewsApp>().getString(R.string.network_failure)))
////                else -> newsList.postValue(Resource.Error(getApplication<NewsApp>().getString(R.string.conversion_error)))
////            }
////        }
//
//    }


    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}