package com.app.retrofitwithretrysupport.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.app.retrofitwithretrysupport.R
import com.app.retrofitwithretrysupport.model.SearchResultModel
import com.app.retrofitwithretrysupport.networking.utility.ApiHelper
import com.app.retrofitwithretrysupport.networking.ApiProvider
import com.app.retrofitwithretrysupport.networking.ApiService
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity(), Callback<SearchResultModel?> {
    var call: Call<SearchResultModel?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        searchEditText?.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                resultTextView?.visibility = View.GONE
                progressLoading?.visibility = View.VISIBLE

                startSearching(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun startSearching(searchQuery: String?) {
        val retrofit = ApiProvider.createService(ApiService::class.java)
        call?.cancel()
        call = retrofit.getResult(searchQuery)
        ApiHelper.enqueueWithRetry(call, this)
    }

    private fun checkAndSetData(searchResultModel: Response<SearchResultModel?>) {
        if (!searchResultModel.isSuccessful) {
            resultTextView?.visibility = View.GONE
            progressLoading?.visibility = View.VISIBLE
        } else {
            if (searchResultModel.body() != null) {
                val data = searchResultModel.body()
                if (data?.status.equals("OK", true) && data?.data?.items?.size ?: 0 > 0) {
                    resultTextView?.text = data.toString()
                } else {
                    resultTextView?.text = "No data"
                }
            } else {
                resultTextView?.text = "No data"
            }

            resultTextView?.visibility = View.VISIBLE
            progressLoading?.visibility = View.GONE
        }
    }

    override fun onFailure(call: Call<SearchResultModel?>, t: Throwable) {
    }

    override fun onResponse(call: Call<SearchResultModel?>, response: Response<SearchResultModel?>) {
        checkAndSetData(response)
    }
}