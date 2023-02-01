package com.bracketcove

fun isValidPhoneNumber(phoneNumber: String): Boolean {
    val pattern = Regex("""^\+(?:[0-9] ?){10,14}[0-9]$""")
    return pattern.matches(phoneNumber)
}
