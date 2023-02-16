package com.shepherdapp.app.network.retrofit

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer

/**
Created by "Deepak Rattan" on 27/05/2022
 */


class EventObserver<T>(private val onEventUnhandledContent: (T) -> Unit) : Observer<Event<T>> {
    override fun onChanged(t: Event<T>?) {
        t?.getContentIfNotHandled()?.let { value ->
            {
                onEventUnhandledContent(value)
            }
        }
    }
}

inline fun <T> LiveData<Event<T>>.observeEvent(
    owner: LifecycleOwner,
    crossinline onEventUnhandledContent: (T) -> Unit
) {
    try {
        observe(owner, Observer { it?.getContentIfNotHandled()?.let(onEventUnhandledContent) })
    }catch (e:Exception){
        e.printStackTrace()
    }
}

