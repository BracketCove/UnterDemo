package com.bracketcove.android.profile.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DriveEta
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.bracketcove.android.R
import com.bracketcove.android.style.color_black
import com.bracketcove.android.style.color_primary
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography

@Composable
fun ProfileSettingsScreen(
    viewModel: ProfileSettingsViewModel,
    unregisteredUserView: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = color_white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        ProfileToolbar(viewModel = viewModel)

        var showDriverSwitch by rememberSaveable {
            mutableStateOf(false)
        }

        var driverSwitchState by rememberSaveable {
            mutableStateOf(false)
        }


        ProfileHeader(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            viewModel = viewModel,
            showDriverSwitch = showDriverSwitch,
            driverSwitchState = driverSwitchState,
            checkedChanged = { driverSwitchState = it }
        )

        if (unregisteredUserView) DriverRegistryPrompt(viewModel = viewModel)
        else DriverInfo(
            modifier = Modifier
                .wrapContentHeight()
                .fillMaxWidth(),
            viewModel = viewModel
        )


    }
}

@Composable
fun ProfileToolbar(
    modifier: Modifier = Modifier,
    viewModel: ProfileSettingsViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.KeyboardArrowLeft,
            contentDescription = stringResource(id = R.string.close_icon)
        )

        TextButton(
            onClick = { viewModel.handleLogOut() }
        ) {
            Text(
                text = stringResource(id = R.string.log_out),
                style = typography.button.copy(
                    color = color_black,
                    fontWeight = FontWeight.Light
                )
            )
        }
    }
}

@Composable
fun ProfileHeader(
    modifier: Modifier,
    viewModel: ProfileSettingsViewModel,
    showDriverSwitch: Boolean,
    driverSwitchState: Boolean,
    checkedChanged: (Boolean) -> Unit
) {
    Row(modifier = modifier) {
        ProfileAvatar(modifier = Modifier, viewModel = viewModel)
        NameAndDriverState(
            modifier = Modifier,
            viewModel = viewModel,
            showDriverSwitch = showDriverSwitch,
            driverSwitchState = driverSwitchState,
            checkedChanged = checkedChanged
        )
    }
}

@Composable
fun NameAndDriverState(
    modifier: Modifier,
    viewModel: ProfileSettingsViewModel,
    showDriverSwitch: Boolean,
    driverSwitchState: Boolean,
    checkedChanged: (Boolean) -> Unit
) {

    BoxWithConstraints {
        ConstraintLayout {
            val (name, userType, switch) = createRefs()

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(name) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(start = 16.dp),
                text = "Username",
                style = typography.h3
            )

            Switch(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top)
                    .constrainAs(switch) {
                        top.linkTo(userType.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(userType.bottom)
                    }
                    .padding(start = 16.dp),
                checked = driverSwitchState,
                onCheckedChange = { checkedChanged(it) }
            )

            Text(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top)
                    .constrainAs(userType) {
                        top.linkTo(name.bottom)
                        start.linkTo(switch.end)
                    },
                text = stringResource(id = R.string.passenger),
                style = typography.subtitle2
            )


        }
    }
}

@Composable
fun ProfileAvatar(
    modifier: Modifier,
    viewModel: ProfileSettingsViewModel
) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        Image(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape),
            imageVector = ImageVector.vectorResource(id = R.drawable.baseline_account_circle_24),
            contentDescription = stringResource(id = R.string.user_avatar),
            contentScale = ContentScale.Crop,
        )

        Icon(
            imageVector = ImageVector.vectorResource(id = R.drawable.check_circle_24px),
            contentDescription = stringResource(id = R.string.edit_avatar),
            tint = Color.Unspecified
        )
    }
}


@Composable
fun DriverInfo(
    modifier: Modifier,
    viewModel: ProfileSettingsViewModel,
) {

    BoxWithConstraints(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 16.dp, end = 16.dp, top = 32.dp)

    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 1.dp,
                    color = color_black.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(16.dp)

        ) {
            val (avatar, title, editButton, description) = createRefs()

            Image(
                modifier = Modifier
                    .constrainAs(avatar) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        bottom.linkTo(parent.bottom)
                    }
                    .size(88.dp),
                painter = painterResource(id = R.drawable.donkey),
                contentDescription = stringResource(id = R.string.vehicle_avatar)
            )

            Text(
                modifier = Modifier
                    .wrapContentHeight()
                    .constrainAs(title) {
                        top.linkTo(editButton.top)
                        bottom.linkTo(editButton.bottom)
                        start.linkTo(avatar.end)
                        end.linkTo(editButton.start)
                        width = Dimension.fillToConstraints
                    }
                    .padding(start = 16.dp),
                text = stringResource(id = R.string.vehicle_description),
                style = typography.subtitle2,
                textAlign = TextAlign.Start
            )

            Text(
                modifier = Modifier
                    .wrapContentHeight(align = Alignment.Top)
                    .constrainAs(description) {
                        top.linkTo(title.bottom)
                        start.linkTo(avatar.end)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints

                    }
                    .padding(start = 16.dp),
                text = stringResource(id = R.string.lorem_ipsum),
                style = typography.button.copy(color = color_black, fontSize = 14.sp),
                maxLines = 3
            )

            TextButton(
                modifier = Modifier.constrainAs(editButton) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
                onClick = { viewModel.handleDriverDetailEdit() }) {
                Text(
                    text = stringResource(id = R.string.edit),
                    style = typography.button.copy(color = color_primary)
                )
            }

        }
    }
}

@Composable
fun DriverRegistryPrompt(
    viewModel: ProfileSettingsViewModel
) {
    Column(
        Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.register_to_become_driver),
            style = typography.h3,
            textAlign = TextAlign.Center
        )

        Button(
            modifier = Modifier.padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color_primary,
                contentColor = color_white
            ),
            onClick = { viewModel.handleDriverDetailEdit() },
        ) {
            Icon(
                imageVector = Icons.Filled.DriveEta,
                contentDescription = stringResource(id = R.string.register)
            )

            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(id = R.string.register),
                style = typography.button
            )
        }
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewProfileSettingsScreen() {
    ProfileSettingsScreen(viewModel = ProfileSettingsViewModel(), unregisteredUserView = true)
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewProfileSettingsScreenRegistered() {
    ProfileSettingsScreen(viewModel = ProfileSettingsViewModel(), unregisteredUserView = false)
}