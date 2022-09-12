package com.shepherdapp.app.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Created by Deepak Rattan on 29/07/22
 */
class FileValidator {
    private var pattern: Pattern? = null
    private var matcher: Matcher? = null

    /**
     * Validate image with regular expression
     * @param image image for validation
     * @return true valid image, false invalid image
     */
    fun validate(image: String?): Boolean {
        matcher = pattern?.matcher(image)
        return matcher?.matches() == true
    }

    companion object {
        private const val IMAGE_PATTERN = "([^\\s]+(\\.(?i)(jpg|jpeg|png|gif|pdf|doc|docx|txt))$)"
    }

    init {
        pattern = Pattern.compile(IMAGE_PATTERN)
    }
}