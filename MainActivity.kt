package com.example.fitnessvoiceapp

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.fitnessvoiceapp.ui.theme.FitnessVoiceAppTheme
import java.util.*

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        setContent {
            FitnessVoiceAppTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WorkoutScreen(tts)
                }
            }
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts.language = Locale.US
        }
    }

    override fun onDestroy() {
        if (::tts.isInitialized) {
            tts.stop()
            tts.shutdown()
        }
        super.onDestroy()
    }
}

@Composable
fun WorkoutScreen(tts: TextToSpeech) {
    val workouts = listOf(
        "Jumping Jacks - 30 seconds",
        "Push-ups - 15 reps",
        "Squats - 20 reps",
        "Plank - 30 seconds",
        "Lunges - 10 each leg"
    )

    var started by remember { mutableStateOf(false) }
    var currentStep by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text("Voice Guided Workout", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(workouts.size) { index ->
                Text("${index + 1}. ${workouts[index]}",
                    style = if (index == currentStep) MaterialTheme.typography.bodyLarge.copy(color = MaterialTheme.colorScheme.primary)
                            else MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 4.dp))
            }
        }

        Button(onClick = {
            if (!started) {
                started = true
                currentStep = 0
                tts.speak(workouts[0], TextToSpeech.QUEUE_FLUSH, null, null)
            } else {
                if (currentStep < workouts.size - 1) {
                    currentStep++
                    tts.speak(workouts[currentStep], TextToSpeech.QUEUE_FLUSH, null, null)
                } else {
                    tts.speak("Workout complete! Great job!", TextToSpeech.QUEUE_FLUSH, null, null)
                    started = false
                    currentStep = 0
                }
            }
        }) {
            Text(if (!started) "Start Workout" else "Next Step")
        }
    }
}
