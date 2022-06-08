package com.app.shepherd.constants

import com.app.shepherd.BuildConfig.BASE_URL


/*
  Created by "Deepak Rattan" on 5/27/2022
*/


object ApiConstants {

    object WEB_URLS {
//        const val TERMS_AND_CONDITIONS = "${BuildConfig.BASE_URL}${API_VERSION}termsAndCondition"
//        const val PRIVACY_POLICY = "${BuildConfig.BASE_URL}${API_VERSION}privacyPolicy"
//        const val CONTACT_US = "${BuildConfig.BASE_URL}${API_VERSION}contactUs"
    }

    object AGORA {
        /* const val TEMP_TOKEN =
             "eyJpdiI6IllhajV5emZUUk53Nm1nK280dG1aUGc9PSIsInZhbHVlIjoiclFiczJnS2Uzb0ZWRUE0YkRzZnFVZz09IiwibWFjIjoiNjljYmI3OTliYWY2Y2RiNzBmNDQzZWU5ZjBkOTEwOGEyMDY4MGQxMTZlNzM2NTQ1YmQ2YWFkMzMyOTUxOTliOSJ9-1629695509"
         const val TEMP_CHANNEL_NAME = "cus_JlV5gtn3ip6NG2"*/
    }

    private const val API_VERSION = "api/v1/"

    object AUTHENTICATION {
        const val LOGIN = "$BASE_URL${API_VERSION}users/login"
        const val SIGN_UP = "$BASE_URL${API_VERSION}users"
        const val UPLOAD_IMAGE = "$BASE_URL${API_VERSION}user_profiles/profile/"
        const val FORGOT_PASSWORD = "$BASE_URL${API_VERSION}users/forgot-password"

    }

    object RELATIONS {
        const val GET_RELATIONS = "$BASE_URL${API_VERSION}relations"
    }

    object LOVED_ONE {
        const val CREATE_LOVED_ONE = "$BASE_URL${API_VERSION}users/create-loved-one"
    }

    object MEDICAL_CONDITIONS {
        const val GET_MEDICAL_CONDITIONS = "$BASE_URL${API_VERSION}conditions"
    }






    object ADDRESS {
//        const val GET_COUNTRY = "${BuildConfig.BASE_URL}${API_VERSION}get-country"
//        const val GET_STATES = "${BuildConfig.BASE_URL}${API_VERSION}get-state"
    }

    object NOTIFICATIONS {
//        const val GET_NOTIFICATIONS = "${BuildConfig.BASE_URL}${API_VERSION}notification"
    }

    object USER_POSTS {
//        const val GET_STORIES = "${BuildConfig.BASE_URL}${API_VERSION}story-list"
//        const val GET_POSTS = "${BuildConfig.BASE_URL}${API_VERSION}post-list"
//        const val ADD_POST = "${BuildConfig.BASE_URL}${API_VERSION}add-post"
//        const val BOOKMARKS_LISTING = "${BuildConfig.BASE_URL}${API_VERSION}bookmark-list"
//        const val ADD_STORY = "${BuildConfig.BASE_URL}${API_VERSION}add-story"
//        const val LIKE_POST = "${BuildConfig.BASE_URL}${API_VERSION}like-post"
//        const val ADD_BOOKMARK = "${BuildConfig.BASE_URL}${API_VERSION}add-bookmark"
//        const val GET_POST_COMMENTS = "${BuildConfig.BASE_URL}${API_VERSION}get-comment"
//        const val ADD_POST_COMMENTS = "${BuildConfig.BASE_URL}${API_VERSION}comment"
//        const val GET_POST_LIKES = "${BuildConfig.BASE_URL}${API_VERSION}postlike-user"
//        const val BUY_POST = "${BuildConfig.BASE_URL}${API_VERSION}buy-post"
//        const val GET_PROVIDER_POST_LIST = "${BuildConfig.BASE_URL}${API_VERSION}provider-post-list"
//        const val ARCHIVED_DELETE_POST = "${BuildConfig.BASE_URL}${API_VERSION}archived-delete-post"
//        const val VIEW_POST = "${BuildConfig.BASE_URL}${API_VERSION}view-post"

    }

    object COMMUNITY {
//        const val COMMUNITY_LISTING = "${BuildConfig.BASE_URL}${API_VERSION}community-list"

    }

    object FANS {
//        const val GET_FANS = "${BuildConfig.BASE_URL}${API_VERSION}fan-list"
//        const val GET_FOLLOWING = "${BuildConfig.BASE_URL}${API_VERSION}follow-list"

    }

