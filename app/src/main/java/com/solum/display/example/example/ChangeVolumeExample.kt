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
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeVolumeExample(navController: NavHostController) {
    // Get the current context
    val context = LocalContext.current
    // Get an instance of AudioManager system service
    val audioManager = remember { context.getSystemService(AudioManager::class.java) }

    // State for the current volume level, initialized with the current music stream volume
    var volumeLevel by remember { mutableIntStateOf(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)) }
    // State for the maximum volume level for the music stream
    val maxVolume = remember { audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) }
    // State for the minimum volume level for the music stream (usually 0)
    val minVolume = remember { audioManager.getStreamMinVolume(AudioManager.STREAM_MUSIC) }

    // FocusRequester to programmatically request focus for keyboard input
    val focusRequester = remember { FocusRequester() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_change_volume)) },
                navigationIcon = {
                    IconButton(onClick = {
                        // Navigate up when the back arrow on the primary screen's TopAppBar is clicked
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back" // Provide a content description
                        )
                    }
                },
                // Custom colors for the TopAppBar on the primary display
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->

        Column(
            modifier = Modifier.padding(innerPadding)
        ) {
            // Display the current volume level
            Text("Volume: $volumeLevel", style = MaterialTheme.typography.headlineSmall)

            Spacer(modifier = Modifier.height(16.dp))

            // Row containing the Slider and handling focus for key events
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester) // Assign the FocusRequester to this Row
                    .focusable() // Make this Row focusable so it can receive key events
                    .onKeyEvent { keyEvent -> // Listen for keyboard events
                        // Process only when a key is pressed down (not on release or repeat)
                        if (keyEvent.type == KeyEventType.KeyDown) {
                            when (keyEvent.key) {
                                // When the right arrow key is pressed
                                Key.DirectionRight -> {
                                    // Increase volume level, ensuring it stays within the min..max range
                                    volumeLevel = (volumeLevel + 1).coerceIn(minVolume..maxVolume)
                                    // Set the system volume
                                    setVolume(audioManager, volumeLevel)
                                    true // Indicate that the event was consumed
                                }
                                // When the left arrow key is pressed
                                Key.DirectionLeft -> {
                                    // Decrease volume level, ensuring it stays within the min..max range
                                    volumeLevel = (volumeLevel - 1).coerceIn(minVolume..maxVolume)
                                    // Set the system volume
                                    setVolume(audioManager, volumeLevel)
                                    true // Indicate that the event was consumed
                                }
                                // For any other key, do not consume the event
                                else -> false
                            }
                        } else {
                            // Do not process key up or repeat events here, let them propagate
                            false
                        }
                    }
            ) {
                // Slider for visually changing the volume
                Slider(
                    value = volumeLevel.toFloat(), // Current value of the slider
                    onValueChange = { newValue -> // Callback when the slider value changes
                        volumeLevel = newValue.toInt()
                        // Set the system volume when slider value changes
                        setVolume(audioManager, volumeLevel)
                    },
                    valueRange = minVolume.toFloat()..maxVolume.toFloat(), // Define the range of the slider
                    steps = (maxVolume - minVolume - 1).coerceAtLeast(0), // Number of discrete steps in the slider
                    modifier = Modifier.weight(1f) // Allow the slider to take up available width
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text(stringResource(R.string.text_volume_tooltip), style = MaterialTheme.typography.bodySmall)


            // Request focus for the Row (containing the slider) when the composable first appears
            // This allows keyboard controls to work immediately if this is the primary interactive element.
            LaunchedEffect(Unit) {
                focusRequester.requestFocus()
            }
        }
    }
}

/**
 * Gets the current volume for the music audio stream.
 * Digital signage video often uses STREAM_MUSIC; control its audio via STREAM_MUSIC only.
 * @param audioManager The AudioManager instance.
 * @return The current volume level for STREAM_MUSIC.
 */
private fun getVolume(audioManager: AudioManager): Int =
    audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)

/**
 * Sets the volume for the music audio stream.
 * @param audioManager The AudioManager instance.
 * @param volumeLevel The desired volume level.
 */
private fun setVolume(audioManager: AudioManager, volumeLevel: Int) {
    var flags = 0 // Flags for setStreamVolume, e.g., to show UI
    // Example: condition to show the system volume UI, default is true
    val showSystemUi = true
    if (showSystemUi) {
        flags = flags or AudioManager.FLAG_SHOW_UI
    }
    // Set the volume for the music stream
    audioManager.setStreamVolume(
        AudioManager.STREAM_MUSIC, // Stream type to control
        volumeLevel,               // Volume level to set
        flags                      // Additional flags (e.g., AudioManager.FLAG_SHOW_UI)
    )
}