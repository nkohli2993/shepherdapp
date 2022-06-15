package com.app.shepherd.constants

import com.app.shepherd.BuildConfig.BASE_URL


/**
Created by "Deepak Rattan" on 5/27/2022
 */


object ApiConstants {

    private const val API_VERSION = "api/v1/"

    object AUTHENTICATION {
        const val LOGIN = "$BASE_URL${API_VERSION}users/login"
        const val LOGIN_WITH_DEVICE = "$BASE_URL${API_VERSION}users/login/device"
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

    object CARE_TEAMS {
        const val GET_CARE_TEAMS = "$BASE_URL${API_VERSION}care_teams"
    }

    object USER_DETAILS {
        const val GET_USER_DETAILS = "$BASE_URL${API_VERSION}users/{id}"

    }


}

