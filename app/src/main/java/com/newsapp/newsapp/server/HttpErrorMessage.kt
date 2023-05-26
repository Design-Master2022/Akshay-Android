package com.newsapp.newsapp.server

const val TIME_OUT = 0
const val CONNECTION = 1
const val UN_AUTHORIZED = 401
const val NOT_FOUND = 404
const val SESSION_EXPIRED = 412
const val REQUEST_SUCCESS = 200
const val REQUEST_FAILED = 400
const val INTERNAL_SERVER_ERROR = 500 // Token expired
const val INTERNAL_SERVER_ERROR_MSG = "Internal server error"
const val TIME_OUT_MSG = "Time out"
const val UN_AUTHORIZED_MSG = "Authorization is required"
const val NOT_FOUND_MSG = "Not found"
const val CONNECTION_MSG = "Failed to connect server"
const val ERROR_MSG = "Internet not available"//Something went wrong. Please try again
