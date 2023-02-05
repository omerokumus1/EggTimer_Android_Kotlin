package com.example.eggtimer

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.eggtimer.databinding.ActivityMainBinding
import java.util.*
import kotlin.concurrent.timerTask

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null
    private val eggTimes = mapOf(EggType.SOFT to 5, EggType.MEDIUM to 8, EggType.HARD to 12)
    private var chunk: Int? = null
    private var timer: Timer = Timer()
    private var timerTask: TimerTask? = null
    private var uiHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setClickListeners()
    }

    private fun setClickListeners() {
        setSoftEggClickListener()
        setMediumEggClickListener()
        setHardEggClickListener()
    }

    private fun setHardEggClickListener() {
        binding?.hardEggButton?.setOnClickListener {
            clearTimer()
            setProgressBarMax(EggType.HARD)
            setChunk(EggType.HARD)
            setTimer()
        }
    }

    private fun setMediumEggClickListener() {
        binding?.mediumEggButton?.setOnClickListener {
            clearTimer()
            setProgressBarMax(EggType.MEDIUM)
            setChunk(EggType.MEDIUM)
            setTimer()
        }
    }

    private fun setSoftEggClickListener() {
        binding?.softEggButton?.setOnClickListener {
            setProgressBarMax(EggType.SOFT)
            setChunk(EggType.SOFT)
            setTimer()
        }
    }

    private fun setProgressBarMax(eggType: EggType) {
        when (eggType) {
            EggType.SOFT -> binding?.progressbar?.max = eggTimes[EggType.SOFT] ?: 1
            EggType.MEDIUM -> binding?.progressbar?.max = eggTimes[EggType.MEDIUM] ?: 1
            EggType.HARD -> binding?.progressbar?.max = eggTimes[EggType.HARD] ?: 1
        }
    }

    private fun setChunk(eggType: EggType) {
        chunk = when (eggType) {
            EggType.SOFT -> binding?.progressbar?.max?.div(eggTimes[EggType.SOFT] ?: 1)
            EggType.MEDIUM -> binding?.progressbar?.max?.div(eggTimes[EggType.MEDIUM] ?: 1)
            EggType.HARD -> binding?.progressbar?.max?.div(eggTimes[EggType.HARD] ?: 1)
        }
    }

    private fun clearTimer() {
        timerTask?.cancel()
        timer.purge()
    }

    private fun setTimer() {
        clearTimer()
        resetPrompText()
        val delay = 1000L
        val period = 1000L
        setProgressBarToZero()
        timerTask = getTimerTask()
        timer.scheduleAtFixedRate(timerTask, delay, period)
    }

    private fun getTimerTask() = timerTask {
        Log.i("timerTask", timerTask?.scheduledExecutionTime()?.toString() ?: "")
        binding?.progressbar?.let {
            if (it.progress < it.max) {
                incrementProgressBar()
            }
        }
    }

    private fun incrementProgressBar() {
        binding?.let {
            if (it.progressbar.progress < it.progressbar.max) {
                it.progressbar.progress += chunk ?: 1
            }
            checkIfFinished()
        }
    }

    private fun checkIfFinished() {
        binding?.progressbar?.let {
            if (it.progress >= it.max) {
                timesUp()
            }
        }
    }

    private fun timesUp() {
        clearTimer()
        setPromptText()
        resetViews()
    }

    private fun setPromptText() {
        uiHandler.post {
            binding?.let {
                it.promptText.text = "Your egg is ready. Enjoy!"
            }
        }
    }

    private fun resetViews() {
        timer.schedule(
            timerTask {
                uiHandler.post {
                    resetPrompText()
                    resetProgressBar()
                }
            },
            2000L
        )
    }

    private fun resetPrompText() {
        binding?.promptText?.text = "How do you like your eggs?"
    }

    private fun resetProgressBar() {
        binding?.progressbar?.let {
            it.max = 10
            it.progress = 5
        }
    }

    private fun setProgressBarToZero() {
        binding?.progressbar?.progress = 0
    }

    enum class EggType {
        SOFT,
        MEDIUM,
        HARD
    }
}