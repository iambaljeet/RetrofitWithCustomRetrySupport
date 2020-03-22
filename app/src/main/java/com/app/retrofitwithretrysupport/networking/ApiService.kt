package com.app.retrofitwithretrysupport.networking

import com.app.retrofitwithretrysupport.model.SearchResultModel
import com.app.retrofitwithretrysupport.utils.Constants.SEARCH_URL
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {
    @GET(SEARCH_URL)
    fun getResult(
            @Query("q") query: String?): Call<SearchResultModel?>
}