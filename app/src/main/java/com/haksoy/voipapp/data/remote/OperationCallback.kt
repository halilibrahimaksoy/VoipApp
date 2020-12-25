package com.haksoy.voipapp.data.remote

interface OperationCallback<T> {
    fun onSuccess(data: List<Any>?)
    fun onError(error: String?)
}