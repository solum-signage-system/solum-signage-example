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

import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.navigation.NavHostController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.solum.display.example.R
import com.solum.display.example.ui.theme.SolumSignageExampleTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DualScreenExample(navController: NavHostController) {
    // Get the current context and lifecycle owner from the composition
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Remember the DisplayManager service
    val displayManager = remember { context.getSystemService(DisplayManager::class.java) }
    // Remember the list of available displays
    val displays = remember { getDisplays(displayManager) }

    // State to hold the Presentation instance for the secondary display.
    // Using `var` and re-assigning inside DisposableEffect is one way,
    // though for more complex scenarios, you might use `remember { mutableStateOf<MyPresentation?>(null) }`.
    var presentationForSecondaryDisplay: MyPresentation? = null

    // Remember the resolution of the primary display
    val (width, height) = remember { getDisplayResolution(getPrimaryDisplay(displays)) }

    // DisposableEffect handles the lifecycle of the Presentation and its observers.
    // It runs when `lifecycleOwner` changes.
    DisposableEffect(lifecycleOwner) {
        // Check if there is more than one display available
        if (displays.size > 1) {
            // Create and configure the Presentation for the secondary display
            presentationForSecondaryDisplay = MyPresentation(
                lifecycleOwner, // Pass the lifecycle owner for Compose content within Presentation
                context,
                getSecondaryDisplay(displays) // Get the secondary display
            ).apply {
                // Set a listener for when the Presentation is dismissed
                setOnDismissListener {
                    // When the Presentation is dismissed (e.g., secondary display disconnected, or dismiss() called),
                    // navigate up (go back) on the primary screen's NavController.
                    navController.navigateUp()
                }
            }
        }
        // Show the presentation if it was created
        presentationForSecondaryDisplay?.show()

        // Create a LifecycleEventObserver to manage the Presentation's visibility
        // based on the primary screen's lifecycle.
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                // If the primary screen (and thus this Composable) is started,
                // ensure the presentation is shown.
                presentationForSecondaryDisplay?.show()
            } else if (event == Lifecycle.Event.ON_STOP) {
                // If the primary screen is stopped, dismiss the presentation.
                presentationForSecondaryDisplay?.dismiss()
            }
        }

        // Add the observer to the lifecycle of the current lifecycle owner (typically the Activity or Fragment)
        lifecycleOwner.lifecycle.addObserver(observer)

        // The onDispose block is executed when the DisposableEffect leaves the composition
        // (e.g., Composable is removed from the UI tree, or keys change forcing a restart).
        onDispose {
            // Remove the lifecycle observer to prevent leaks
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Dismiss the presentation to clean it up
            presentationForSecondaryDisplay?.dismiss()
            // It's also good practice to nullify the reference if it's a var outside remember
            // presentationForSecondaryDisplay = null
        }
    }

    // Main UI for the primary display
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.navigation_dual_screen)) },
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
        Column(modifier = Modifier.padding(innerPadding)) {
            if (displays.size > 1) {
                // If more than one display is available, show text for the primary display
                Text(
                    text = stringResource(R.string.text_primary_display),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                // If only one display is available, show a different message
                Text(
                    text = stringResource(R.string.text_only_1_display),
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            // Display the resolution of the primary display
            Text("Primary Display - Width: $width, Height: $height")
        }
    }
}

/**
 * Custom Presentation class to display Jetpack Compose content on a secondary display.
 *
 * @param lifecycleOwner The LifecycleOwner from the primary screen (e.g., an Activity or Fragment).
 *                       This is crucial for scoping the Compose content within the Presentation
 *                       and ensuring it behaves correctly with lifecycle events, ViewModel store,
 *                       and saved state.
 * @param activityContext The context of the hosting Activity. This is the "outerContext"
 *                        required by the Presentation class constructor.
 * @param display The Display object representing the secondary display where this Presentation
 *                will be shown.
 */
class MyPresentation(
    private val lifecycleOwner: LifecycleOwner, // Needs to be a LifecycleOwner for setViewTreeLifecycleOwner
    activityContext: Context,                   // The context of the Activity showing this Presentation
    display: Display                            // The target secondary display
) : Presentation(activityContext, display) {    // Pass `activityContext` as `outerContext` and the `display`

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) // Always call the superclass method first

        // Ensure the lifecycleOwner is also a SavedStateRegistryOwner for state restoration.
        // This is typically true if the lifecycleOwner is an Activity or a Fragment.
        val savedStateRegistryOwner = lifecycleOwner as SavedStateRegistryOwner

        // Create a ComposeView to host Jetpack Compose content within this Presentation.
        // `context` here refers to the Presentation's own context, which is derived from `activityContext`
        // but themed for the specific display.
        val composeView = ComposeView(context).apply {
            // Set the necessary ViewTree owners for Compose to integrate correctly with the
            // lifecycle, ViewModel store, and saved state registry of the passed `lifecycleOwner`.
            // This allows Composables inside this Presentation to use `viewModel()` and `rememberSaveable()`.
            setViewTreeLifecycleOwner(lifecycleOwner)
            setViewTreeViewModelStoreOwner(lifecycleOwner as? ViewModelStoreOwner) // Safe cast if lifecycleOwner might not be a ViewModelStoreOwner
            setViewTreeSavedStateRegistryOwner(savedStateRegistryOwner)

            // Define the strategy for disposing of the Composition when the View tree lifecycle is destroyed.
            // DisposeOnViewTreeLifecycleDestroyed is a common and safe choice.
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)

            // Set the Jetpack Compose content for the Presentation.
            setContent {
                // Apply your app's theme (e.g., SolumSignageExampleTheme) to the content
                // shown on the secondary display. This ensures consistent styling.
                SolumSignageExampleTheme {
                    PresentationContent() // Call the Composable function that defines the UI for this Presentation
                }
            }
        }
        // Set the created ComposeView as the content view for this Presentation dialog.
        setContentView(composeView)
    }

    /**
     * Composable function defining the UI content for the Presentation.
     * This is where you build the UI that will be displayed on the secondary screen.
     */
    @OptIn(ExperimentalMaterial3Api::class) // Opt-in for Material 3 experimental APIs if used
    @Composable
    private fun PresentationContent() {
        // Remember the display resolution (width and height) of the current display
        // (which is the secondary display this Presentation is shown on).
        // `display` is an inherited property from the `Presentation` class.
        val (width, height) = remember { getDisplayResolution(display) }

        // Use Scaffold for a standard Material Design layout structure.
        Scaffold(
            topBar = {
                // Define a TopAppBar for the Presentation screen.
                TopAppBar(
                    title = {
                        // Set the title of the TopAppBar using a string resource.
                        Text(stringResource(R.string.navigation_dual_screen))
                    },
                    // Apply custom colors to the TopAppBar.
                    // These colors are taken from the MaterialTheme.colorScheme,
                    // ensuring they adapt to the current theme (light/dark).
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary,
                        navigationIconContentColor = MaterialTheme.colorScheme.onPrimary, // If you had a nav icon
                        actionIconContentColor = MaterialTheme.colorScheme.onPrimary    // If you had action icons
                    )
                )
            },
            modifier = Modifier.fillMaxSize() // Make the Scaffold fill the entire Presentation screen.
        ) { innerPadding -> // `innerPadding` is provided by Scaffold to avoid content overlapping with TopAppBar.
            // Column to arrange content vertically.
            Column(
                modifier = Modifier
                    .padding(innerPadding) // Apply padding to respect the TopAppBar area.
                    .fillMaxSize()         // Fill the remaining space.
            ) {
                // Display a text indicating this is the secondary display.
                Text(
                    text = stringResource(R.string.text_secondary_display),
                    style = MaterialTheme.typography.displayLarge, // Use a large display style from the theme.
                    color = MaterialTheme.colorScheme.onSurfaceVariant // Use an appropriate text color from the theme.
                )
                // Display the width and height of the secondary display.
                Text("Secondary Display - Width: $width, Height: $height")
            }
        }
    }
}

