package com.fitness.modval.interfaces

import androidx.lifecycle.LiveData

interface ResponseListener {
    fun onStarted(tag:String)
    fun onSuccess(tag:String, apiResponse: LiveData<String>)
    fun onFailure(tag:String, message : String)
}