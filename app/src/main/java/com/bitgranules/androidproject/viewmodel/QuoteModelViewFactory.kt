package com.bitgranules.androidproject.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.bitgranules.androidproject.data.SettingsRepository

class QuoteModelViewFactory(private val repository: SettingsRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QuoteModelView::class.java)) {
            @Suppress("UNCHECKED_CAST") return QuoteModelView(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}