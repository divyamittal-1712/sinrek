package com.fitness.modval.network

import android.content.SharedPreferences
import com.app.appsinrek.models.ResponseData
import com.app.appsinrek.network.API_LINKS.*
import com.app.appsinrek.viewmodels.ServiceInterceptor
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface ApiInterface {
    @POST("{api}")
    fun apiCall(
        @Path("api") api: String,
        @Body data: RequestBody,
    ): Call<ResponseData>

    @Multipart
    @POST("{api}")
    fun apiPostForm(
        @Path("api") api: String,
        @PartMap params : HashMap<String, RequestBody>,
    ): Call<ResponseData>

    @GET("{api}")
    fun apiCallGet(
        @Path("api") api: String,
    ): Call<ResponseData>

    companion object{
        operator fun invoke(apiKey: String?) : ApiInterface{
            val gson = GsonBuilder().setLenient().create();

            var client = OkHttpClient.Builder()
                    .addInterceptor(ServiceInterceptor(apiKey))
                    //.readTimeout(45,TimeUnit.SECONDS)
                    //.writeTimeout(45,TimeUnit.SECONDS)
                    .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiInterface :: class.java)
        }
    }


}