/**
 * Helper function to get all available displays from the DisplayManager.
 * @param displayManager The system's DisplayManager service.
 * @return An array of Display objects.
 */
private fun getDisplays(displayManager: DisplayManager): Array<Display> =
    displayManager.displays

/**
 * Helper function to find the primary display from an array of displays.
 * The primary display usually has a displayId of Display.DEFAULT_DISPLAY (0).
 * @param displays An array of Display objects.
 * @return The primary Display object.
 * @throws NoSuchElementException if no display has Display.DEFAULT_DISPLAY ID.
 */
private fun getPrimaryDisplay(displays: Array<Display>): Display =
    displays.first { it.displayId == Display.DEFAULT_DISPLAY }

/**
 * Helper function to find the first available secondary display.
 * This assumes any display that is not the default display is a secondary display.
 * For more robust multi-display handling, you might need more sophisticated logic
 * to select a specific secondary display if multiple are present.
 * @param displays An array of Display objects.
 * @return The first secondary Display object found.
 * @throws NoSuchElementException if no secondary display is found.
 */
private fun getSecondaryDisplay(displays: Array<Display>): Display =
    displays.first { it.displayId != Display.DEFAULT_DISPLAY }

/**
 * Helper function to get the physical width and height of a given Display.
 * It accesses the display's current mode to get the resolution.
 * @param display The Display object.
 * @return A Pair containing the physical width and height in pixels.
 */
private fun getDisplayResolution(display: Display): Pair<Int, Int> =
    display.mode.let { mode -> // `mode` gives access to display characteristics like resolution
        Pair(mode.physicalWidth, mode.physicalHeight)
    }