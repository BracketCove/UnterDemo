package com.bracketcove.android.uicommon

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.fragment.app.Fragment
import com.bracketcove.android.R
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography
import com.bracketcove.isValidPhoneNumber


fun Fragment.handleToast(code: ToastMessages) {
    val message = when (code) {
        ToastMessages.GENERIC_ERROR -> getString(R.string.generic_error)
        ToastMessages.SERVICE_ERROR -> getString(R.string.service_error)
        ToastMessages.PERMISSION_ERROR -> getString(R.string.permissions_required_to_use_this_app)
        ToastMessages.INVALID_CREDENTIALS -> getString(R.string.invalid_credentails)
        ToastMessages.ACCOUNT_EXISTS -> getString(R.string.an_account_already_exists)
    }
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
}

@Composable
fun MobileInputField(
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
        label = { Text(text = stringResource(id = R.string.mobile_number)) }
    )
}
@Composable
fun UnterHeader(
    modifier: Modifier = Modifier,
    subtitleText: String = "Sign up for free"
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.unter),
            style = typography.h1
        )
        Text(
            text = subtitleText,
            style = typography.subtitle2
        )
    }
}

@Preview
@Composable
fun previewHeader() {
    Box(Modifier.background(color = color_white)) {
        UnterHeader(
            Modifier,
            "Sign up for free"
        )
    }
}