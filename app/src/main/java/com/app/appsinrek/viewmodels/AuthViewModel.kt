package com.app.appsinrek.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.app.appsinrek.fcm.services.MyFcmListenerService
import com.app.appsinrek.network.API_LINKS
import com.app.appsinrek.storage.StorageUtil
import com.app.appsinrek.utils.AppConstants
import com.app.appsinrek.utils.CommonUtils
import com.app.appsinrek.utils.CommonUtils.hideKeyboardFrom
import com.app.appsinrek.utils.NetworkUtils
import com.app.appsinrek.utils.countrypicker.Country
import com.fitness.modval.interfaces.AuthListener
import com.fitness.modval.repositories.UserRepository


class AuthViewModel(app: Application) : AndroidViewModel(app) {
    var mobile: String? = ""
    var username: String? = ""
    var country: Country = Country.getCountryByISO("IN")
    var email: String? = ""
    var password: String? = ""
    var otp: String? = ""
    var newPassword: String? = ""
    var confirmPassword: String? = ""
    val udid = CommonUtils.getDeviceId(getApplication<Application>().applicationContext)
    val storage = StorageUtil(getApplication<Application>().applicationContext)
    var device_token: String? = MyFcmListenerService.refereshedToken();
    var authListener: AuthListener? = null

    fun loginButtonClick(view: View) {
        if (view != null)
            hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        authListener?.onStarted(AppConstants.TAG_LOGIN)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            authListener?.onFailure("No Internet Connection")
            return
        }
        if (mobile.isNullOrEmpty()) {
            authListener?.onFailure("mobile is empty")
            return
        } else if (password.isNullOrEmpty()) {
            authListener?.onFailure("Password is empty")
            return
        }
        val body = HashMap<Any, Any?>()
        body["mobile"] = country.dialCode + mobile
        body["password"] = password
        body["device_token"] = device_token

        val apiResponse = UserRepository(null).apiCall(API_LINKS.API_SIGN_IN, body);
        authListener?.onSuccess(AppConstants.TAG_LOGIN, apiResponse)
    }

    fun sendOtp(view: View) {
        hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        authListener?.onStarted(AppConstants.TAG_SEND_OTP)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            authListener?.onFailure("No Internet Connection")
            return
        }
        if (mobile.isNullOrEmpty()) {
            authListener?.onFailure("mobile is empty")
            return
        }
        val body = HashMap<Any, Any?>()
        body["mobile"] = country.dialCode + mobile

        val apiResponse = UserRepository(null).apiCall(API_LINKS.API_SEND_OTP, body);
        authListener?.onSuccess(AppConstants.TAG_SEND_OTP, apiResponse)
    }

    fun forgotPassword(view: View) {
        hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        authListener?.onStarted(AppConstants.TAG_FORGOT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            authListener?.onFailure("No Internet Connection")
            return
        }
        if (mobile.isNullOrEmpty()) {
            authListener?.onFailure("mobile is empty")
            return
        }
        val body = HashMap<Any, Any?>()
        body["mobile"] = country.dialCode + mobile

        val apiResponse = UserRepository(null).apiCall(API_LINKS.API_FORGOT, body);
        authListener?.onSuccess(AppConstants.TAG_FORGOT, apiResponse)
    }

    fun resetPasswrod(view: View) {
        hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        authListener?.onStarted(AppConstants.TAG_RESET)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            authListener?.onFailure("No Internet Connection")
            return
        }
        if (newPassword.isNullOrEmpty()) {
            authListener?.onFailure("Password is empty")
            return
        } else if (newPassword!!.length < 8) {
            authListener?.onFailure("Password length must be grater then 8")
            return
        } else if (confirmPassword.isNullOrEmpty()) {
            authListener?.onFailure("Confirm Password is empty")
            return
        } else if (!confirmPassword.equals(newPassword)) {
            authListener?.onFailure("Confirm Password is not match")
            return
        }
        val body = HashMap<Any, Any?>()
        body["mobile"] = country.dialCode + mobile
        body["otp"] = otp
        body["password"] = newPassword

        val apiResponse = UserRepository(null).apiCall(API_LINKS.API_RESET_PASSWORD, body);
        authListener?.onSuccess(AppConstants.TAG_RESET, apiResponse)
    }

    fun onSignUpButtonClick(view: View) {
        authListener?.onStarted(AppConstants.TAG_SIGNUP)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            authListener?.onFailure("No Internet Connection")
            return
        }
        if (mobile.isNullOrEmpty()) {
            authListener?.onFailure("Mobile is empty")
            return
        } else if (username.isNullOrEmpty()) {
            authListener?.onFailure("Username is empty")
            return
        } else if (password.isNullOrEmpty()) {
            authListener?.onFailure("Password is empty")
            return
        } else if (password!!.length < 8) {
            authListener?.onFailure("Password length must be grater then 8")
            return
        } else if (device_token == null) {
            authListener?.onFailure("device token is empty.")
            device_token = MyFcmListenerService.getCurrentToken()
            return
        } else if (device_token!!.isEmpty()) {
            authListener?.onFailure("device token is empty.")
            device_token = MyFcmListenerService.getCurrentToken()
            return
        }
        val body = HashMap<Any, Any?>()
        body["mobile"] = country.dialCode + mobile
        body["username"] = username
        body["password"] = password
        body["device_token"] = device_token

        val apiResponse = UserRepository(null).apiCall(API_LINKS.API_SIGN_UP, body);
        authListener?.onSuccess(AppConstants.TAG_SIGNUP, apiResponse)
    }

    fun onCheckUsername(view: View) {
        authListener?.onStarted(AppConstants.TAG_CHECK_USERNAME)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            authListener?.onFailure("No Internet Connection")
            return
        }
        if (mobile.isNullOrEmpty()) {
            authListener?.onFailure("Mobile is empty")
            return
        } else if (username.isNullOrEmpty()) {
            authListener?.onFailure("Username is empty")
            return
        } else if (password.isNullOrEmpty()) {
            authListener?.onFailure("Password is empty")
            return
        } else if (password!!.length < 8) {
            authListener?.onFailure("Password length must be grater then 8")
            return
        }
        val body = HashMap<Any, Any?>()
        body["username"] = username

        val apiResponse = UserRepository(null).apiCall(API_LINKS.API_Check_Username_exist, body);
        authListener?.onSuccess(AppConstants.TAG_CHECK_USERNAME, apiResponse)
    }

}