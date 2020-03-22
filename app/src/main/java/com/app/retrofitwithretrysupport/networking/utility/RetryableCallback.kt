package com.app.retrofitwithretrysupport.networking.utility

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

abstract class RetryableCallback<T>(private val call: Call<T>, totalRetries: Int) :
    Callback<T> {
    private var totalRetries = 3
    private var retryCount = 0
    override fun onResponse(
        call: Call<T>,
        response: Response<T>
    ) {
        if (!ApiHelper.isCallSuccess(response)) if (retryCount++ < totalRetries) {
            Log.v(
                    TAG,
                "Retrying API Call -  ($retryCount / $totalRetries)"
            )
            retry()
        } else onFinalResponse(call, response) else onFinalResponse(call, response)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e(TAG, t.message.toString())
        if (!call.isCanceled && retryCount++ < totalRetries) {
            Log.v(
                    TAG,
                "Retrying API Call -  ($retryCount / $totalRetries)"
            )
            retry()
        } else onFinalFailure(call, t)
    }

    open fun onFinalResponse(call: Call<T>, response: Response<T>) {}
    open fun onFinalFailure(call: Call<T>, t: Throwable) {}

    private fun retry() {
        call.clone().enqueue(this)
    }

    companion object {
        private val TAG = RetryableCallback::class.java.simpleName
    }

    init {
        this.totalRetries = totalRetries
    }
}