package com.example.currencycheckertestwork.domain.scheduler

import io.reactivex.Scheduler

interface SchedulerProvider {
    fun main(): Scheduler
    fun io(): Scheduler
}