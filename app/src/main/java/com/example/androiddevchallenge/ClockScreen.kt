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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun ClockScreen(
    state: ClockState,
    onOneMinClicked: () -> Unit = {},
    onTenSecClicked: () -> Unit = {},
    onOneSecClicked: () -> Unit = {},
    onStartTimer: () -> Unit = {},
    onStopTimer: () -> Unit = {},
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 24.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {

        when (state) {
            is ClockState.Running -> {
                ClockView(
                    state.seconds,
                    state.total,
                    state.timerMinute,
                    state.timerDozenSecond,
                    state.timerUnitSecond
                )
            }

            else -> ClockView()
        }

        val isEnable = state is ClockState.Stopped
        ClockSetupView(
            isEnable,
            onOneMinClicked,
            onTenSecClicked,
            onOneSecClicked,
            onStartTimer,
            onStopTimer
        )
    }
}

@Composable
fun ClockView(
    seconds: Int = 1,
    total: Int = 1,
    timerMinute: Int = 1,
    timerDozenSecond: Int = 1,
    timerUnitSecond: Int = 1
) {
    Box(contentAlignment = Alignment.Center) {
        ProgressView(
            modifier = Modifier
                .size(280.dp)
                .alpha(1f),
            color = Color.Blue,
            strokeWidth = 8.dp,
            progress = seconds / total.toFloat(),
        )
        Text(
            color = MaterialTheme.colors.onSurface,
            text = "$timerMinute:${timerDozenSecond}$timerUnitSecond",
            style = MaterialTheme.typography.h4
        )
    }
}

@Composable
fun ProgressView(
    progress: Float,
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colors.primary,
    strokeWidth: Dp = ProgressIndicatorDefaults.StrokeWidth
) {
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Butt)
    }
    Canvas(
        modifier
            .progressSemantics(progress)
            .size(40.dp)
            .focusable()
    ) {
        val sweepAngle = (1 - progress) * 360f
        val startAngle = 270f
        drawCircularIndicator(startAngle, sweepAngle, color, stroke)
    }
}

@Composable
private fun ClockSetupView(
    isEnable: Boolean = true,
    onOneMinClicked: () -> Unit,
    onTenSecClicked: () -> Unit,
    onOneSecClicked: () -> Unit,
    onStartTimer: () -> Unit,
    onStopTimer: () -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEnable) {
            StartStopButton("Start") {
                onStartTimer()
            }
        } else {
            StartStopButton("Stop") {
                onStopTimer()
            }
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            modifier = Modifier
                .padding(8.dp),
            enabled = isEnable,
            onClick = { onOneMinClicked() }
        ) {

            Text(
                text = "1min",
                color = MaterialTheme.colors.onSurface
            )
        }

        Button(
            modifier = Modifier
                .padding(8.dp),
            enabled = isEnable,
            onClick = { onTenSecClicked() }
        ) {

            Text(
                text = "10s",
                color = MaterialTheme.colors.onSurface
            )
        }
        Button(
            modifier = Modifier
                .padding(8.dp),
            enabled = isEnable,
            onClick = { onOneSecClicked() }
        ) {

            Text(
                text = "1s",
                color = MaterialTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun StartStopButton(
    text: String,
    onClick: () -> Unit
) {

    Button(
        onClick = { onClick() },
        modifier = Modifier
            .padding(8.dp),
    ) {
        Text(
            text = text,
            color = MaterialTheme.colors.onSurface
        )
    }
}

private fun DrawScope.drawCircularIndicator(
    startAngle: Float,
    sweep: Float,
    color: Color,
    stroke: Stroke
) {
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

@Preview(showBackground = true)
@Composable
fun ClockViewPreview() {
    ClockScreen(ClockState.Running(60, 10, 1, 1, 1))
}
