package com.example.currencycheckertestwork.domain.scheduler

import kotlinx.coroutines.CoroutineDispatcher

interface SchedulerProvider {
    fun main(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}