package com.example.currencycheckertestwork.data

import com.example.currencycheckertestwork.domain.scheduler.SchedulerProvider
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

class SchedulerProviderImpl @Inject constructor() : SchedulerProvider {
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun io(): CoroutineDispatcher = Dispatchers.IO
}