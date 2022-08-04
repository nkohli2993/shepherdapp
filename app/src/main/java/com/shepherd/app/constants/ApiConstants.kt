package com.shepherd.app.constants

import com.shepherd.app.BuildConfig.BASE_URL
import com.shepherd.app.BuildConfig.BASE_URL_USER


/**
Created by "Deepak Rattan" on 5/27/2022
 */


object ApiConstants {

    private const val API_VERSION = "api/v1/"

    object Authentication {
        const val LOGIN = "$BASE_URL_USER${API_VERSION}users/login"
        const val LOGIN_WITH_DEVICE = "$BASE_URL_USER${API_VERSION}users/login/device"
        const val SIGN_UP = "$BASE_URL_USER${API_VERSION}users"
        const val UPLOAD_IMAGE = "$BASE_URL_USER${API_VERSION}user_profiles/profile/"
        const val FORGOT_PASSWORD = "$BASE_URL_USER${API_VERSION}users/forgot-password"
        const val BIOMETRIC = "$BASE_URL_USER${API_VERSION}user_profiles/biomertic"
        const val LOGOUT = "$BASE_URL_USER${API_VERSION}users/logout"
        const val USER_ROLES = "$BASE_URL_USER${API_VERSION}roles"
    }

    object Relations {
        const val GET_RELATIONS = "$BASE_URL${API_VERSION}relations"
    }

    object LovedOne {
        const val CREATE_LOVED_ONE = "$BASE_URL_USER${API_VERSION}users/create-loved-one"
    }

    object MedicalConditions {
        const val GET_MEDICAL_CONDITIONS = "$BASE_URL${API_VERSION}conditions"
        const val CREATE_BULK_ONE_CONDITIONS =
            "$BASE_URL${API_VERSION}user_conditions/create-bulkone"
    }

    object CareTeams {
        const val GET_CARE_TEAMS = "$BASE_URL${API_VERSION}care_teams"
        const val GET_CARE_TEAM_ROLES = "$BASE_URL${API_VERSION}care_roles"
        const val ADD_NEW_CARE_TEAM_MEMBER = "$BASE_URL${API_VERSION}invites"
        const val DELETE_CARE_TEAM_MEMBER = "$BASE_URL${API_VERSION}care_teams/{id}"
        const val UPDATE_CARE_TEAM_MEMBER = "$BASE_URL${API_VERSION}care_teams/{id}"
    }

    object UserDetails {
        const val GET_USER_DETAILS = "$BASE_URL_USER${API_VERSION}users/{id}"
        const val GET_USER_DETAILS_BY_UUID = "$BASE_URL_USER${API_VERSION}users/uuid/{id}"
    }

    object Event {
        const val CREATE_EVENT = "$BASE_URL${API_VERSION}events"
        const val GET_EVENT = "$BASE_URL${API_VERSION}events"
        const val GET_EVENT_DETAIL = "$BASE_URL${API_VERSION}events/{id}"
        const val ADD_EVENT_COMMENT = "$BASE_URL${API_VERSION}event_comments"
        const val GET_ALL_EVENT_COMMENT = "$BASE_URL${API_VERSION}event_comments/"
    }

    object Home {
        //const val GET_HOME_DATA = "$BASE_URL_USER${API_VERSION}users/get-home-data"
        const val GET_HOME_DATA = "$BASE_URL${API_VERSION}dashboards/get-home-data"
    }

    object Invitations {
        const val GET_INVITATIONS = "$BASE_URL${API_VERSION}invites"
        const val ACCEPT_INVITATIONS = "$BASE_URL${API_VERSION}invites/accept/{id}"
    }

    object LockBox {
        const val GET_ALL_LOCK_BOX_TYPES = "$BASE_URL${API_VERSION}lockbox_types"
        const val UPLOAD_LOCK_BOX_DOC = "$BASE_URL${API_VERSION}lockboxs/upload/"
        const val UPLOAD_MULTIPLE_LOCK_BOX_DOC = "$BASE_URL${API_VERSION}lockboxs/upload/multiple"
        const val CREATE_LOCK_BOX = "$BASE_URL${API_VERSION}lockboxs"

        const val GET_ALL_UPLOADED_DOCUMENTS_BY_LOVED_ONE_UUID =
             "$BASE_URL${API_VERSION}lockboxs/loved_one"
      /*  const val GET_ALL_UPLOADED_DOCUMENTS_BY_LOVED_ONE_UUID =
            "$BASE_URL${API_VERSION}lockboxs"*/
        const val DELETE_UPLOADED_LOCK_BOX_DOC = "$BASE_URL${API_VERSION}lockboxs/{id}"
        const val UPDATE_LOCK_BOX_DOC = "$BASE_URL${API_VERSION}lockboxs/{id}"
    }

    object MedList {
        const val GET_ALL_MED_LIST ="$BASE_URL${API_VERSION}medlists"
        const val GET_ALL_DOSE_LIST ="$BASE_URL${API_VERSION}dosages"
    }
}

