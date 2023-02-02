package com.bracketcove.android.authentication.login

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.bracketcove.android.uicommon.MobileInputField
import com.bracketcove.android.uicommon.UnterHeader
import com.bracketcove.isValidPhoneNumber

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

        val textFieldValue by rememberSaveable {
            mutableStateOf("")
        }

        var validationError by rememberSaveable {
            mutableStateOf(false)
        }

        MobileInputField(
            modifier = Modifier.padding(top = 16.dp),
            textFieldValue = textFieldValue,
            validationError = validationError,
            updateIsError = { validationError = it }
        )

        LoginContinueButton(
            modifier = Modifier.padding(top = 32.dp),
            viewModel = viewModel,
            textFieldValue = textFieldValue
        )

        SignupText(
            modifier = Modifier.padding(top = 32.dp),
            viewModel = viewModel
        )
    }
}

@Composable
fun LoginContinueButton(
    modifier: Modifier,
    viewModel: LoginViewModel,
    textFieldValue: String
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { viewModel.handleLogin(textFieldValue) },
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
    LoginScreen(viewModel = LoginViewModel())
}