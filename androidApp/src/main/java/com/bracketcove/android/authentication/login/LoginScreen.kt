package com.bracketcove.android.authentication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bracketcove.android.R
import com.bracketcove.android.style.color_primary
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography
import com.bracketcove.android.uicommon.UnterHeader
import com.bracketcove.fakes.FakeAuthService
import com.bracketcove.fakes.FakeUserService
import com.zhuinden.simplestack.Backstack

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = color_white),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        UnterHeader(
            modifier = Modifier.padding(top = 64.dp),
            subtitleText = stringResource(id = R.string.need_a_ride)
        )

        EmailInputField(
            modifier = Modifier.padding(top = 16.dp),
            viewModel = viewModel
        )

        PasswordInputField(
            modifier = Modifier.padding(top = 16.dp),
            viewModel = viewModel
        )

        LoginContinueButton(
            modifier = Modifier.padding(top = 32.dp),
            handleLogin = { viewModel.handleLogin() }
        )

        SignupText(
            modifier = Modifier.padding(top = 32.dp),
            viewModel = viewModel
        )
    }
}

@Composable
fun EmailInputField(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel
) {

    OutlinedTextField(
        modifier = modifier,
        value = viewModel.email,
        onValueChange = {
            viewModel.updateEmail(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        label = { Text(text = stringResource(id = R.string.email)) }
    )
}

@Composable
fun PasswordInputField(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel
) {

    OutlinedTextField(
        modifier = modifier,
        value = viewModel.password,
        onValueChange = {
            viewModel.updatePassword(it)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        label = { Text(text = stringResource(id = R.string.password)) }
    )
}

@Composable
fun LoginContinueButton(
    modifier: Modifier,
    handleLogin: () -> Unit
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { handleLogin() },
    ) {
        Text(
            text = stringResource(id = R.string.string_continue),
            style = typography.button
        )
    }
}

@Composable
fun SignupText(
    modifier: Modifier,
    viewModel: LoginViewModel
) {
    TextButton(
        modifier = modifier,
        onClick = { viewModel.goToSignup() }) {
        Text(
            style = typography.subtitle2,
            text = buildAnnotatedString {
                append(stringResource(id = R.string.no_account))
                append(" ")
                withStyle(
                    SpanStyle(
                        color = color_primary,
                        textDecoration = TextDecoration.Underline
                    )
                ) {
                    append(stringResource(id = R.string.sign_up))
                }
                append(" ")
                append(stringResource(id = R.string.here))
            }
        )
    }
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewLoginScreen() {
    LoginScreen(viewModel = LoginViewModel(Backstack(), FakeUserService(), FakeAuthService()))
}