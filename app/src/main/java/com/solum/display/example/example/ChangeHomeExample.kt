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
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavHostController
import com.solum.display.example.R

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun ChangeHomeExample(navController: NavHostController) {
    val context = LocalContext.current
    val packageManager = context.packageManager

    // State variables
    var currentLauncher by remember { mutableStateOf<LauncherAppInfo?>(null) }
    var availableLaunchers by remember { mutableStateOf<List<LauncherAppInfo>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // Load launcher information (using LaunchedEffect)
    LaunchedEffect(key1 = Unit) {
        isLoading = true
        currentLauncher = getCurrentLauncher(context, packageManager)
        availableLaunchers = getAvailableLaunchers(context, packageManager)
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_change_home_application)) }, // Title modified
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Navigate back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(vertical = 24.dp))
                Text("Loading launcher information...")
            } else {
                // Display current default launcher information
                currentLauncher?.let { launcher ->
                    Text(
                        "Current Default Launcher",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LauncherRow(launcherInfo = launcher, isCurrentDefault = true, onInfoClick = { }) {
                        // No specific action when current launcher is clicked (or navigate to app info)
                        //openApplicationDetailsSettings(context, launcher.packageName)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // List of available launchers
                Text(
                    "Available Launchers",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                if (availableLaunchers.isEmpty()) {
                    Text("No other launchers found.")
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(availableLaunchers) { launcher ->
                            LauncherRow(
                                launcherInfo = launcher,
                                isCurrentDefault = launcher.packageName == currentLauncher?.packageName && launcher.activityName == currentLauncher?.activityName,
                                onInfoClick = { }
                            ) {
                                // On launcher item click:
                                // 1. Navigate to the app's info screen (using the function below)
                                // openApplicationDetailsSettings(context, launcher.packageName)
                                // 2. Or guide to the system's default app settings screen (same function as the button below)
                                //openDefaultAppSettings(context)
                            }
                            HorizontalDivider()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button to navigate to system default app settings screen
                Button(
                    onClick = { /*openDefaultAppSettings(context)*/ },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Change Default Launcher in Settings")
                }
                Text(
                    "You need to change the default launcher in the system settings.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun LauncherRow(
    launcherInfo: LauncherAppInfo,
    isCurrentDefault: Boolean,
    onInfoClick: () -> Unit, // Callback to be invoked when the app info icon is clicked
    modifier: Modifier = Modifier, // Modifier to be applied to the entire Row
    onRowClick: () -> Unit // Callback to be invoked when the entire Row is clicked
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onRowClick) // Make the entire Row clickable
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ){
        // Display app icon
        launcherInfo.icon?.let {
            Image(
                bitmap = it.toBitmap(width = 120, height = 120).asImageBitmap(), // Adjust icon size appropriately
                contentDescription = "${launcherInfo.appName} icon",
                modifier = Modifier.size(48.dp) // Icon size in Composable
            )
        } ?: Box(modifier = Modifier.size(48.dp)) { // Empty space if no icon (or placeholder icon)
            // Example: Icon(Icons.Filled.BrokenImage, contentDescription = "No icon", modifier = Modifier.fillMaxSize())
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Display app name and package name
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = launcherInfo.appName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isCurrentDefault) FontWeight.Bold else FontWeight.Normal,
                color = if (isCurrentDefault) MaterialTheme.colorScheme.primary else LocalContentColor.current
            )
            Text(
                text = launcherInfo.packageName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant // Softer color
            )
        }

        // Display check icon if it's the current default launcher
        if (isCurrentDefault) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = "Current default launcher",
                tint = MaterialTheme.colorScheme.primary, // Use theme's primary color
                modifier = Modifier.padding(start = 8.dp) // Small padding on the left
            )
        }

        // App info icon button (always displayed)
        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Application Information for ${launcherInfo.appName}",
                tint = MaterialTheme.colorScheme.onSurfaceVariant // A less prominent color
            )
        }
    }
}

// Data class to hold launcher app information
private data class LauncherAppInfo(
    val appName: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable?
)

// Helper function: Get a list of installed launcher apps
private fun getAvailableLaunchers(context: Context, packageManager: PackageManager): List<LauncherAppInfo> {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    // Use PackageManager.queryIntentActivities() to get all activities that can handle the CATEGORY_HOME intent.
    val resolveInfoList: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // For Android 13 (API 33) and above, use ResolveInfoFlags.
        packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
    } else {
        // For older versions, pass the flags directly.
        @Suppress("DEPRECATION") // Handling for older versions
        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

    return resolveInfoList.mapNotNull { resolveInfo ->
        val activityInfo: ActivityInfo = resolveInfo.activityInfo
        // Extract app name, package name, activity name, and icon from each ResolveInfo to create a LauncherAppInfo object.
        LauncherAppInfo(
            appName = resolveInfo.loadLabel(packageManager).toString(),
            packageName = activityInfo.packageName,
            activityName = activityInfo.name, // Full class name of the activity
            icon = resolveInfo.loadIcon(packageManager)
        )
    }.distinctBy { it.packageName to it.activityName } // Remove duplicates in case of multiple HOME activities within the same package
}

// Helper function: Get the current default launcher
private fun getCurrentLauncher(context: Context, packageManager: PackageManager): LauncherAppInfo? {
    val intent = Intent(Intent.ACTION_MAIN).apply {
        addCategory(Intent.CATEGORY_HOME)
    }

    // Use PackageManager.resolveActivity() to get the currently set default HOME activity.
    // This function returns the best single activity that can handle the specified intent.
    val resolveInfo: ResolveInfo? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // For Android 13 (API 33) and above, use ResolveInfoFlags.
        packageManager.resolveActivity(intent, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
    } else {
        // For older versions, pass the flags directly.
        @Suppress("DEPRECATION") // Handling for older versions
        packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
    }

    // If resolveInfo is not null, convert its information into a LauncherAppInfo object and return it.
    return resolveInfo?.activityInfo?.let { activityInfo ->
        LauncherAppInfo(
            appName = resolveInfo.loadLabel(packageManager).toString(),
            packageName = activityInfo.packageName,
            activityName = activityInfo.name, // Full class name of the activity
            icon = resolveInfo.loadIcon(packageManager)
        )
    }
}