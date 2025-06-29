package com.grepp.smartwatcha

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication(scanBasePackages = ["com.grepp.smartwatcha"])
@EnableScheduling
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
} 