    object BLOCK_UN_BLOCK {
//        const val BLOCK_USER = "${BuildConfig.BASE_URL}${API_VERSION}block-user"
//        const val RESTRICT_USER = "${BuildConfig.BASE_URL}${API_VERSION}restrict-user"
//        const val FAVOURITE_USER = "${BuildConfig.BASE_URL}${API_VERSION}favourite"
//        const val GET_FAVOURITES = "${BuildConfig.BASE_URL}${API_VERSION}favourite-list"
//        const val BLOCKED_RESTRICTED_USER = "${BuildConfig.BASE_URL}${API_VERSION}block-restrict"
//        const val REPORT_REASONS = "${BuildConfig.BASE_URL}${API_VERSION}report-reason"
//        const val REPORT_USER = "${BuildConfig.BASE_URL}${API_VERSION}report-user"
//        const val REPORT_POST = "${BuildConfig.BASE_URL}${API_VERSION}report-post"
    }


    object HELP_SUPPORT {
//        const val HELP_AND_SUPPORT = "${BuildConfig.BASE_URL}${API_VERSION}help-support"

    }

    /*object PROFILE {
        const val UPLOAD_BANNER_PROFILE_IMAGE =
            "${BuildConfig.BASE_URL}${API_VERSION}update-profile"
        const val UPDATE_ACCOUNT_INFO = "${BuildConfig.BASE_URL}${API_VERSION}edit-account"
        const val UPDATE_PROFILE_INFO = "${BuildConfig.BASE_URL}${API_VERSION}edit-profile"
        const val GET_USER_PROFILE = "${BuildConfig.BASE_URL}${API_VERSION}getuser-profile"
        const val UPDATE_SUBSCRIPTION_RENEW_STATUS =
            "${BuildConfig.BASE_URL}${API_VERSION}autorenew"
        const val DELETE_ACCOUNT = "${BuildConfig.BASE_URL}${API_VERSION}delete-account"
        const val PUSH_NOTIFICATIONS_STATUS =
            "${BuildConfig.BASE_URL}${API_VERSION}autonotification"
        const val EMAIL_NOTIFICATIONS_STATUS = "${BuildConfig.BASE_URL}${API_VERSION}autoemail"

    }*/

    /* object CHAT {
         const val GET_CHAT_LIST = "${BuildConfig.BASE_URL}${API_VERSION}chat-user"
         const val READ_MESSAGE = "${BuildConfig.BASE_URL}${API_VERSION}read-message"
         const val START_CONVERSATION = "${BuildConfig.BASE_URL}${API_VERSION}converstion"

     }*/

    /*object PLANS {
        const val ADD_PLAN = "${BuildConfig.BASE_URL}${API_VERSION}add-plan"
        const val GET_PLAN = "${BuildConfig.BASE_URL}${API_VERSION}get-plan"
        const val SUBSCRIBE_PROVIDER = "${BuildConfig.BASE_URL}${API_VERSION}subscribe-provider"
        const val DELETE_PLAN = "${BuildConfig.BASE_URL}${API_VERSION}delete-plan"
        const val FREE_PLAN = "${BuildConfig.BASE_URL}${API_VERSION}plan-free"
        const val PLAN_DESCRIPTION = "${BuildConfig.BASE_URL}${API_VERSION}plan-description"
        const val UNSUBSCRIBE = "${BuildConfig.BASE_URL}${API_VERSION}unsubscribe"

    }*/

//    object DOCUMENTS {
//        const val UPLOAD_DOC = "${BuildConfig.BASE_URL}${API_VERSION}save-image"
//
//    }


//    const val RE_SEND_OTP = "${BuildConfig.BASE_URL}${API_VERSION}user/resendOtp"

//    const val VERIFY_OTP = "${BuildConfig.BASE_URL}${API_VERSION}user/verifyOtp"


//    const val RESET_PASSWORD = "${BuildConfig.BASE_URL}${API_VERSION}user/resetPassword"

//    const val UPDATE_PROFILE = "${BuildConfig.BASE_URL}${API_VERSION}user/profile/update"

//    const val CHANGE_PASSWORD = "${BuildConfig.BASE_URL}${API_VERSION}user/changePassword"

//    const val VERIFY_UPDATED_EMAIL = "${BuildConfig.BASE_URL}${API_VERSION}user/verifyUpdatedEmail"

//    const val CHANGE_EMAIL = "${BuildConfig.BASE_URL}${API_VERSION}user/changeEmail"

}

