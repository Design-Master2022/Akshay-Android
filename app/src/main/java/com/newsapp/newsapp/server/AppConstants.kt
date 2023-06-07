package com.newsapp.newsapp.server

/**
 * An object that contains constant values used in the application.
 */
object AppConstants {
    /**
     * Constant value representing the user login request code.
     */
    const val USER_LOGIN: Int = 1001

    /**
     * Default page size used for pagination.
     */
    const val PAGE_SIZE = 20

    /**
     * Constant key used for passing news details between activities.
     */
    const val DETAIL_NEWS = "DETAIL_NEWS"

    /**
     * Header key for specifying the Cache-Control directive.
     */
    const val HEADER_CACHE_CONTROL = "Cache-Control"

    /**
     * Header key for specifying the Pragma directive.
     */
    const val HEADER_PRAGMA = "Pragma"

    /**
     * Default country code used for news API requests.
     */
    const val DEFAULT_COUNTRY = ""

    /**
     * Default category used for news API requests.
     */
    const val DEFAULT_CATEGORY = ""
}
