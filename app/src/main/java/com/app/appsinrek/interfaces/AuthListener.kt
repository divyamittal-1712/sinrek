package com.fitness.modval.interfaces

import androidx.lifecycle.LiveData
import com.app.appsinrek.models.ResponseData

interface AuthListener {
    fun onStarted(tag:String)
    fun onSuccess(tag:String, apiResponse: LiveData<ResponseData>)
    fun onFailure(message : String)
}