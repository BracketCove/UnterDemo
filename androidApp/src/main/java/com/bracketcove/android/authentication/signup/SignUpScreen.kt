package com.bracketcove.android.authentication.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bracketcove.android.R
import com.bracketcove.android.style.color_primary
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography
import com.bracketcove.android.uicommon.UnterHeader
import com.bracketcove.usecase.SignUpUser
import com.bracketcove.fakes.FakeAuthService
import com.bracketcove.fakes.FakeUserService
import com.zhuinden.simplestack.Backstack

@Composable
fun SignUpScreen(
    viewModel: SignUpViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = color_white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            contentAlignment = Alignment.TopStart
        ) {
            Icon(
                modifier = Modifier.clickable { viewModel.handleBackPress() },
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.close_icon),
            )
        }

        UnterHeader(
            modifier = Modifier.padding(top = 64.dp),
            subtitleText = stringResource(id = R.string.sign_up_for_free)
        )

        UsernameInputField(
            modifier = Modifier.padding(top = 16.dp),
            viewModel = viewModel
        )

        PhoneInputField(
            modifier = Modifier.padding(top = 16.dp),
            viewModel = viewModel
        )

        SignUpContinueButton(
            modifier = Modifier.padding(top = 32.dp),
            viewModel = viewModel
        )
    }
}

@Composable
fun SignUpContinueButton(
    modifier: Modifier,
    viewModel: SignUpViewModel
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { viewModel.handleSignUp() },
    ) {
        Text(
            text = stringResource(id = R.string.string_continue),
            style = typography.button
        )
    }
}

@Composable
fun UsernameInputField(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel
) {
    OutlinedTextField(
        modifier = modifier,
        value = viewModel.name,
        onValueChange = {
            viewModel.updateName(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = { Text(text = stringResource(id = R.string.user_name)) }
    )
}

@Composable
fun PhoneInputField(
    modifier: Modifier = Modifier,
    viewModel: SignUpViewModel
) {
    OutlinedTextField(
        modifier = modifier,
        value = viewModel.mobileNumber,
        onValueChange = {
            viewModel.updateMobileNumber(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = { Text(text = stringResource(id = R.string.password)) }
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(
        viewModel = SignUpViewModel(
            Backstack(),
            SignUpUser(FakeAuthService(), FakeUserService()
            )
        )
    )
}