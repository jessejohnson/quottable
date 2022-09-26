package com.jessejojojohnson.quottable.data

data class Quote(
    val modifiedDateInMillis: Long,
    val text: String,
    val attribution: String,
    val fontName: String,
    val background: Int,
    val backgroundImageUri: String,
    val fontSize: Float,
    val maskPercentage: Int
) {
    companion object {
        // explicit, sane default
        val EMPTY = Quote(
            modifiedDateInMillis = System.currentTimeMillis(),
            text = "",
            attribution = "",
            fontName = "fonts/PTSerif.ttc",
            background = 0,
            backgroundImageUri = "",
            fontSize = 50F,
            maskPercentage = 50
        )
    }
}