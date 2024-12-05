/**
 * MIT License
 * Copyright (c) 2022 Jemshit Iskenderov
 */
package com.sdk.mysdklibrary.impl

import android.annotation.SuppressLint
import walletconnect.core.util.Logger
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SetTextI18n")
class TextViewLogger() : Logger {

    private val dateFormatter = SimpleDateFormat("mm:ss.SSS", Locale.US)

    override fun debug(tag: String, parameters: String?) {
        val time = dateFormatter.format(Date(System.currentTimeMillis()))
        println("$time: Debug | $tag | $parameters\n")
    }

    override fun info(tag: String, parameters: String?) {
        val time = dateFormatter.format(Date(System.currentTimeMillis()))
        println("$time: Info  | $tag | $parameters\n")
    }

    override fun warning(tag: String, parameters: String?) {
        val time = dateFormatter.format(Date(System.currentTimeMillis()))
        println("$time: WARN  | $tag | $parameters\n")
    }

    override fun error(tag: String, parameters: String?) {
        val time = dateFormatter.format(Date(System.currentTimeMillis()))
        println("$time: ERROR | $tag | $parameters\n")
    }

}