package com.sc.dtracker.features.map.ui

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.sc.dtracker.R
import com.sc.dtracker.ui.theme.AlertButtonActionArea
import com.sc.dtracker.ui.theme.AlertButtonActionText

@Composable
fun NoMapPermissionView(
    onDismiss: () -> Unit
) {
    val ctx = LocalContext.current

    AlertDialog(
        icon = {
            Icon(Icons.Default.Place, contentDescription = "Example Icon")
        },
        title = {
            Text(text = stringResource(R.string.no_location_permission_title))
        },
        text = {
            Text(text = stringResource(R.string.no_location_permission_text))
        },
        onDismissRequest = {
            onDismiss()
        },
        confirmButton = {
            Button(
                colors = ButtonColors(
                    containerColor = AlertButtonActionArea,
                    contentColor = AlertButtonActionText,
                    disabledContainerColor = AlertButtonActionArea,
                    disabledContentColor = AlertButtonActionText,
                ),
                onClick = {
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", ctx.packageName, null)
                    )
                    ctx.startActivity(intent)
                    onDismiss()
                }
            ) {
                Text(text = stringResource(R.string.no_location_permission_go_settings))
            }
        },
    )
}
