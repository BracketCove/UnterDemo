package com.bracketcove

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform