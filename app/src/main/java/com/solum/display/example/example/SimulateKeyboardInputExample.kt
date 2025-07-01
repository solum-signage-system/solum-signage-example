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

import android.content.Context
import android.os.SystemClock
import android.view.InputDevice
import android.view.KeyCharacterMap
import android.view.KeyEvent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Lock
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R
import com.solum.display.manager.Input
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun SimulateKeyboardInputExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current
    // Get a CoroutineScope tied to this Composable's lifecycle.
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            // Standard TopAppBar for the screen.
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_find_audio_output_device)) }, // Replace with a more appropriate title if needed
                navigationIcon = {
                    // Back button to navigate up in the navigation stack.
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back" // Accessibility description
                        )
                    }
                },
                // Custom colors for the TopAppBar.
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary // Though no actions here, good for consistency
                )
            )
        },
        modifier = Modifier.fillMaxSize() // Make the Scaffold fill the entire screen.
    ) { innerPadding -> // `innerPadding` is provided by Scaffold to avoid content overlapping with TopAppBar.
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply padding from Scaffold
                .fillMaxSize()         // Fill the available space
                .padding(16.dp),       // Add overall padding for content
            horizontalAlignment = Alignment.CenterHorizontally, // Center content horizontally
            verticalArrangement = Arrangement.Center          // Center content vertically
        ) {
            Text(
                text = "Simulate Volume Key Presses", // More direct title for the content
                style = MaterialTheme.typography.headlineSmall, // Use a more prominent style
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 8.dp) // Add some space below the title
            )
            Text(
                text = "Click buttons below to simulate system volume key events.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 24.dp) // Space before the first button
            )

            // Volume Up Button
            VolumeControlButton(
                text = "Volume Up",
                icon = Icons.Default.KeyboardArrowUp, // Icon for Volume Up
                onClick = {
                    // Call the function to inject a single key press (DOWN and UP)
                    coroutineScope.launch {
                        injectSingleKeyEvent(context, KeyEvent.KEYCODE_VOLUME_UP)
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f) // Make buttons a bit wider
            )

            Spacer(modifier = Modifier.height(16.dp)) // Consistent spacing between buttons

            // Volume Down Button
            VolumeControlButton(
                text = "Volume Down",
                icon = Icons.Default.KeyboardArrowDown, // Icon for Volume Down
                onClick = {
                    coroutineScope.launch {
                        injectSingleKeyEvent(context, KeyEvent.KEYCODE_VOLUME_DOWN)
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Mute Button
            VolumeControlButton(
                text = "Mute / Unmute", // Mute is often a toggle
                icon = Icons.Filled.Lock, // Icon for Mute (or VolumeOff if preferred)
                onClick = {
                    coroutineScope.launch {
                        injectSingleKeyEvent(context, KeyEvent.KEYCODE_VOLUME_MUTE)
                    }
                },
                modifier = Modifier.fillMaxWidth(0.8f)
            )

            // Optional: Add a small note about permissions if this is for a real app
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "Note: Actual volume change requires system permissions.",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Softer color for notes
            )
        }
    }
}

@Composable
fun VolumeControlButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .padding(vertical = 8.dp), // Add vertical padding to the button itself
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp) // Adjust internal padding
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center // Center icon and text within the column
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text, // Icon's content description is the button text
                modifier = Modifier.size(36.dp) // Slightly larger icon
            )
            Spacer(modifier = Modifier.height(8.dp)) // Space between icon and text
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge // Use a slightly larger label style
            )
        }
    }
}

private fun injectSingleKeyEvent(context: Context, keyCode: Int) {
    val now = SystemClock.uptimeMillis()
    Input.injectInputEvent(context, KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode,
        0 /* repeatCount */,
        0 /*metaState*/,
        KeyCharacterMap.VIRTUAL_KEYBOARD,
        0 /*scancode*/,
        0 /*flags*/,
        InputDevice.SOURCE_UNKNOWN))
    Input.injectInputEvent(context, KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode,
        0 /* repeatCount */,
        0 /*metaState*/,
        KeyCharacterMap.VIRTUAL_KEYBOARD,
        0 /*scancode*/,
        0 /*flags*/,
        /*flags*/
        InputDevice.SOURCE_UNKNOWN))
}