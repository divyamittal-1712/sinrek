package com.fitness.modval.repositories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.appsinrek.models.ResponseData
import com.fitness.modval.network.ApiInterface
import okhttp3.MediaType
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class UserRepository(apiKey: String?) {

    private val apiKey: String? = apiKey

    fun apiCall(url: String,data: HashMap<Any,Any?>): LiveData<ResponseData>{
        val apiResponse = MutableLiveData<ResponseData>()

        val body: RequestBody = RequestBody.create(
            MediaType.parse("application/json; charset=utf-8"),
            JSONObject(data).toString()
        )
        ApiInterface(apiKey).apiCall(url,body).enqueue(object: Callback<ResponseData>{
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if(response.isSuccessful){
                    Log.d("response", response.body().toString())

                    val model = response.body();
                    if (model != null) {
                        apiResponse.value = response.body()
                    } else {
                        apiResponse.value = ResponseData(response.code(),response.errorBody()?.string() ?: "Some Error Occurred!")
                    }
                }else{
                    apiResponse.value = ResponseData(response.code(),response.errorBody()?.string() ?: "Some Error Occurred!")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                Log.d("response4", call.toString())
                Log.d("response4", t.message.toString())

                apiResponse.value = ResponseData(500,t.message ?: "Some Error Occurred!")
            }
        })
        return apiResponse
    }
    fun apiPostForm(url: String, data: HashMap<String, RequestBody>): LiveData<ResponseData>{
        val apiResponse = MutableLiveData<ResponseData>()


        ApiInterface(apiKey).apiPostForm(url,data).enqueue(object: Callback<ResponseData>{
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if(response.isSuccessful){
                    val model = response.body();
                    if (model != null) {
                        apiResponse.value = response.body()
                    } else {
                        apiResponse.value = ResponseData(response.code(),response.errorBody()?.string() ?: "Some Error Occurred!")
                    }
                }else{
                    apiResponse.value = ResponseData(response.code(),response.errorBody()?.string() ?: "Some Error Occurred!")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                apiResponse.value = ResponseData(500,t.message ?: "Some Error Occurred!")
            }
        })
        return apiResponse
    }
    fun apiCallGet(url: String): LiveData<ResponseData>{
        val apiResponse = MutableLiveData<ResponseData>()
        ApiInterface(apiKey).apiCallGet(url).enqueue(object: Callback<ResponseData>{
            override fun onResponse(call: Call<ResponseData>, response: Response<ResponseData>) {
                if(response.isSuccessful){
                    val model = response.body();
                    if (model != null) {
                        apiResponse.value = response.body()
                    } else {
                        apiResponse.value = ResponseData(response.code(),response.errorBody()?.string() ?: "Some Error Occurred!")
                    }
                }else{
                    apiResponse.value = ResponseData(response.code(),response.errorBody()?.string() ?: "Some Error Occurred!")
                }
            }

            override fun onFailure(call: Call<ResponseData>, t: Throwable) {
                apiResponse.value = ResponseData(500,t.message ?: "Some Error Occurred!")
            }
        })
        return apiResponse
    }

}