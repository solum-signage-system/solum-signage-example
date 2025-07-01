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

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.solum.display.example.R
import com.solum.display.manager.Package
import com.solum.display.manager.Package.InstallListener
import java.io.BufferedReader
import java.io.InputStreamReader

@OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs
@Composable
fun InstallApplicationExample(navController: NavHostController) {
    // Get the current context from the Composable's local environment.
    val context = LocalContext.current
    var selectedFileUri by remember { mutableStateOf<Uri?>(null) }
    var installStatus by remember { mutableStateOf<UiInstallStatus>(UiInstallStatus.Idle) }


    // 1. Prepare ActivityResultLauncher
    // ActivityResultContracts.OpenDocument() is used to select a single document.
    // To select multiple files, use ActivityResultContracts.OpenMultipleDocuments().
    // You can filter file types by specifying a specific MIME type.
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(), // or ActivityResultContracts.GetContent()
        onResult = { uri: Uri? ->
            // 3. Process the selected file
            selectedFileUri = uri
            uri?.let {
                // Caution: it.path might not return an actual file system path for SAF Uris.
                // For SAF Uris, `Package.install` might need to handle the Uri directly, or
                // you might need to obtain an InputStream via ContentResolver, copy it to a
                // temporary file, and then pass the temporary file's path.
                // In this example, we assume `it.path` can be used directly.
                val path = it.path // Note: This path might not be an actual file system path.
                // The `Package.install` function should handle this appropriately.

                if (path == null) {
                    installStatus = UiInstallStatus.Error(null, "Could not get file path from URI.")
                    return@let
                }

                // Initialize status before starting installation
                installStatus = UiInstallStatus.Idle

                Package.install(context, path, object : Package.InstallListener {
                    override fun onStarted(packageName: String?) {
                        installStatus = UiInstallStatus.Started(packageName)
                    }

                    override fun onWriteProgress(
                        packageName: String?,
                        bytesWritten: Long,
                        totalBytes: Long
                    ) {
                        val progressValue =
                            if (totalBytes > 0) bytesWritten.toFloat() / totalBytes.toFloat() else 0f
                        installStatus = UiInstallStatus.Writing(
                            packageName,
                            progressValue,
                            bytesWritten,
                            totalBytes
                        )
                    }

                    override fun onCommitStart(packageName: String?) {
                        installStatus = UiInstallStatus.Committing(packageName)
                    }

                    override fun onFinished(packageName: String?) {
                        installStatus = UiInstallStatus.Finished(packageName)
                        selectedFileUri = null // Clear URI after successful installation
                    }

                    override fun onError(packageName: String?, errorMessage: String?) {
                        installStatus = UiInstallStatus.Error(packageName, errorMessage)
                    }
                })
            }
        }
    )

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(onClick = {
                // 2. Launch file selection intent
                // You can specify MIME types to display only certain types of files.
                // E.g., "image/*", "application/pdf", "text/plain"
                // To specify multiple MIME types, use an array like arrayOf("image/*", "video/*").
                filePickerLauncher.launch(arrayOf("application/vnd.android.package-archive")) // Use "*/*" to see all file types
            }) {
                Text("Select APK and Install")
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Display UI based on installation status
            when (val status = installStatus) {
                is UiInstallStatus.Idle -> {
                    selectedFileUri?.let {
                        Text("Selected file: ${it.lastPathSegment ?: it.toString()}")
                        Text("Ready to install.")
                    } ?: Text("Please select an APK file to install.")
                }

                is UiInstallStatus.Started -> {
                    Text(
                        "Installation started for: ${status.packageName ?: "Unknown Package"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Preparing installation...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                is UiInstallStatus.Writing -> {
                    Text(
                        "Installing: ${status.packageName ?: "Unknown Package"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { status.progress },
                        modifier = Modifier
                            .fillMaxWidth(0.8f) // Take 80% of width for better aesthetics
                            .height(10.dp) // Slightly thicker progress bar
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${(status.progress * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        // Helper function to format bytes, assuming it's defined elsewhere
                        // e.g., fun formatBytes(bytes: Long): String { ... }
                        text = "${status.bytesWritten} / ${status.totalBytes}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                is UiInstallStatus.Committing -> {
                    Text(
                        "Finalizing installation for: ${status.packageName ?: "Unknown Package"}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Please wait, committing changes...",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
                is UiInstallStatus.Finished -> {
                    Text(
                        "Installation Successful!",
                        style = MaterialTheme.typography.titleLarge, // Larger text for success
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32) // A pleasant green color
                    )
                    status.packageName?.let {
                        Text(
                            "Package: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    // Optionally, add an icon for success
                    // Icon(Icons.Filled.CheckCircle, contentDescription = "Success", tint = Color(0xFF2E7D32), modifier = Modifier.size(48.dp).padding(top = 8.dp))
                }
                is UiInstallStatus.Error -> {
                    Text(
                        "Installation Failed!",
                        style = MaterialTheme.typography.titleLarge, // Larger text for error
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error // Use theme's error color
                    )
                    status.packageName?.let {
                        Text(
                            "Package: $it",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                    Text(
                        "Error: ${status.message ?: "An unknown error occurred."}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error, // Use theme's error color
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    // Optionally, add an icon for error
                    // Icon(Icons.Filled.Error, contentDescription = "Error", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(48.dp).padding(top = 8.dp))
                }
            }
        }
    }
}

// Sealed Class representing the installation status
sealed class UiInstallStatus {
    object Idle : UiInstallStatus()
    data class Started(val packageName: String?) : UiInstallStatus()
    data class Writing(val packageName: String?, val progress: Float, val bytesWritten: Long, val totalBytes: Long) : UiInstallStatus()
    data class Committing(val packageName: String?) : UiInstallStatus()
    data class Finished(val packageName: String?) : UiInstallStatus()
    data class Error(val packageName: String?, val message: String?) : UiInstallStatus()
}