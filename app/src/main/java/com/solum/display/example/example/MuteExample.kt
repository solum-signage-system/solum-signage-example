/*
 * Copyright © 2025 SOLUM. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.solum.display.example.example

import android.media.AudioManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun MuteExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current
    // Remember an instance of the AudioManager system service.
    // `remember` ensures that getSystemService is called only once during the initial composition
    // or if the context changes (which is unlikely for a screen-level Composable).
    val audioManager = remember { context.getSystemService(AudioManager::class.java) }

    // A MutableState to hold the current mute state of the media stream.
    // Initialize it with the actual current mute state of the system for STREAM_MUSIC.
    var isMuted by remember { mutableStateOf(isMuted(audioManager)) }

    Scaffold(
        topBar = {
            // Standard TopAppBar for the screen.
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_find_audio_output_device)) },
                navigationIcon = {
                    // Back button to navigate up in the navigation stack.
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back" // Provide a meaningful content description
                        )
                    }
                },
                // Custom colors for the TopAppBar.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = Modifier.fillMaxSize() // Make the Scaffold fill the entire screen.
    ) { innerPadding -> // `innerPadding` is provided by Scaffold to avoid content overlapping with TopAppBar.

        // UI layout for the screen
        Column(
            modifier = Modifier
                .fillMaxSize() // Fill the entire screen
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
            verticalArrangement = Arrangement.Center // Center content vertically
        ) {
            // Display the current mute status
            Text(
                text = if (isMuted) "Media Muted" else "Media Unmuted",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(24.dp)) // Add vertical spacing

            // Button to toggle the mute state
            Button(onClick = {
                setMuteStatus(audioManager, !isMuted)
                isMuted = !isMuted
            }) {
                Text(if (isMuted) "Unmute Media" else "Mute Media")
            }
            Spacer(modifier = Modifier.height(16.dp)) // Add vertical spacing

            // Informational text
            Text(
                text = "This controls the media volume (music, videos, etc.).",
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = "Other streams (ringtone, alarm) may need separate control.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

private fun isMuted(audioManager: AudioManager, streamType: Int = AudioManager.STREAM_MUSIC) =
    audioManager.isStreamMute(streamType)

private fun setMuteStatus(audioManager: AudioManager, mute: Boolean, streamType: Int = AudioManager.STREAM_MUSIC) {
    var flags = 0 // Flags for adjustStreamVolume, e.g., to show UI
    // Example: condition to show the system volume UI, default is true
    val showSystemUi = true
    if (showSystemUi) {
        flags = flags or AudioManager.FLAG_SHOW_UI
    }
    audioManager.adjustStreamVolume(
        streamType,
        if (mute) AudioManager.ADJUST_MUTE else AudioManager.ADJUST_UNMUTE,
        flags
    )
}
