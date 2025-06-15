package com.grepp.smartwatcha.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class EmailAuthApplication

fun main(args: Array<String>) {
    runApplication<EmailAuthApplication>(*args)
} 