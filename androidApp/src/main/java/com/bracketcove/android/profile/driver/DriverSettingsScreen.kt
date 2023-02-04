package com.bracketcove.android.profile.driver

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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

        val vehicleAvatarUrl by rememberSaveable {
            mutableStateOf<String?>(null)
        }

        AvatarAndSubtitle(
            viewModel = viewModel,
            vehicleAvatarUrl = vehicleAvatarUrl
        )

        var descriptionValidationError by rememberSaveable {
            mutableStateOf(false)
        }

        val textFieldValue by rememberSaveable {
            mutableStateOf("")
        }

        VehicleDescription(
            viewModel = viewModel,
            textFieldInitialValue = textFieldValue,
            validationError = descriptionValidationError,
            updateIsError = { descriptionValidationError = it }
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
    viewModel: DriverSettingsViewModel,
    vehicleAvatarUrl: String?
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        VehicleAvatar(avatarUrl = vehicleAvatarUrl, viewModel = viewModel)
        Text(
            modifier = Modifier.padding(16.dp),
            text = stringResource(id = R.string.please_upload_an_image),
            style = typography.body1
        )
    }
}

@Composable
fun VehicleAvatar(
    avatarUrl: String?,
    viewModel: DriverSettingsViewModel
) {
    Box(
        Modifier
            .background(color_black.copy(alpha = 0.16f), shape = RoundedCornerShape(4.dp))
            .size(88.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        if (avatarUrl != null) GlideImage(
            modifier = Modifier
                .matchParentSize(),
            imageModel = { avatarUrl }
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.check_circle_24px),
            contentDescription = stringResource(id = R.string.edit_avatar),
            tint = Color.Unspecified
        )
    }
}

@Composable
fun VehicleDescription(
    viewModel: DriverSettingsViewModel,
    textFieldInitialValue: String?,
    validationError: Boolean,
    updateIsError: (Boolean) -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            value = textFieldInitialValue ?: "",
            onValueChange = { newNumber ->
                if (isValidPhoneNumber(newNumber) || newNumber == "") updateIsError(false)
                else updateIsError(true)
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            isError = validationError,
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
