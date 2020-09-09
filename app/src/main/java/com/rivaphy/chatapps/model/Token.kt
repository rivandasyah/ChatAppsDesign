package com.rivaphy.chatapps.model

class Token {
    private var token : String = ""

    constructor()
    constructor(Token: String) {
        this.token = Token
    }

    fun getToken(): String {
        return token
    }

    fun setToken (token : String?) {
        this.token = token!!
    }
}