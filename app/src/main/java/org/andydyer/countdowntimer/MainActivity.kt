package org.andydyer.countdowntimer

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.andydyer.countdowntimer.ui.theme.CountdownTimerTheme
import java.util.*
import kotlin.concurrent.scheduleAtFixedRate
import kotlin.math.max

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CountdownTimerTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Timer(10_000)
                }
            }
        }
    }
}

@Composable
fun Timer(duration: Long) {
    val end = remember { mutableStateOf(0L) }
    val remaining = remember { mutableStateOf(duration) }
    val remainingPercent = remaining.value * 1F / duration

    val timer = remember { Timer() }
    val timerTask = remember { mutableStateOf<TimerTask>(EmptyTimerTask) }

    if (timerTask.value != EmptyTimerTask && remaining.value <= 0L) {
        timerTask.value.cancel()
        timerTask.value = EmptyTimerTask
    }

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colors.primary)
            .clickable {
                if (end.value != 0L) {
                    remaining.value = duration
                    end.value = 0L
                } else {
                    end.value = System.currentTimeMillis() + duration
                    timerTask.value = timer.scheduleAtFixedRate(delay = 1000L, period = 1L) {
                        scope.launch(Dispatchers.Main) {
                            remaining.value = max(end.value - System.currentTimeMillis(), 0)
                        }
                    }
                }
            },
        contentAlignment = Alignment.BottomStart
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.secondary)
                .fillMaxHeight(remainingPercent)
                .fillMaxWidth()
        )
        Text(
            text = "%.0f".format(remaining.value / 1000F),
            color = Color.White,
            fontSize = (300 * remainingPercent).sp,
            modifier = Modifier
                .fillMaxHeight(max(remainingPercent, .075F))
                .fillMaxWidth()
                .padding(16.dp),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    CountdownTimerTheme {
        Timer(10_000)
    }
}

private object EmptyTimerTask : TimerTask() {
    override fun run() {}
}
