package com.jessejojojohnson.quottable.data

import android.content.res.AssetManager
import android.graphics.Typeface
import com.jessejojojohnson.quottable.R

interface Provider<T> {
    fun getNext() : T
}

class FontProvider : Provider<String> {

    private val fonts = arrayOf(
        "fonts/Roboto-Light.ttf",
        "fonts/Roboto-Medium.ttf",
        "fonts/EBGaramond-Regular.ttf",
        "fonts/Roboto-ThinItalic.ttf",
        "fonts/Fanwood.otf",
        "fonts/Arapey_Italic.ttf",
        "fonts/imfell.otf",
        "fonts/PTSerif.ttc"
    )
    private var iterator = fonts.iterator()

    override fun getNext(): String {
        if (!iterator.hasNext()) iterator = fonts.iterator()
        return iterator.next()
    }
}

class TypefaceProvider(
    private val assetManager: AssetManager,
    private val fontProvider: FontProvider
    ) : Provider<Typeface> {

    override fun getNext(): Typeface {
        return Typeface.createFromAsset(assetManager, fontProvider.getNext())
    }
}

class ColorProvider : Provider<Int> {

    private val colors = intArrayOf(
        R.color.red,
        R.color.black,
        R.color.yellow,
        R.color.orange,
        R.color.colorPrimary,
        R.color.white
    )
    private var iterator = colors.iterator()

    override fun getNext(): Int {
        if (!iterator.hasNext()) iterator = colors.iterator()
        return iterator.next()
    }
}