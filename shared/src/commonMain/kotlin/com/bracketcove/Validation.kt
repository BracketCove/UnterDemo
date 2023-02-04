package com.bracketcove

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val pattern = Regex("""^\+(?:\d ?){10,14}\d$""")
    return pattern.matches(phoneNumber)
}
