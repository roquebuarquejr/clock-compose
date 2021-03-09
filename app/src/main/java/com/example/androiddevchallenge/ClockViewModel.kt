/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.androiddevchallenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class ClockViewModel : ViewModel() {
    private val _state = MutableLiveData<ClockState>(ClockState.Stopped())
    val state: LiveData<ClockState> = _state

    private var currentTimer: Job? = null

    private val minuteTimer = MutableLiveData(0)
    private val secondOneTimer = MutableLiveData(0)
    private val secondTwoTimer = MutableLiveData(0)

    fun minutePress() {
        minuteTimer.value?.let {
            if (it == 5) {
                minuteTimer.value = 0
            } else {
                minuteTimer.value = it + 1
            }
        }
    }

    fun secondDozenPress() {
        secondOneTimer.value?.let {
            if (it == 5) {
                secondOneTimer.value = 0
            } else {
                secondOneTimer.value = it + 1
            }
        }
    }

    fun secondUnitPress() {
        secondTwoTimer.value?.let {
            if (it == 9) {
                secondTwoTimer.value = 0
            } else {
                secondTwoTimer.value = it + 1
            }
        }
    }

    private fun secondDown() {
        if (secondTwoTimer.value != 0) {
            secondTwoTimer.value?.let { secondTwoTimer.value = it - 1 }
            if (
                secondOneTimer.value == 0 &&
                secondTwoTimer.value == 0 &&
                minuteTimer.value == 0
            ) {
                _state.value = ClockState.Stopped(0)
            }
        } else {
            secondTwoTimer.value?.let { secondTwoTimer.value = 9 }
            if (secondOneTimer.value != 0) {
                secondOneTimer.value?.let { secondOneTimer.value = it - 1 }
            } else {
                secondOneTimer.value?.let { secondOneTimer.value = 5 }
                if (minuteTimer.value != 0) {
                    minuteTimer.value?.let { minuteTimer.value = it - 1 }
                } else {
                    _state.value = ClockState.Stopped(0)
                }
            }
        }
    }

    fun startTimer() {
        val currentState = _state.value
        if (currentState !is ClockState.Stopped) {
            return
        }
        val seconds =
            (minuteTimer.value!!.times(60)) + (secondOneTimer.value?.times(10)!!) + secondTwoTimer.value!!
        if (seconds <= 0) {
            return
        }
        _state.value = ClockState.Running(
            seconds,
            seconds,
            minuteTimer.value!!,
            secondOneTimer.value!!,
            secondTwoTimer.value!!
        )
        this.currentTimer = viewModelScope.launch {
            timer(seconds).collect {
                _state.value = if (it == 0) {
                    ClockState.Stopped(0)
                } else {
                    secondDown()
                    ClockState.Running(
                        seconds,
                        it,
                        minuteTimer.value!!,
                        secondOneTimer.value!!,
                        secondTwoTimer.value!!
                    )
                }
            }
        }
    }

    fun stopTimer() {
        currentTimer?.cancel()
        _state.value = ClockState.Stopped(0)
        minuteTimer.value?.let { minuteTimer.value = 0 }
        secondOneTimer.value?.let { secondOneTimer.value = 0 }
        secondTwoTimer.value?.let { secondTwoTimer.value = 0 }
    }

    private fun timer(seconds: Int): Flow<Int> = flow {
        for (s in (seconds - 1) downTo 0) {
            delay(1000L)
            emit(s)
        }
    }
}
