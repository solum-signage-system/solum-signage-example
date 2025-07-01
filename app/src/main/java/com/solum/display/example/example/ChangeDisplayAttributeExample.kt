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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.isEmpty
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
import com.solum.display.manager.Display


@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun ChangeDisplayAttributeExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current

    // Retrieve the list of supported display operations.
    // `remember` ensures this is fetched only once or when the context changes.
    // Assuming `Display.getSupportedDisplayOperations(context)` returns a list of DisplayOperation objects
    // that have `attribute`, `displayId`, `value`, `minimum`, and `maximum` properties.
    val displayOperations = remember { Display.getSupportedDisplayOperations(context) }

    Scaffold(
        topBar = {
            // Standard TopAppBar for the screen.
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_change_display_attribute)) },
                navigationIcon = {
                    // Back button to navigate up in the navigation stack.
                    IconButton(onClick = {
                        navController.navigateUp()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back" // Provide a meaningful content description for accessibility
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
        // Column to arrange display operation controls vertically.
        Column(
            modifier = Modifier
                .padding(innerPadding) // Apply padding to respect the TopAppBar area.
                .padding(16.dp) // Add additional padding around the content.
        ) {
            // Iterate through each supported display operation.
            displayOperations.forEach { operation ->
                // FocusRequester to programmatically request focus for keyboard input on the Row.
                val focusRequester = remember { FocusRequester() }
                // State to hold the current value of the display attribute, initialized with the operation's current value.
                var value by remember { mutableIntStateOf(operation.value) }
                // Remember the minimum and maximum allowed values for this attribute.
                val minValue = remember { operation.minimum }
                val maxValue = remember { operation.maximum }

                // Display the attribute name.
                Text("Attribute: ${operation.attribute}", style = MaterialTheme.typography.titleMedium) // Changed to titleMedium for better hierarchy
                // Display the ID of the display(s) affected by this operation.
                Text("Affected displays: ${operation.displayId}", style = MaterialTheme.typography.bodySmall) // Changed to bodySmall
                Spacer(modifier = Modifier.height(8.dp)) // Add some space

                // Row to group the Slider and handle focus for keyboard events.
                Row(
                    verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center.
                    horizontalArrangement = Arrangement.spacedBy(8.dp), // Space between items in the Row.
                    modifier = Modifier
                        .fillMaxWidth() // Make the Row take the full width.
                        .focusRequester(focusRequester) // Assign the FocusRequester to this Row.
                        .focusable() // Make this Row focusable so it can receive key events.
                        .onKeyEvent { keyEvent -> // Listen for keyboard events on this Row.
                            // Process only when a key is pressed down (KeyEventType.KeyDown).
                            // This avoids handling events on key release or repeat if not desired.
                            if (keyEvent.type == KeyEventType.KeyDown) {
                                when (keyEvent.key) {
                                    // When the right arrow key is pressed, increment the value.
                                    Key.DirectionRight -> {
                                        value = (value + 1).coerceIn(minValue..maxValue) // Increment and coerce within min/max bounds.
                                        operation.value = value // Update the actual display operation's value.
                                        true // Indicate that the event was consumed.
                                    }
                                    // When the left arrow key is pressed, decrement the value.
                                    Key.DirectionLeft -> {
                                        // Corrected: Should decrement for left arrow.
                                        value = (value - 1).coerceIn(minValue..maxValue) // Decrement and coerce.
                                        operation.value = value // Update the actual display operation's value.
                                        true // Indicate that the event was consumed.
                                    }
                                    // For any other key, do not consume the event, let it propagate.
                                    else -> false
                                }
                            } else {
                                // Do not process key up or repeat events here; let them propagate if needed elsewhere.
                                false
                            }
                        }
                ) {
                    // Slider for visually and interactively changing the attribute's value.
                    Slider(
                        value = value.toFloat(), // Current value of the slider (needs to be a Float).
                        onValueChange = { newValueFromSlider -> // Callback when the slider's value changes through user interaction.
                            value = newValueFromSlider.toInt() // Update the state variable.
                            operation.value = value // Update the actual display operation's value.
                        },
                        valueRange = minValue.toFloat()..maxValue.toFloat(), // Define the valid range for the slider.
                        // Number of discrete steps in the slider.
                        // `coerceAtLeast(0)` ensures steps is not negative if max <= min.
                        steps = (maxValue - minValue - 1).coerceAtLeast(0),
                        modifier = Modifier.weight(1f) // Allow the slider to take up available horizontal space in the Row.
                    )
                    // Display the current value next to the slider for clarity.
                    Text(
                        text = value.toString(),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 8.dp) // Add some padding to separate from slider
                    )
                }
                Spacer(modifier = Modifier.height(24.dp)) // Add more space between different operations.
            }

            // If there are no display operations, show a message.
            if (displayOperations.isEmpty()) {
                Text(
                    "No display attributes available to change.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.align(Alignment.CenterHorizontally) // Center the text if no operations
                )
            }
        }
    }
}