package com.bracketcove.android.uicommon

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
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
import java.io.Serializable

/*
These functions are from:
https://github.com/Zhuinden/flow-combinetuple-kt/blob/master/src/main/java/com/zhuinden/flowcombinetuplekt/FlowCombineTuple.kt
 */

fun <T1, T2> combineTuple(f1: Flow<T1>, f2: Flow<T2>): Flow<Pair<T1, T2>> = combine(f1, f2) { t1, t2 -> Pair(t1, t2) }

fun <T1, T2, T3, T4> combineTuple(f1: Flow<T1>, f2: Flow<T2>, f3: Flow<T3>, f4: Flow<T4>): Flow<Tuple4<T1, T2, T3, T4>> = combine(f1, f2, f3, f4) { t1, t2, t3, t4 -> Tuple4<T1, T2, T3, T4>(t1, t2, t3, t4) }

fun <T1, T2, T3, T4, T5> combineTuple(f1: Flow<T1>, f2: Flow<T2>, f3: Flow<T3>, f4: Flow<T4>, f5: Flow<T5>): Flow<Tuple5<T1, T2, T3, T4, T5>> = combine(f1, f2, f3, f4, f5) { t1, t2, t3, t4, t5 -> Tuple5<T1, T2, T3, T4, T5>(t1, t2, t3, t4, t5) }

//This class is from: https://github.com/Zhuinden/tuples-kt/blob/master/src/main/java/com/zhuinden/tupleskt/Tuples.kt
data class Tuple4<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
) : Serializable {
    override fun toString(): String {
        return "Tuple4[$first, $second, $third, $fourth]"
    }
}

data class Tuple5<A, B, C, D, E>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D,
    val fifth: E
) : Serializable {
    override fun toString(): String {
        return "Tuple5[$first, $second, $third, $fourth, $fifth]"
    }
}

//This value is picked somewhat arbitrarily; it's just a unique identifier
internal const val LOCATION_PERMISSION = 1000
//How frequently do we want to request the location in milliseconds (10s here)
internal const val LOCATION_REQUEST_INTERVAL = 10000L

fun hideKeyboard(view: View, context: Context) {
    val inputMethodManager : InputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken,0)
}

fun Fragment.handleToast(code: ToastMessages) {
    val message = when (code) {
        ToastMessages.GENERIC_ERROR -> getString(R.string.generic_error)
        ToastMessages.SERVICE_ERROR -> getString(R.string.service_error)
        ToastMessages.PERMISSION_ERROR -> getString(R.string.permissions_required_to_use_this_app)
        ToastMessages.INVALID_CREDENTIALS -> getString(R.string.invalid_credentails)
        ToastMessages.ACCOUNT_EXISTS -> getString(R.string.an_account_already_exists)
        ToastMessages.UPDATE_SUCCESSFUL -> getString(R.string.update_successful)
        ToastMessages.UNABLE_TO_RETRIEVE_COORDINATES -> getString(R.string.unable_to_retrieve_coordinates_address)
    }
    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
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