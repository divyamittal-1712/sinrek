package com.app.appsinrek.viewmodels

import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.app.appsinrek.network.API_LINKS
import com.app.appsinrek.player_view.enums.PostType
import com.app.appsinrek.storage.StorageUtil
import com.app.appsinrek.utils.AppConstants
import com.app.appsinrek.utils.CommonUtils
import com.app.appsinrek.utils.CommonUtils.hideKeyboardFrom
import com.app.appsinrek.utils.NetworkUtils
import com.app.appsinrek.utils.countrypicker.Country
import com.app.appsinrek.viewmodels.helpers.LocalResourceManager
import com.app.appsinrek.viewmodels.interfaces.PixLifecycle
import com.fitness.modval.interfaces.AuthListener
import com.fitness.modval.repositories.UserRepository
import okhttp3.MediaType
import okhttp3.RequestBody
import java.io.File


class MainViewModel(app: Application) : AndroidViewModel(app), PixLifecycle {
    var mobile: String? = ""
    var username: String? = ""
    var country: Country = Country.getCountryByISO("IN")
    var email: String? = ""
    var password: String? = ""
    var otp: String? = ""
    var newPassword: String? = ""
    var confirmPassword: String? = ""
    var caption: String = ""
    var text: String = ""
    var path: String? = ""
    var postType: String = ""

    val udid = CommonUtils.getDeviceId(getApplication<Application>().applicationContext)
    val storage = StorageUtil(getApplication<Application>().applicationContext)
    var user = StorageUtil(getApplication<Application>().applicationContext).readUserInfo()
    var apiKey = StorageUtil(getApplication<Application>().applicationContext).readUserAuthToken()

    var mainListener: AuthListener? = null


    val longSelection: MutableLiveData<Boolean> = MutableLiveData(false)
    val selectionList by lazy { MutableLiveData<MutableSet<Img>>(HashSet()) }
    val allImagesList by lazy { MutableLiveData(ModelList()) }
    private val allVideoList by lazy { MutableLiveData(ModelList()) }
    val callResults by lazy { MutableLiveData<Event<MutableSet<Img>>>() }
    val longSelectionValue: Boolean
        get() {
            return longSelection.value ?: false
        }
    val selectionListSize: Int
        get() {
            return selectionList.value?.size ?: 0
        }
    val imageList: LiveData<ModelList> = allImagesList
    private var options: Options = Options()
    fun retrieveImages(localResourceManager: LocalResourceManager) {
        val sizeInitial = 0
        selectionList.value?.clear()
        allImagesList.postValue(
                localResourceManager.retrieveMedia(
                        limit = sizeInitial,
                        mode = options.mode
                )
        )
//        val modelList = localResourceManager.retrieveMedia(
//            start = sizeInitial + 1,
//            mode = Mode.Picture
//        )
//        if (modelList.list.isNotEmpty()) {
//            allImagesList.postValue(modelList)
//        }
    }


    override fun onImageSelected(element: Img?, position: Int, callback: (Boolean) -> Boolean) {
        if (longSelectionValue) {
            selectionList.value?.apply {
                if (contains(element)) {
                    remove(element)
                    callback(false)
                } else if (callback(true)) {
                    element!!.position = (position)
                    add(element)
                }
            }
            selectionList.postValue(selectionList.value)
        } else {
            element!!.position = position
            selectionList.value?.add(element)
            returnObjects()
        }

    }

    override fun onImageLongSelected(element: Img?, position: Int, callback: (Boolean) -> Boolean) {
        if (options.count > 1) {
            // Utility.Companion.vibe(this@Pix, 50)
            longSelection.postValue(true)
            selectionList.value?.apply {
                if (contains(element)) {
                    remove(element)
                    callback(false)
                } else if (callback(true)) {
                    element!!.position = (position)
                    add(element)
                }
            }
            selectionList.postValue(selectionList.value)
        }
    }

    fun returnObjects() = callResults.postValue(Event(selectionList.value ?: HashSet()))

    fun setOptions(options: Options) {
        this.options = options
    }

