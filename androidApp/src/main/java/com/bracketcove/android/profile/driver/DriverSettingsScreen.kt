package com.bracketcove.android.profile.driver

import android.content.Intent
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bracketcove.android.R
import com.bracketcove.android.style.color_black
import com.bracketcove.android.style.color_primary
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography
import com.bracketcove.fakes.FakeUserService
import com.bracketcove.isValidPhoneNumber
import com.skydoves.landscapist.glide.GlideImage
import com.zhuinden.simplestack.Backstack

@Composable
fun DriverSettingsScreen(
    viewModel: DriverSettingsViewModel
) {

    Column(
        Modifier
            .fillMaxSize()
            .background(color = color_white),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                modifier = Modifier.clickable { viewModel.handleCancelPress() },
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.close_icon)
            )
        }

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            text = stringResource(id = R.string.driver_registration),
            style = typography.h3,
            textAlign = TextAlign.Center
        )

        AvatarAndSubtitle(
            viewModel = viewModel
        )

        VehicleDescription(
            viewModel = viewModel
        )

        ApplyButton(
            modifier = Modifier,
            handleSubmitButton = {
                viewModel.handleSubmitButton()
            }
        )
    }
}

@Composable
fun AvatarAndSubtitle(
    viewModel: DriverSettingsViewModel
) {

    val vehicleAvatarUrl by viewModel.vehiclePhotoUrl.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VehicleAvatar(vehicleAvatarUrl = vehicleAvatarUrl, viewModel = viewModel)
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.please_upload_an_image),
            style = typography.body1
        )
    }
}

@Composable
fun VehicleAvatar(
    vehicleAvatarUrl: String?,
    viewModel: DriverSettingsViewModel
) {
    Box(
        Modifier
            .background(color_black.copy(alpha = 0.16f), shape = RoundedCornerShape(4.dp))
            .size(88.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (vehicleAvatarUrl != null) {
            GlideImage(
                modifier = Modifier
                    .matchParentSize(),
                imageModel = { vehicleAvatarUrl }
            )

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult(),
                onResult = {

                    viewModel.handleThumbnailUpdate(it.data?.data)
                }
            )

            Icon(
                modifier = Modifier.clickable {
                    launcher.launch(
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    )
                },
                imageVector = ImageVector.vectorResource(id = R.drawable.check_circle_24px),
                contentDescription = stringResource(id = R.string.edit_avatar),
                tint = Color.Unspecified
            )
        }
    }
}

@Composable
fun VehicleDescription(
    viewModel: DriverSettingsViewModel
) {

    val user by viewModel.userModel.collectAsState()

    if (user != null) Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            text = stringResource(id = R.string.please_provide_a_description),
            style = typography.body2
        )

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp)
                .defaultMinSize(minHeight = 144.dp),
            value = user!!.vehicleDescription ?: "",
            onValueChange = {
                            viewModel.updateVehicleDescription(it)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            label = { Text(text = stringResource(id = R.string.description)) }
        )
    }
}

@Composable
fun ApplyButton(
    modifier: Modifier,
    handleSubmitButton: () -> Unit
) {
    Button(
        modifier = modifier.padding(16.dp),
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { handleSubmitButton() },
    ) {
        Text(
            text = stringResource(id = R.string.apply),
            style = typography.button
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewDriverSettingsScreen() {
    DriverSettingsScreen(viewModel = DriverSettingsViewModel(Backstack(), FakeUserService()))
}