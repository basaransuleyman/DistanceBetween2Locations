package com.example.maps

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinModule {
    val viewModelModule = module {
        viewModel { ViewModel(get()) }
    }
}