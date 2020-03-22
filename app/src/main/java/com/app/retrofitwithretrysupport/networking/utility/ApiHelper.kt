package com.app.retrofitwithretrysupport.networking.utility

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object ApiHelper {
    private const val DEFAULT_RETRIES = 3

    fun <T> enqueueWithRetry(
        call: Call<T>,
        callback: Callback<T>,
        retryCount: Int = DEFAULT_RETRIES
    ) {
        call.enqueue(object : RetryableCallback<T>(call, retryCount) {
            override fun onFinalResponse(call: Call<T>, response: Response<T>) {
                callback.onResponse(call, response)
            }

            override fun onFinalFailure(call: Call<T>, t: Throwable) {
                callback.onFailure(call, t)
            }
        })
    }

    fun <T> isCallSuccess(response: Response<T>): Boolean {
        val code: Int = response.code()
        return code in 200..399
    }
}