package com.bracketcove

import com.bracketcove.domain.User

interface IFakeRepository {
    fun getUser(): User?
}