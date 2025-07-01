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
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.minDimension
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin


@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun ChangeTimeZoneExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current
    // State to hold the currently selected time zone ID, initialized with the device's current time zone.
    var currentSelectedTimeZoneId by remember { mutableStateOf(getCurrentDeviceTimeZone().id) } // Renamed for clarity
    // Remember the list of available time zone IDs. This is fetched once.
    val timeZoneIdList = remember { TimeZone.getAvailableIDs() }

    Scaffold(
        topBar = {
            // Standard TopAppBar for the screen.
            TopAppBar(
                // Title for the app bar - consider changing R.string.navigation_change_language to something like R.string.navigation_change_timezone
                title = { Text(stringResource(R.string.navigation_change_language)) },
                navigationIcon = {
                    // Back button to navigate up in the navigation stack.
                    IconButton(onClick = {
                        navController.navigateUp() // Navigate to the previous screen
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Standard back arrow icon
                            contentDescription = "Navigate back" // Accessibility description for the icon
                        )
                    }
                },
                // Custom colors for the TopAppBar using the app's MaterialTheme.
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
                .padding(innerPadding) // Apply padding from Scaffold to avoid overlap with the TopAppBar.
                .padding(16.dp) // Add additional padding around the content within the Column.
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally // Center content horizontally.
        ) {
            // Display the currently selected time zone ID.
            Text(
                text = "Current timeZone: $currentSelectedTimeZoneId",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp) // Add some space below the text.
            )
            // Display an analog clock that updates in real-time.
            AnalogClock()

            // LazyColumn to efficiently display a scrollable list of time zone IDs.
            LazyColumn(
                modifier = Modifier.weight(1f) // Allow the LazyColumn to take up available vertical space.
            ) {
                // Iterate through the list of available time zone IDs.
                items(timeZoneIdList) { timeZoneId ->
                    // Composable item for each time zone ID.
                    TimeZoneItem(
                        id = timeZoneId,
                        isSelected = timeZoneId == currentSelectedTimeZoneId, // Check if this ID is the currently selected one.
                        onSelected = { selectedTimeZoneId ->
                            // When a time zone is selected, update the device's time zone using the SDK.
                            setDeviceTimeZoneWithSDK(context, selectedTimeZoneId) // Renamed for clarity
                            // Update the state to reflect the new selection in the UI.
                            currentSelectedTimeZoneId = selectedTimeZoneId
                        }
                    )
                    HorizontalDivider() // Add a divider line between time zone items.
                }
            }
        }
    }
}

@Composable
fun TimeZoneItem(
    id: String, // The time zone ID string (e.g., "America/New_York")
    isSelected: Boolean,
    onSelected: (String) -> Unit // Callback when this item is selected
) {
    // Row to display the time zone ID and a selection indicator (RadioButton).
    Row(
        modifier = Modifier
            .fillMaxWidth() // Make the Row take the full available width.
            .clickable { onSelected(id) } // Make the entire Row clickable.
            .padding(vertical = 12.dp, horizontal = 16.dp), // Add padding inside the Row.
        verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center.
        horizontalArrangement = Arrangement.SpaceBetween // Distribute space between the text and the RadioButton.
    ) {
        // Text to display the time zone ID.
        Text(
            text = id, // Display the raw time zone ID.
            style = MaterialTheme.typography.bodyLarge, // Use a larger body text style.
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, // Make selected item's text bold.
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface // Highlight selected item's text color.
        )

        // RadioButton to indicate selection.
        RadioButton(
            selected = isSelected, // Set the selected state of the RadioButton.
            onClick = { onSelected(id) }, // Also trigger selection when RadioButton is clicked directly.
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary // Color for the selected RadioButton.
            )
        )
    }
}


