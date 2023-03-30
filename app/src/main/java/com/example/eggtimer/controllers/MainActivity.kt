package com.example.eggtimer.controllers

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.eggtimer.databinding.ActivityMainBinding
import com.example.eggtimer.models.CountdownTimer

class MainActivity : AppCompatActivity() {
    private val eggTimes = mapOf(EggType.SOFT to 3, EggType.MEDIUM to 4, EggType.HARD to 5)
    private var progressChunk: Int = 1
    private var timer: CountdownTimer? = null
    private var titleTimer: CountdownTimer? = null

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        binding.softEggButton.setOnClickListener { eggButtonClicked(binding.softEggButton) }
        binding.mediumEggButton.setOnClickListener { eggButtonClicked(binding.mediumEggButton) }
        binding.hardEggButton.setOnClickListener { eggButtonClicked(binding.hardEggButton) }
    }

    private fun eggButtonClicked(eggButton: Button) {
        val eggType = getEggType(eggButton)
        val time = eggTimes[eggType]
        resetViews()
        cancelTimers()
        setProgressBarMax(eggType)
        setProgressChunk(eggType)
        setTimer(time)
    }

    private fun getEggType(eggButton: Button) = when (eggButton.text) {
        "Soft" -> EggType.SOFT
        "Medium" -> EggType.MEDIUM
        "Hard" -> EggType.HARD
        else -> EggType.SOFT

    }

    private fun resetViews() {
        setProgressBarToZero()
        resetTitleLabel()
    }

    private fun setProgressBarToZero() {
        binding.progressbar.progress = 0
    }

    private fun resetTitleLabel() {
        binding.promptText.text = "How do you like your eggs?"
    }

    private fun cancelTimers() {
        timer?.cancel()
        titleTimer?.cancel()
    }

    private fun setProgressBarMax(eggType: EggType) {
        when (eggType) {
            EggType.SOFT -> binding.progressbar.max = eggTimes[EggType.SOFT] ?: 1
            EggType.MEDIUM -> binding.progressbar.max = eggTimes[EggType.MEDIUM] ?: 1
            EggType.HARD -> binding.progressbar.max = eggTimes[EggType.HARD] ?: 1
        }
    }

    private fun setProgressChunk(eggType: EggType) {
        progressChunk = when (eggType) {
            EggType.SOFT -> binding.progressbar.max.div(eggTimes[EggType.SOFT] ?: 1)
            EggType.MEDIUM -> binding.progressbar.max.div(eggTimes[EggType.MEDIUM] ?: 1)
            EggType.HARD -> binding.progressbar.max.div(eggTimes[EggType.HARD] ?: 1)
        }
    }

    private fun setTimer(time: Int?) {
        if (time == null) return
        timer = CountdownTimer(time, onTick, onFinish)
        timer?.start()
    }

    private val onTick: () -> Unit = {
        runOnUiThread { binding.progressbar.progress += progressChunk }
    }

    private val onFinish: () -> Unit = {
        runOnUiThread {
            binding.promptText.text = "Done!"
            binding.progressbar.progress += progressChunk
            setTitleTimer()
        }
    }

    private fun setTitleTimer() {
        titleTimer?.cancel()
        titleTimer = CountdownTimer(
            2,
            onTick = null
        ) {
            runOnUiThread {
                resetTitleLabel()
                resetProgressBar()
            }
        }
        titleTimer?.start()
    }

    private fun resetProgressBar() {
        binding.progressbar.let {
            it.max = 10
            it.progress = 5
        }
    }


    enum class EggType {
        SOFT,
        MEDIUM,
        HARD
    }
}