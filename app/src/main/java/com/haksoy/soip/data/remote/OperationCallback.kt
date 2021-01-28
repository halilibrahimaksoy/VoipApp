package com.haksoy.soip.data.remote

interface OperationCallback<T> {
    fun onSuccess(data: List<Any>?)
    fun onError(error: String?)
}