package com.bracketcove.android.authentication.login

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import com.bracketcove.android.R
import com.bracketcove.android.style.color_primary
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography
import com.bracketcove.isValidPhoneNumber

@Composable
fun LoginScreen(
    viewModel: LoginViewModel
) {

}



@Composable
fun MobileInputField(
    modifier: Modifier,
    textFieldValue: String,
    validationError: Boolean,
    updateIsError: (Boolean) -> Unit
) {
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = { newNumber ->
            if (isValidPhoneNumber(newNumber) || newNumber == "") updateIsError(false)
            else updateIsError(true)
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        isError = validationError,
        label = { Text(text = stringResource(id = R.string.mobile_number)) }
    )
}

@Composable
fun ContinueButton(
    viewModel: LoginViewModel,
    textFieldValue: String
) {
    Button(
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