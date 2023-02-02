package com.bracketcove.android.authentication.signup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
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
                imageVector = Icons.Filled.Close,
                contentDescription = stringResource(id = R.string.close_icon)
            )
        }

        UnterHeader(
            modifier = Modifier.padding(top = 64.dp),
            subtitleText = stringResource(id = R.string.sign_up_for_free)
        )

        val nameTextFieldValue by rememberSaveable {
            mutableStateOf("")
        }

        var nameValidationError by rememberSaveable {
            mutableStateOf(false)
        }

        UsernameInputField(
            modifier = Modifier.padding(top = 16.dp),
            textFieldValue = nameTextFieldValue,
            validationError = nameValidationError,
            updateIsError = { nameValidationError = it }
        )

        val mobileTextFieldValue by rememberSaveable {
            mutableStateOf("")
        }

        var mobileValidationError by rememberSaveable {
            mutableStateOf(false)
        }

        MobileInputField(
            modifier = Modifier.padding(top = 16.dp),
            textFieldValue = mobileTextFieldValue,
            validationError = mobileValidationError,
            updateIsError = { mobileValidationError = it }
        )

        SignUpContinueButton(
            modifier = Modifier.padding(top = 32.dp),
            viewModel = viewModel,
            textFieldValue = mobileTextFieldValue
        )
    }
}

@Composable
fun SignUpContinueButton(
    modifier: Modifier,
    viewModel: SignUpViewModel,
    textFieldValue: String
) {
    Button(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            backgroundColor = color_primary,
            contentColor = color_white
        ),
        onClick = { viewModel.handleSignUp(textFieldValue) },
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
    textFieldValue: String,
    validationError: Boolean,
    updateIsError: (Boolean) -> Unit
) {
    OutlinedTextField(
        modifier = modifier,
        value = textFieldValue,
        onValueChange = { newNumber ->
            if (isValidPhoneNumber(newNumber) || newNumber == "") updateIsError(false)
            else updateIsError(true)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        isError = validationError,
        label = { Text(text = stringResource(id = R.string.user_name)) }
    )
}

@Preview(showBackground = true, device = Devices.PIXEL_4_XL)
@Composable
fun PreviewSignUpScreen() {
    SignUpScreen(viewModel = SignUpViewModel())
}