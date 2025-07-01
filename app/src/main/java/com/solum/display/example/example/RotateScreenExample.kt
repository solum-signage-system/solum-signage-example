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

import android.view.Surface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R
import com.solum.display.manager.Display

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun RotateScreenExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current

    Scaffold(
        topBar = {
            // Standard TopAppBar for the screen.
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_rotate_screen)) }, // Title for the app bar
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
        ) {
            // Informational text for the user.
            Text(text = "Rotate the screen to change the orientation")

            // Row to arrange rotation buttons horizontally.
            Row {
                // Array of Pairs: first is the display text for the button, second is the Surface.ROTATION_* constant.
                arrayOf(
                    "0" to Surface.ROTATION_0,
                    "90" to Surface.ROTATION_90,
                    "180" to Surface.ROTATION_180,
                    "270" to Surface.ROTATION_270
                ).forEach { rotationData -> // Using a descriptive variable name for the Pair.

                    // Button for each rotation option.
                    Button(
                        onClick = {
                            // Call the SDK function to change the screen orientation.
                            // Assumes `Display.changeOrientation` is a method in your SDK.
                            // `android.view.Display.DEFAULT_DISPLAY` refers to the primary device display.
                            Display.changeOrientation(context, android.view.Display.DEFAULT_DISPLAY, rotationData.second)
                        },
                        modifier = Modifier.padding(horizontal = 4.dp) // Add some horizontal padding for spacing between buttons.
                    ) {
                        // Text displayed inside the button, using the first element of the Pair.
                        Text(text = rotationData.first)
                    }
                }
            }
        }
    }
}