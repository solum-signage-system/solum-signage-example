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
import android.media.AudioAttributes
import android.media.AudioDeviceInfo
import android.media.AudioManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R

/**
 * A Composable screen that demonstrates how to find and list available audio output devices.
 * It uses AudioManager to get general output devices and, for Android Tiramisu (API 33) and above,
 * it also shows how to get output devices specific to a given audio stream type (e.g., music).
 *
 * @param navController The NavHostController for navigating back.
 */
@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun FindAudioOutputDeviceExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current
    // Remember an instance of the AudioManager system service.
    // `remember` ensures that getSystemService is called only once during the initial composition
    // or if the context changes (which is unlikely for a screen-level Composable).
    val audioManager = remember { context.getSystemService(AudioManager::class.java) }

    // Remember the list of general audio output devices.
    // This list is fetched once when the Composable is first composed.
    // AudioManager.GET_DEVICES_OUTPUTS flag retrieves all output devices.
    val outputDevices = remember { audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS) }

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

        // Column to arrange UI elements vertically.
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply padding to respect the TopAppBar area.
                .padding(16.dp)        // Add some additional padding around the content.
        ) {
            // Section title for general supported audio output devices.
            Text(
                stringResource(R.string.text_supported_audio_output),
                style = MaterialTheme.typography.headlineSmall // Use a headline style from the theme.
            )
            Spacer(modifier = Modifier.height(8.dp)) // Add a small space after the title.

            // Iterate through the list of general output devices and display their information.
            // Note: If `outputDevices` could be empty, you might want to add a placeholder Text.
            if (outputDevices.isNotEmpty()) {
                for (device in outputDevices) {
                    Text(
                        "Type: ${device.type.toAudioOutputTypeString()}, Name: ${device.productName}",
                        style = MaterialTheme.typography.bodyMedium // Use a body style for device details.
                    )
                    Spacer(modifier = Modifier.height(4.dp)) // Add a small space between device entries.
                }
            } else {
                Text(
                    "No general audio output devices found.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }


            Spacer(modifier = Modifier.height(24.dp)) // Add a larger space before the next section.

            // This section is only relevant for Android Tiramisu (API 33) and above,
            // as `getAudioDevicesForAttributes` was introduced in this version.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // Section title for audio output devices specific to media playback.
                Text(
                    stringResource(R.string.text_supported_audio_output_for_media),
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Get and display output devices specifically for the MUSIC stream type.
                // This call is wrapped in the API level check.
                val mediaOutputDevices = getOutputDeviceForStreamType(context)
                if (mediaOutputDevices.isNotEmpty()) {
                    mediaOutputDevices.forEach { device ->
                        Text(
                            "Type: ${device.type.toAudioOutputTypeString()}, Name: ${device.productName}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                } else {
                    Text(
                        "No specific audio output devices found for media.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

/**
 * Retrieves a list of [AudioDeviceInfo] suitable for the given audio stream type.
 * This function is available only on Android Tiramisu (API 33) and above.
 * It maps traditional stream types to modern [AudioAttributes] to query the AudioManager.
 *
 * @param context The application context.
 * @param streamType The AudioManager stream type (e.g., [AudioManager.STREAM_MUSIC]).
 *                   Defaults to [AudioManager.STREAM_MUSIC].
 * @return A list of [AudioDeviceInfo] objects that can handle the specified stream type.
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU) // Annotation to ensure this is only called on API 33+
private fun getOutputDeviceForStreamType(
    context: Context,
    streamType: Int = AudioManager.STREAM_MUSIC // Default to music stream
): List<AudioDeviceInfo> {
    // Get an instance of AudioManager.
    val audioManager = context.getSystemService(AudioManager::class.java)

    // Determine the AudioAttributes usage based on the stream type.
    val usage = when (streamType) {
        AudioManager.STREAM_MUSIC -> AudioAttributes.USAGE_MEDIA
        AudioManager.STREAM_RING -> AudioAttributes.USAGE_NOTIFICATION_RINGTONE
        AudioManager.STREAM_ALARM -> AudioAttributes.USAGE_ALARM
        AudioManager.STREAM_VOICE_CALL -> AudioAttributes.USAGE_VOICE_COMMUNICATION
        // Add more mappings as needed for other stream types.
        else -> AudioAttributes.USAGE_UNKNOWN // Fallback for unhandled stream types.
    }

    // Determine the AudioAttributes content type based on the stream type.
    val contentType = when (streamType) {
        AudioManager.STREAM_MUSIC -> AudioAttributes.CONTENT_TYPE_MUSIC
        AudioManager.STREAM_RING -> AudioAttributes.CONTENT_TYPE_SONIFICATION
        AudioManager.STREAM_ALARM -> AudioAttributes.CONTENT_TYPE_SONIFICATION
        AudioManager.STREAM_VOICE_CALL -> AudioAttributes.CONTENT_TYPE_SPEECH
        // Add more mappings as needed.
        else -> AudioAttributes.CONTENT_TYPE_UNKNOWN // Fallback.
    }

    // Build the AudioAttributes object.
    val audioAttributes = AudioAttributes.Builder()
        .setUsage(usage)
        .setContentType(contentType)
        .build()

    // Retrieve and return the list of audio devices that match the specified attributes.
    // `getAudioDevicesForAttributes` returns an array, so convert it to a List.
    return audioManager.getAudioDevicesForAttributes(audioAttributes).toList()
}

/**
 * Extension function for an [Int] (representing an [AudioDeviceInfo.getType()] constant)
 * to convert it into a human-readable string.
 *
 * This function provides a clear textual representation for various audio output device types
 * defined in [AudioDeviceInfo].
 *
 * @receiver The integer value corresponding to an [AudioDeviceInfo.getType()].
 * @return A [String] describing the audio device type. Returns "Unknown" if the type
 *         is not explicitly handled or is [AudioDeviceInfo.TYPE_UNKNOWN].
 */
private fun Int.toAudioOutputTypeString(): String =
    when (this) {
        AudioDeviceInfo.TYPE_UNKNOWN -> "Unknown" // Device type is unknown.
        AudioDeviceInfo.TYPE_BUILTIN_EARPIECE -> "Builtin Earpiece" // Device is the earpiece on a phone.
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER -> "Builtin Speaker" // Device is the speaker(s) built into the device.
        AudioDeviceInfo.TYPE_WIRED_HEADSET -> "Wired Headset" // Device is a wired headset (headphones + microphone).
        AudioDeviceInfo.TYPE_WIRED_HEADPHONES -> "Wired Headphones" // Device is wired headphones (no microphone).
        AudioDeviceInfo.TYPE_LINE_ANALOG -> "Line analog" // Device is an analog line-level connection (e.g., AUX).
        AudioDeviceInfo.TYPE_LINE_DIGITAL -> "Line digital" // Device is a digital line-level connection (e.g., S/PDIF).
        AudioDeviceInfo.TYPE_BLUETOOTH_SCO -> "Bluetooth SCO" // Device is a Bluetooth device supporting SCO (Synchronous Connection Oriented) audio, typically for voice.
        AudioDeviceInfo.TYPE_BLUETOOTH_A2DP -> "Bluetooth A2DP" // Device is a Bluetooth device supporting A2DP (Advanced Audio Distribution Profile) audio, typically for music.
        AudioDeviceInfo.TYPE_HDMI -> "HDMI" // Device is connected via HDMI (High-Definition Multimedia Interface).
        AudioDeviceInfo.TYPE_HDMI_ARC -> "HDMI ARC" // Device is connected via HDMI ARC (Audio Return Channel).
        AudioDeviceInfo.TYPE_USB_DEVICE -> "USB Device" // Device is a USB audio device.
        AudioDeviceInfo.TYPE_USB_ACCESSORY -> "USB Accessory" // Device is a USB accessory acting as an audio device.
        AudioDeviceInfo.TYPE_DOCK -> "Dock" // Device is a docking station.
        AudioDeviceInfo.TYPE_FM -> "FM" // Device is an FM (Frequency Modulation) transmitter. (Less common for output)
        AudioDeviceInfo.TYPE_FM_TUNER -> "FM Tuner" // Device is an FM radio tuner. (Typically an input, but listed for completeness if API uses it for output scenarios)
        AudioDeviceInfo.TYPE_TV_TUNER -> "TV Tuner" // Device is a TV tuner. (Typically an input)
        AudioDeviceInfo.TYPE_TELEPHONY -> "Telephony" // Device is the telephony device (e.g., for phone calls).
        AudioDeviceInfo.TYPE_AUX_LINE -> "Aux Line" // Device is an auxiliary line-level connection.
        AudioDeviceInfo.TYPE_IP -> "IP" // Device is an IP-based audio device (e.g., network streaming).
        AudioDeviceInfo.TYPE_BUS -> "Bus" // Device is on an internal system bus (e.g., for inter-processor audio).
        AudioDeviceInfo.TYPE_USB_HEADSET -> "USB Headset" // Device is a USB headset.
        AudioDeviceInfo.TYPE_HEARING_AID -> "Hearing Aid" // Device is a hearing aid.
        AudioDeviceInfo.TYPE_BUILTIN_SPEAKER_SAFE -> "Builtin Speaker safe" // Device is a speaker with volume limitations for safety.
        AudioDeviceInfo.TYPE_REMOTE_SUBMIX -> "Remote Submix" // Device is a remote submix, often used for screen casting or virtual audio.
        AudioDeviceInfo.TYPE_BLE_HEADSET -> "BLE Headset" // Device is a Bluetooth Low Energy (BLE) headset. (Android S/API 31+)
        AudioDeviceInfo.TYPE_BLE_SPEAKER -> "BLE Speaker" // Device is a Bluetooth Low Energy (BLE) speaker. (Android T/API 33+)
        AudioDeviceInfo.TYPE_HDMI_EARC -> "HDMI EARC" // Device is connected via HDMI eARC (Enhanced Audio Return Channel). (Android R/API 30+)
        AudioDeviceInfo.TYPE_BLE_BROADCAST -> "BLE Broadcast" // Device is a Bluetooth Low Energy (BLE) broadcast sink. (Android T/API 33+)
        AudioDeviceInfo.TYPE_DOCK_ANALOG -> "Dock analog" // Device is an analog connection through a dock. (Android U/API 34+)
        // Add any new types here as they are introduced in future Android versions.
        else -> "Unknown ($this)" // Fallback for any type not explicitly listed. Including the raw type value can help in debugging.
    }