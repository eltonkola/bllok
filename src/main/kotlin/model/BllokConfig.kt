package com.eltonkola.model

data class BllokConfig(
    val templatePath: String,
    val inputPath: String,
    val outputPath: String,
    val debug: Boolean = false
)