    fun getOptions(): Options {
        return options;
    }


    fun editProfile(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_EDIT_PROFILE)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_EDIT_PROFILE, body);
        mainListener?.onSuccess(AppConstants.TAG_EDIT_PROFILE, apiResponse)
    }

    fun getUserProfile(userid: String) {
        mainListener?.onStarted(AppConstants.TAG_USER_PROFILE)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id
        if (!user.id.equals(userid)) {
            body["other_id"] = userid
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Get_User_detail, body);
        mainListener?.onSuccess(AppConstants.TAG_USER_PROFILE, apiResponse)
    }

    fun userBlock(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_USER_BLOCK)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_blockUser, body);
        mainListener?.onSuccess(AppConstants.TAG_USER_BLOCK, apiResponse)
    }

    fun userUnBlock(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_USER_BLOCK)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_unblockUser, body);
        mainListener?.onSuccess(AppConstants.TAG_USER_BLOCK, apiResponse)
    }

    fun getBlockedUserList() {
        mainListener?.onStarted(AppConstants.TAG_BLOCKED_USERS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["userid"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_getBlockedUsers, body);
        mainListener?.onSuccess(AppConstants.TAG_BLOCKED_USERS, apiResponse)
    }

    fun userReport(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_USER_REPORT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_User_Report, body);
        mainListener?.onSuccess(AppConstants.TAG_USER_REPORT, apiResponse)
    }

    fun postReport(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_POST_REPORT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Post_Report, body);
        mainListener?.onSuccess(AppConstants.TAG_POST_REPORT, apiResponse)
    }

    fun updateSettings(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_UPDATE_SETTINGS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Update_Permission, body);
        mainListener?.onSuccess(AppConstants.TAG_UPDATE_SETTINGS, apiResponse)
    }

    fun unfollow(friend_id: String) {
        mainListener?.onStarted(AppConstants.TAG_UNFOLLOW)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id
        body["friend_id"] = friend_id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Send_unFollow, body);
        mainListener?.onSuccess(AppConstants.TAG_UNFOLLOW, apiResponse)
    }

    fun cancelRequest(receiver_id: String) {

        mainListener?.onStarted(AppConstants.TAG_CANCEL_REQUEST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["sender_id"] = user.id
        body["receiver_id"] = receiver_id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_cancel_request, body);
        mainListener?.onSuccess(AppConstants.TAG_CANCEL_REQUEST, apiResponse)
    }

    fun sendFollowRequest(receiver_id: String) {
        mainListener?.onStarted(AppConstants.TAG_FOLLOW)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["sender_id"] = user.id
        body["receiver_id"] = receiver_id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Direct_Follow_Request, body);
        mainListener?.onSuccess(AppConstants.TAG_FOLLOW, apiResponse)
    }

    fun updateNotificationSettings(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_SET_NOTIFICATION_SETTINGS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Notification_Permission, body);
        mainListener?.onSuccess(AppConstants.TAG_SET_NOTIFICATION_SETTINGS, apiResponse)
    }

    fun getNotificationSettings() {
        mainListener?.onStarted(AppConstants.TAG_GET_NOTIFICATION_SETTINGS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["userid"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Get_Notification_Permission, body);
        mainListener?.onSuccess(AppConstants.TAG_GET_NOTIFICATION_SETTINGS, apiResponse)
    }

    fun getAllPermissionSettings() {
        mainListener?.onStarted(AppConstants.TAG_GET_All_SETTINGS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["userid"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Get_All_Permission, body);
        mainListener?.onSuccess(AppConstants.TAG_GET_All_SETTINGS, apiResponse)
    }

    fun searchUser(keyword: String) {
        mainListener?.onStarted(AppConstants.TAG_SEARCH)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id
        body["keyword"] = keyword

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Search_User, body);
        mainListener?.onSuccess(AppConstants.TAG_SEARCH, apiResponse)
    }

    fun postLikedUsers(post_id: String) {
        mainListener?.onStarted(AppConstants.TAG_LIKE_USERS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id
        body["post_id"] = post_id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Post_Liked_Users, body);
        mainListener?.onSuccess(AppConstants.TAG_LIKE_USERS, apiResponse)
    }

    fun getFollowersList(userid: String) {
        mainListener?.onStarted(AppConstants.TAG_SHOW_ALL_FOLLOWERS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = userid

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_FOLLWER_LIST, body);
        mainListener?.onSuccess(AppConstants.TAG_SHOW_ALL_FOLLOWERS, apiResponse)
    }

    fun getFollowingList(userid: String) {
        mainListener?.onStarted(AppConstants.TAG_SHOW_ALL_FOLLOWING)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = userid

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Show_following_list, body);
        mainListener?.onSuccess(AppConstants.TAG_SHOW_ALL_FOLLOWING, apiResponse)
    }

    fun getNotificationCount() {
        mainListener?.onStarted(AppConstants.TAG_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Get_Notification_Count, body);
        mainListener?.onSuccess(AppConstants.NOTIFICATIONS_COUNT, apiResponse)
    }

    fun getPosts(page: String) {
        mainListener?.onStarted(AppConstants.TAG_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id
        body["page"] = page

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Home_Search_POSTS, body);
        mainListener?.onSuccess(AppConstants.TAG_POST, apiResponse)
    }

    fun getPostsForUpdate(post_id: String) {
        mainListener?.onStarted(AppConstants.TAG_POST_FOR_UPDATE)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_GET_Single_POST_Detail, body);
        mainListener?.onSuccess(AppConstants.TAG_POST_FOR_UPDATE, apiResponse)
    }

    fun getPostsDetail(post_id: String) {
        mainListener?.onStarted(AppConstants.TAG_POST_DETAIL)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_GET_Single_POST_Detail, body);
        mainListener?.onSuccess(AppConstants.TAG_POST_DETAIL, apiResponse)
    }


    fun getUserPosts(id: String?) {
        mainListener?.onStarted(AppConstants.TAG_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id
        if (id != null) {
            body["other_user_id"] = id
        }

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_User_POSTS, body);
        mainListener?.onSuccess(AppConstants.TAG_POST, apiResponse)
    }

    fun likePosts(post_id: String, like: String) {
        mainListener?.onStarted(AppConstants.TAG_LIKE)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id
        body["like"] = like

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Post_Action, body);
        mainListener?.onSuccess(AppConstants.TAG_LIKE, apiResponse)
    }

    fun viewCountPosts(post_id: String) {
        mainListener?.onStarted(AppConstants.TAG_VIEW_COUNT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Views, body);
        mainListener?.onSuccess(AppConstants.TAG_VIEW_COUNT, apiResponse)
    }

    fun deletePosts(post_id: String) {
        mainListener?.onStarted(AppConstants.TAG_DELETE_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_User_deletePost, body);
        mainListener?.onSuccess(AppConstants.TAG_DELETE_POST, apiResponse)
    }

    fun bookmarkPosts(post_id: String, bookmark: String) {
        mainListener?.onStarted(AppConstants.TAG_BOOKMARK)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id
        body["bookmark"] = bookmark

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Post_Action, body);
        mainListener?.onSuccess(AppConstants.TAG_BOOKMARK, apiResponse)
    }

    fun showBookmarkedPosts() {
        mainListener?.onStarted(AppConstants.TAG_SHOW_ALL_BOOKMARK)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Show_Book_Marked_POSTs, body);
        mainListener?.onSuccess(AppConstants.TAG_SHOW_ALL_BOOKMARK, apiResponse)
    }

    fun getSuggestionsUsers(page: String) {
        mainListener?.onStarted(AppConstants.TAG_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["page"] = page
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_user_suggestion, body);
        mainListener?.onSuccess(AppConstants.TAG_USER_SUGGESTION, apiResponse)
    }

    fun sendOtp(view: View) {
        hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        mainListener?.onStarted(AppConstants.TAG_SEND_OTP)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        if (mobile.isNullOrEmpty()) {
            mainListener?.onFailure("mobile is empty")
            return
        }
        val body = HashMap<Any, Any?>()
        body["mobile"] = country.dialCode + mobile

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_SEND_OTP, body);
        mainListener?.onSuccess(AppConstants.TAG_SEND_OTP, apiResponse)
    }

    fun myPost(view: View) {
        hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        mainListener?.onStarted(AppConstants.TAG_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        if (postType == PostType.TEXT.value) {
            if (text.trim().length < 16) {
                mainListener?.onFailure("Text is empty")
                return
            }
        } else {
            if (path.isNullOrEmpty()) {
                mainListener?.onFailure("File path is empty")
                return
            }
        }
        val map = HashMap<String, RequestBody>()
        map["user_id"] = toRequestBody(user.id)
        map["post_type"] = toRequestBody("2")
        map["caption"] = toRequestBody(CommonUtils.encodeEmoji(if (PostType.TEXT.value == postType) text else caption))
        map["type"] = toRequestBody(postType)
        map["location_string"] = toRequestBody("")
        map["lat"] = toRequestBody("")
        map["long"] = toRequestBody("")
        if (PostType.IMAGE.value == postType)
            map.put("attachment\"; filename=\"pp.png\"", toImageRequestBody(path!!));
        if (PostType.VIDEO.value == postType)
            map.put("attachment\"; filename=\"pp.mp4\"", toVideoRequestBody(path!!));

        val apiResponse = UserRepository(apiKey).apiPostForm(API_LINKS.API_ADD_NEW_POST, map)
        mainListener?.onSuccess(AppConstants.TAG_POST, apiResponse)
    }

    fun mySharePost(view: View, map: HashMap<String, RequestBody>) {
        hideKeyboardFrom(getApplication<Application>().applicationContext, view)
        mainListener?.onStarted(AppConstants.TAG_SHARE_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_ADD_NEW_POST, body)
        mainListener?.onSuccess(AppConstants.TAG_SHARE_POST, apiResponse)
    }

    fun getAllNotifications() {
        mainListener?.onStarted(AppConstants.TAG_SHOW_ALL_NOTIFICATIONS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_YOU_NOTIFICATION, body)
        mainListener?.onSuccess(AppConstants.TAG_SHOW_ALL_NOTIFICATIONS, apiResponse)
    }

    fun getAllFollowingActivity() {
        mainListener?.onStarted(AppConstants.TAG_SHOW_ALL_REQUESTS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Show_Following_Activity, body)
        mainListener?.onSuccess(AppConstants.TAG_SHOW_ALL_FOLLOWING_ACTIVITY, apiResponse)
    }


    fun getAllRequests() {
        mainListener?.onStarted(AppConstants.TAG_SHOW_ALL_REQUESTS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["user_id"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Show_Follow_Request, body)
        mainListener?.onSuccess(AppConstants.TAG_SHOW_ALL_REQUESTS, apiResponse)
    }

    fun accept(sender: String, receiver: String) {
        mainListener?.onStarted(AppConstants.TAG_ACCEPT_REJECT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["sender_id"] = sender
        body["receiver_id"] = receiver
        body["status"] = "1"

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Confirm_Follow_Request, body)
        mainListener?.onSuccess(AppConstants.TAG_ACCEPT_REJECT, apiResponse)
    }

    fun reject(sender: String, receiver: String) {
        mainListener?.onStarted(AppConstants.TAG_ACCEPT_REJECT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["sender_id"] = sender
        body["receiver_id"] = receiver

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_deleteFollowRequest, body)
        mainListener?.onSuccess(AppConstants.TAG_ACCEPT_REJECT, apiResponse)
    }

    fun deleteAccount() {
        mainListener?.onStarted(AppConstants.TAG_DELETE_ACCOUNT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["userid"] = user.id

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.deleteUserAccount, body)
        mainListener?.onSuccess(AppConstants.TAG_DELETE_ACCOUNT, apiResponse)
    }

    fun changePassword(password: String) {
        mainListener?.onStarted(AppConstants.TAG_CHANGE_PASS)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["email"] = user.email
        body["password"] = password

        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Reset_Password, body)
        mainListener?.onSuccess(AppConstants.TAG_CHANGE_PASS, apiResponse)
    }

    fun getAllComments(post_id: String) {
        mainListener?.onStarted(AppConstants.TAG_COMMENT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Show_Comment, body)
        mainListener?.onSuccess(AppConstants.TAG_COMMENT, apiResponse)
    }

    fun addComments(post_id: String, comment: String, parent_id: String ?= "") {
        mainListener?.onStarted(AppConstants.TAG_ADD_COMMENT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["user_id"] = user.id
        body["parent_id"] = "";
        if (parent_id != null && parent_id.isNotEmpty())
            body["parent_id"] = parent_id;
        body["comment"] = comment
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_POST_COMMENT, body)
        mainListener?.onSuccess(AppConstants.TAG_ADD_COMMENT, apiResponse)
    }

    fun addCommentsReply(post_id: String, comment_id: String, comment: String) {
        mainListener?.onStarted(AppConstants.TAG_ADD_COMMENT_REPLY)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["post_id"] = post_id
        body["comment_id"] = comment_id
        body["user_id"] = user.id
        body["reply"] = comment
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_PostComment_Reply, body)
        mainListener?.onSuccess(AppConstants.TAG_ADD_COMMENT_REPLY, apiResponse)
    }

    fun deleteComments(id: String) {
        mainListener?.onStarted(AppConstants.TAG_DELETE_COMMENT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val body = HashMap<Any, Any?>()
        body["id"] = id
        body["user_id"] = user.id
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_DEL_COMMENT, body)
        mainListener?.onSuccess(AppConstants.TAG_DELETE_COMMENT, apiResponse)
    }

    fun checkUsernameExist(username: String) {
        mainListener?.onStarted(AppConstants.TAG_CHECK_USERNAME)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["username"] = username
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Check_Username_exist, body)
        mainListener?.onSuccess(AppConstants.TAG_CHECK_USERNAME, apiResponse)
    }

    fun deleteCommentsReply(id: String) {
        mainListener?.onStarted(AppConstants.TAG_DELETE_COMMENT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }

        val body = HashMap<Any, Any?>()
        body["id"] = id
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Delete_Comment_Reply, body)
        mainListener?.onSuccess(AppConstants.TAG_DELETE_COMMENT, apiResponse)
    }

    fun likeComment(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_LIKE_COMMENT)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_Like_Comment, body);
        mainListener?.onSuccess(AppConstants.TAG_LIKE_COMMENT, apiResponse)
    }

    fun updatePost(body: HashMap<Any, Any?>) {
        mainListener?.onStarted(AppConstants.TAG_UPDATE_POST)
        if (!NetworkUtils.isNetworkConnected(getApplication<Application>().applicationContext)) {
            mainListener?.onFailure("No Internet Connection")
            return
        }
        body["user_id"] = user.id
        val apiResponse = UserRepository(apiKey).apiCall(API_LINKS.API_User_editPost, body);
        mainListener?.onSuccess(AppConstants.TAG_UPDATE_POST, apiResponse)
    }

    fun toRequestBody(value: String): RequestBody {
        return RequestBody.create(MediaType.parse("text/plain"), value)
    }

    private fun toImageRequestBody(value: String): RequestBody {
        val file = File(value.replace("file:///", "/"));
        return RequestBody.create(MediaType.parse("image/*"), file)
    }

    private fun toVideoRequestBody(value: String): RequestBody {
        val file = File(value)
        return RequestBody.create(MediaType.parse("video/mp4"), file)
    }
}

open class Event<out T>(private val content: T) {

    var hasBeenHandled = false
        private set // Allow external read but not write

    /**
     * Returns the content and prevents its use again.
     */
    fun getContentIfNotHandledOrReturnNull(): T? {
        return if (hasBeenHandled) {
            null
        } else {
            hasBeenHandled = true
            content
        }
    }

    /**
     * Returns the content, even if it's already been handled.
     */
    fun peekContent(): T = content
}