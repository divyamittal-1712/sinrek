package com.app.appsinrek.viewmodels

import android.content.SharedPreferences
import okhttp3.*
import okio.Buffer
import org.json.JSONObject
import javax.inject.Inject

class ServiceInterceptor @Inject constructor(private val apiKey: String? ) : Interceptor{

    private val token: String? get() = apiKey

    override fun intercept(chain: Interceptor.Chain): Response {
        var request: Request = chain.request()
        if(request.header("No-Authentication")==null){
            if(!token.isNullOrEmpty())
            {
                val finalToken = "Bearer $token"
                request = request.newBuilder()
                        .addHeader("Authorization",finalToken)
                        .build()
            }
        }
        return chain.proceed(request)
    }

//    private fun apiKey(request: Request): String {
//        return try {
//            val oldBody = request.body()
//            val buffer =  Buffer()
//            oldBody?.writeTo(buffer)
//            val strOldBody = buffer.readUtf8()
//            buffer.clear()
//            buffer.close()
//            var body = JSONObject(strOldBody);
//            if(body.has("api_key")) body.get("api_key") as String  else ""
//        }catch (e:Exception){ "" }
//    }
//    private fun editBody(request: Request): RequestBody {
//        val oldBody = request.body()
//        val buffer =  Buffer()
//        oldBody?.writeTo(buffer)
//        val strOldBody = buffer.readUtf8()
//        buffer.clear()
//        buffer.close()
//        val strNewBody = JSONObject(strOldBody);
//        strNewBody.remove("api_key");
//        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), strNewBody.toString());
//    }
}