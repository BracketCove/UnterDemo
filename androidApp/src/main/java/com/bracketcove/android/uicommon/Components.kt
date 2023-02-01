package com.bracketcove.android.uicommon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.bracketcove.android.R
import com.bracketcove.android.style.color_white
import com.bracketcove.android.style.typography

@Composable
fun UnterHeader(subtitleText: String = "Sign up for free") {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
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
            "Sign up for free"
        )
    }

}