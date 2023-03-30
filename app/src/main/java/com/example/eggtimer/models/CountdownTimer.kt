package com.example.eggtimer.models

import android.util.Log
import java.util.*
import kotlin.concurrent.timerTask

class CountdownTimer(
    totalSeconds: Int,
    private val onTick: (() -> Unit)?,
    private val onFinish: (() -> Unit)?
) {
    private var secondsRemaining: Int = 0
    private val timer = Timer()

    init {
        secondsRemaining = totalSeconds
    }

    fun start() {
        Log.i("seconds remaining: ","$secondsRemaining seconds remaining")
        timer.schedule(getTimerTask(), 1000L, 1000L)
    }

    private fun getTimerTask() = timerTask {
        if (secondsRemaining > 0) {
            secondsRemaining--
            Log.i("seconds remaining: ","$secondsRemaining seconds remaining")
            onTick?.invoke()
        }
        if (secondsRemaining == 0) {
            onFinish?.invoke()
            cancel()
        }
    }

    fun cancel() {
        timer.cancel()
    }

    fun invalidate() {
        cancel()
    }

    fun abort() {
        invalidate()
    }
}