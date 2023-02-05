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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/*
This function is from:
https://github.com/Zhuinden/flow-combinetuple-kt/blob/master/src/main/java/com/zhuinden/flowcombinetuplekt/FlowCombineTuple.kt
 */
fun <T1, T2, T3> combineTuple(f1: Flow<T1>, f2: Flow<T2>, f3: Flow<T3>): Flow<Triple<T1, T2, T3>> = combine(f1, f2, f3) { t1, t2, t3 -> Triple<T1, T2, T3>(t1, t2, t3) }

//This value is picked somewhat arbitrarily; it's just a unique identifier
internal const val LOCATION_PERMISSION = 1000
//How frequently do we want to request the location in milliseconds (10s here)
internal const val LOCATION_REQUEST_INTERVAL = 10000L

fun Fragment.handleToast(code: ToastMessages) {
    val message = when (code) {
        ToastMessages.GENERIC_ERROR -> getString(R.string.generic_error)
        ToastMessages.SERVICE_ERROR -> getString(R.string.service_error)
        ToastMessages.PERMISSION_ERROR -> getString(R.string.permissions_required_to_use_this_app)
        ToastMessages.INVALID_CREDENTIALS -> getString(R.string.invalid_credentails)
        ToastMessages.ACCOUNT_EXISTS -> getString(R.string.an_account_already_exists)
        ToastMessages.UPDATE_SUCCESSFUL -> getString(R.string.update_successful)
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