@Composable
fun AnalogClock(modifier: Modifier = Modifier, timeZone: TimeZone = TimeZone.getDefault()) { // Added modifier and timeZone parameter
    // State to hold the current time, represented by a Calendar instance,
    // initialized with the specified timeZone.
    var calendar by remember(timeZone) { mutableStateOf(Calendar.getInstance(timeZone)) } // Re-initialize if timeZone changes

    // LaunchedEffect to update the calendar every second.
    // The effect will restart if `timeZone` changes.
    LaunchedEffect(key1 = timeZone) { // Use timeZone as a key to re-launch if it changes
        while (true) {
            // Get a new Calendar instance for the specified timeZone to ensure it reflects
            // any external changes or the correct current time in that zone.
            calendar = Calendar.getInstance(timeZone)
            delay(1000) // Wait for 1 second.
        }
    }

    // Extract hour, minute, and second from the calendar.
    // Note: Calendar.HOUR is for 12-hour format (0-11), Calendar.HOUR_OF_DAY is for 24-hour format (0-23).
    // We use HOUR_OF_DAY and then modulo 12 for a typical analog clock display (12 instead of 0 for noon/midnight).
    val currentHour24 = calendar.get(Calendar.HOUR_OF_DAY)
    val hours = if (currentHour24 % 12 == 0) 12 else currentHour24 % 12 // Display 12 for 0 or 12
    val minutes = calendar.get(Calendar.MINUTE)
    val seconds = calendar.get(Calendar.SECOND)

    // Canvas to draw the analog clock.
    Canvas(modifier = modifier.size(200.dp)) { // Define the size of the clock using the passed modifier.
        val centerX = size.width / 2
        val centerY = size.height / 2
        val radius = size.minDimension / 2.2f // Radius of the clock face, slightly smaller to avoid clipping hands.

        // Draw clock face (a simple circle).
        drawCircle(
            color = Color.LightGray, // Changed color for better visibility
            radius = radius,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color.DarkGray, // Outline for the clock face
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.dp.toPx())
        )
        // Draw center dot
        drawCircle(
            color = Color.Black,
            radius = 4.dp.toPx(),
            center = Offset(centerX, centerY)
        )


        // Draw hour hand.
        // Angle calculation: (current hour + fraction of the hour based on minutes) * 30 degrees per hour.
        // -90 degrees to make 0 degrees point upwards (12 o'clock).
        val hourAngleRad = Math.toRadians((hours + minutes / 60.0) * 30.0 - 90.0).toFloat()
        drawLine(
            color = Color.Black,
            start = Offset(centerX, centerY), // Start from the center.
            end = Offset( // End point of the hour hand.
                centerX + radius * 0.5f * cos(hourAngleRad), // 50% of radius length
                centerY + radius * 0.5f * sin(hourAngleRad)
            ),
            strokeWidth = 6.dp.toPx() // Thickness of the hour hand.
        )

        // Draw minute hand.
        // Angle calculation: (current minute + fraction of the minute based on seconds) * 6 degrees per minute.
        // -90 degrees for upward orientation.
        val minuteAngleRad = Math.toRadians((minutes + seconds / 60.0) * 6.0 - 90.0).toFloat()
        drawLine(
            color = Color.Black,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + radius * 0.75f * cos(minuteAngleRad), // 75% of radius length
                centerY + radius * 0.75f * sin(minuteAngleRad)
            ),
            strokeWidth = 4.dp.toPx() // Thickness of the minute hand.
        )

        // Draw second hand.
        // Angle calculation: current second * 6 degrees per second.
        // -90 degrees for upward orientation.
        val secondAngleRad = Math.toRadians(seconds * 6.0 - 90.0).toFloat()
        drawLine(
            color = Color.Red,
            start = Offset(centerX, centerY),
            end = Offset(
                centerX + radius * 0.9f * cos(secondAngleRad), // 90% of radius length
                centerY + radius * 0.9f * sin(secondAngleRad)
            ),
            strokeWidth = 2.dp.toPx() // Thickness of the second hand.
        )
    }
}

/**
 * Sets the device's or application's time zone using a specific SDK method.
 *
 * This function is a wrapper around an assumed SDK call: `com.solum.display.manager.TimeZone.setTimeZone`.
 * The actual behavior of this SDK call (e.g., whether it affects the entire system or just the app,
 * and whether a restart or specific permissions are needed) depends on the SDK's implementation.
 *
 * @param context The application context, often required by SDK methods.
 * @param timeZoneId The ID string of the time zone to set (e.g., "America/New_York", "Europe/London").
 *                   It's expected that the `com.solum.display.manager.TimeZone.setTimeZone` method
 *                   accepts a time zone ID string.
 */
private fun setDeviceTimeZoneWithSDK(context: Context, timeZoneId: String) { // Renamed parameter for clarity
    // Call the proprietary SDK method to set the time zone.
    com.solum.display.manager.TimeZone.setTimeZone(context, timeZoneId)
}

/**
 * Gets the default TimeZone for the Java Virtual Machine (JVM) on this device.
 *
 * This typically reflects the device's currently configured system time zone.
 * Note that changing this default JVM time zone programmatically (e.g., via TimeZone.setDefault())
 * might not always affect system-level settings or other applications, and its effect can be
 * specific to the current application's process.
 *
 * @return The default TimeZone object.
 */
private fun getCurrentDeviceTimeZone(): TimeZone = TimeZone.getDefault()