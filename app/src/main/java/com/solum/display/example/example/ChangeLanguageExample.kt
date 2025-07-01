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
import android.os.Build
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R
import java.util.Locale

// List of supported locales for the application.
private val SUPPORTED_LANGUAGE_CODES = listOf( // Changed variable name to follow Kotlin conventions (CODES instead of CODE)
    Locale.ENGLISH,    // English
    Locale("es"),      // Spanish
    Locale("pt"),      // Portuguese
    Locale.KOREAN,     // Korean
    Locale.FRENCH,     // French
    Locale.GERMAN,     // German
    Locale.ITALIAN,    // Italian
    Locale.JAPANESE    // Japanese
    // Add more locales as needed
)

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun ChangeLanguageExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current
    // State to hold the currently selected locale, initialized with the device's current locale.
    var currentSelectedLocale by remember { mutableStateOf(getCurrentDeviceLocale(context)) } // Renamed for clarity

    Scaffold(
        topBar = {
            // Standard TopAppBar for the screen.
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_change_language)) }, // Title for the app bar
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
            // Display the currently selected language.
            Text(
                // Display name of the current locale in its own language, with the first letter capitalized.
                text = "Current language: ${currentSelectedLocale.getDisplayName(currentSelectedLocale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(currentSelectedLocale) else it.toString() }}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp) // Add some space below the text.
            )

            // LazyColumn to efficiently display a scrollable list of languages.
            LazyColumn(
                modifier = Modifier.weight(1f) // Allow the LazyColumn to take up available vertical space.
            ) {
                // Iterate through the list of supported language codes.
                items(SUPPORTED_LANGUAGE_CODES) { locale ->
                    // Composable item for each language.
                    LanguageItem(
                        locale = locale,
                        isSelected = locale == currentSelectedLocale, // Check if this locale is the currently selected one.
                        onLocaleSelected = { selectedLocale ->
                            // When a locale is selected, update the device locale using the SDK.
                            setDeviceLocaleWithSDK(context, selectedLocale) // Renamed for clarity
                            // Update the state to reflect the new selection in the UI.
                            // Note: The actual locale change might require an Activity restart to take full effect.
                            currentSelectedLocale = selectedLocale
                        }
                    )
                    HorizontalDivider() // Add a divider line between language items.
                }
            }
        }
    }
}

@Composable
fun LanguageItem(
    locale: Locale,
    isSelected: Boolean,
    onLocaleSelected: (Locale) -> Unit
) {
    // Row to display the language name and a selection indicator (RadioButton).
    Row(
        modifier = Modifier
            .fillMaxWidth() // Make the Row take the full available width.
            .clickable { onLocaleSelected(locale) } // Make the entire Row clickable.
            .padding(vertical = 12.dp, horizontal = 16.dp), // Add padding inside the Row.
        verticalAlignment = Alignment.CenterVertically, // Align items vertically in the center.
        horizontalArrangement = Arrangement.SpaceBetween // Distribute space between the text and the RadioButton.
    ) {
        // Text to display the language name.
        Text(
            // Display the name of the locale in its own language, capitalizing the first letter.
            text = locale.getDisplayName(locale).replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() },
            style = MaterialTheme.typography.bodyLarge, // Use a larger body text style.
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, // Make selected item's text bold.
            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface // Highlight selected item's text color.
        )

        // RadioButton to indicate selection.
        RadioButton(
            selected = isSelected, // Set the selected state of the RadioButton.
            onClick = { onLocaleSelected(locale) }, // Also trigger selection when RadioButton is clicked directly.
            colors = RadioButtonDefaults.colors(
                selectedColor = MaterialTheme.colorScheme.primary // Color for the selected RadioButton.
            )
        )
    }
}

/**
 * Sets the device's locale using the provided SDK method.
 * This function assumes `com.solum.display.manager.Locale.setLocale` is the correct
 * way to change the application's or device's locale through your specific SDK.
 *
 * @param context The application context.
 * @param locale The Locale to set.
 */
private fun setDeviceLocaleWithSDK(context: Context, locale: Locale) {
    // Call the SDK's method to set the locale.
    // The actual behavior (app-specific vs. system-wide, restart requirement) depends on this SDK call.
    com.solum.display.manager.Locale.setLocale(context, locale.toLanguageTag())
}

/**
 * Gets the current primary locale of the device's configuration.
 *
 * @param context The application context.
 * @return The current primary Locale.
 */
private fun getCurrentDeviceLocale(context: Context): Locale {
    // For Android N (API 24) and above, get the primary locale from the locale list.
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales[0]
    } else {
        // For older versions, get the deprecated single locale.
        @Suppress("DEPRECATION")
        context.resources.configuration.locale
    }
}