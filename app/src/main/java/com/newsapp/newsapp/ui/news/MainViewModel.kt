package com.newsapp.newsapp.ui.news

import androidx.lifecycle.*
import com.newsapp.newsapp.modal.TopHeadLinesResponse
import com.newsapp.newsapp.server.NetworkRepository
import com.newsapp.newsapp.server.Resource
import kotlinx.coroutines.*
import retrofit2.Response

class MainViewModel constructor(private val networkRepository: NetworkRepository) : ViewModel() {

    val errorMessage = MutableLiveData<String>()
    val newsList: MutableLiveData<Resource<TopHeadLinesResponse>> = MutableLiveData()
    var job: Job? = null
    var topHeadLinesNewsPage = 1
    var topHeadLinesNewsResponse : TopHeadLinesResponse? = null

    val loading = MutableLiveData<Boolean>()

    init {
        getTopHeadLines("us", "general")
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

    private fun onError(message: String) {
        errorMessage.value = message
        loading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}