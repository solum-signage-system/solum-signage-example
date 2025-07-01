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

package com.solum.display.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.solum.display.example.example.ChangeDisplayAttributeExample
import com.solum.display.example.example.ChangeHomeExample
import com.solum.display.example.example.ChangeLanguageExample
import com.solum.display.example.example.ChangeTimeZoneExample
import com.solum.display.example.example.ChangeVolumeExample
import com.solum.display.example.example.DualScreenExample
import com.solum.display.example.example.FindAudioOutputDeviceExample
import com.solum.display.example.example.InstallApplicationExample
import com.solum.display.example.example.MuteExample
import com.solum.display.example.example.RotateScreenExample
import com.solum.display.example.example.SimulateKeyboardInputExample
import com.solum.display.example.ui.theme.SolumSignageExampleTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SolumSignageExampleTheme {
                DeviceControlApp()
            }
        }
    }
}

sealed class ScreenIndicator(val route: String, @StringRes val title: Int) {
    data object Home : ScreenIndicator("home", R.string.navigation_home)
    data object FindAudioOutputDevice : ScreenIndicator("find_audio_output", R.string.navigation_find_audio_output_device)
    data object ChangeVolume : ScreenIndicator("change_volume", R.string.navigation_change_volume)
    data object Mute : ScreenIndicator("mute", R.string.navigation_mute)
    data object DualScreen : ScreenIndicator("dual_screen", R.string.navigation_dual_screen)
    data object ChangeDisplayAttribute : ScreenIndicator("change_display_attribute", R.string.navigation_change_display_attribute)
    data object RotateScreen : ScreenIndicator("rotate_screen", R.string.navigation_rotate_screen)
    data object ChangeLanguage : ScreenIndicator("change_language", R.string.navigation_change_language)
    data object ChangeTimeZone : ScreenIndicator("change_timezone", R.string.navigation_change_timezone)
    data object InstallApplication : ScreenIndicator("install_application", R.string.navigation_install_application)
    data object ChangeHomeApplication : ScreenIndicator("change_home", R.string.navigation_change_home_application)
    data object ClearApplicationData : ScreenIndicator("clear_app", R.string.navigation_clear_app_data)
    data object AllowAppMode : ScreenIndicator("allow_appmode", R.string.navigation_allow_appmode)
    data object AllowAndroidPermission : ScreenIndicator("allow_permissions", R.string.navigation_allow_permissions)
    data object GetDeviceInformation : ScreenIndicator("get_device_information", R.string.navigation_get_device_information)
    data object EnableRemoteControl : ScreenIndicator("enable_remote", R.string.navigation_enable_remote_control)
    data object EnableTouchInput : ScreenIndicator("enable_touch", R.string.navigation_enable_touch_input)
    data object TakeScreenShot : ScreenIndicator("take_screenshot", R.string.navigation_take_screenshot)
    data object CaptureLogcatLog : ScreenIndicator("capture_log", R.string.navigation_capture_logcat_log)
    data object SimulateMouseInput : ScreenIndicator("simulate_mouse", R.string.navigation_simulate_mouse_input)
    data object SimulateKeyboardInput : ScreenIndicator("simulate_keyboard", R.string.navigation_simulate_keyboard_input)

    companion object {
        val allScreens = listOf(
            FindAudioOutputDevice,
            ChangeVolume,
            Mute,
            DualScreen,
            ChangeDisplayAttribute,
            RotateScreen,
            ChangeLanguage,
            ChangeTimeZone,
//            InstallApplication,
//            ChangeHomeApplication,
//            ClearApplicationData,
//            AllowAppMode,
//            AllowAndroidPermission,
//            GetDeviceInformation,
//            EnableRemoteControl,
//            EnableTouchInput,
//            TakeScreenShot,
//            CaptureLogcatLog,
//            SimulateMouseInput,
            SimulateKeyboardInput
        )
    }
}

@Composable
fun DeviceControlApp() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = ScreenIndicator.Home.route) {
        composable(ScreenIndicator.Home.route) { HomeScreen(navController) }
        composable(ScreenIndicator.FindAudioOutputDevice.route) { FindAudioOutputDeviceExample(navController) }
        composable(ScreenIndicator.ChangeVolume.route) { ChangeVolumeExample(navController) }
        composable(ScreenIndicator.Mute.route) { MuteExample(navController) }
        composable(ScreenIndicator.DualScreen.route) {DualScreenExample(navController)}
        composable(ScreenIndicator.ChangeDisplayAttribute.route) { ChangeDisplayAttributeExample(navController) }
        composable(ScreenIndicator.RotateScreen.route) { RotateScreenExample(navController) }
        composable(ScreenIndicator.ChangeLanguage.route) { ChangeLanguageExample(navController) }
        composable(ScreenIndicator.ChangeTimeZone.route) { ChangeTimeZoneExample(navController) }
        composable(ScreenIndicator.InstallApplication.route) { InstallApplicationExample(navController) }
        composable(ScreenIndicator.ChangeHomeApplication.route) { ChangeHomeExample(navController) }
        composable(ScreenIndicator.ClearApplicationData.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.AllowAppMode.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.AllowAndroidPermission.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.GetDeviceInformation.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.EnableRemoteControl.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.EnableTouchInput.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.TakeScreenShot.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.CaptureLogcatLog.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.SimulateMouseInput.route) { PlaceHolderScreen(navController) }
        composable(ScreenIndicator.SimulateKeyboardInput.route) { SimulateKeyboardInputExample(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    Scaffold(topBar = { TopAppBar(title = {
            Text("Device Control Examples")
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primary,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary,
            actionIconContentColor = MaterialTheme.colorScheme.onPrimary
        )) },
        modifier = Modifier.fillMaxSize()) { innerPadding ->

        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(items = ScreenIndicator.allScreens, key = { it.route }) { indicator ->
                ListItem(
                    headlineContent = { Text(stringResource(indicator.title)) },
                    trailingContent = {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate(indicator.route) }
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun PlaceHolderScreen(navController: NavHostController) {
    Scaffold(modifier = Modifier.fillMaxSize()) { _ ->

    }
}