package com.jessejojojohnson.quottable.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.slider.Slider
import com.jessejojojohnson.quottable.R
import com.jessejojojohnson.quottable.data.ColorProvider
import com.jessejojojohnson.quottable.data.FontProvider
import com.jessejojojohnson.quottable.data.Quote
import com.jessejojojohnson.quottable.data.TypefaceProvider
import com.jessejojojohnson.quottable.databinding.ActivityQuoteBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

class QuoteActivity : AppCompatActivity() {

    private val colorProvider = ColorProvider()
    private val fontProvider = FontProvider()
    private val typefaceProvider by lazy {
        TypefaceProvider(assets, fontProvider)
    }
    private val binding by lazy {
        ActivityQuoteBinding.inflate(layoutInflater)
    }
    private val viewModel: QuoteActivityViewModel by viewModels { QuoteActivityViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.getQuoteFlow().collect { quote ->
                    binding.apply {
                        etQuote.setText(quote.text)
                        etAttribution.setText(quote.attribution)
                        if (quote.background != Quote.EMPTY.background) {
                            ivBackground.setImageResource(quote.background)
                        }
                        if (quote.backgroundImageUri != Quote.EMPTY.backgroundImageUri) {
                            ivBackground.setImageURI(Uri.parse(quote.backgroundImageUri))
                        }
                        etQuote.typeface = Typeface.createFromAsset(assets, quote.fontName)
                        ivImageMask.drawable.alpha = quote.maskPercentage
                        etQuote.textSize = quote.fontSize

                        fontSizeSlider.value = quote.fontSize
                        maskOverlaySlider.value = quote.maskPercentage.toFloat()
                    }
                }
            }
        }

        intent.apply {
            if (hasExtra(Intent.EXTRA_TEXT)) {
                viewModel.updateText(
                    text = getStringExtra(Intent.EXTRA_TEXT) ?: "",
                    attribution = getStringExtra(Intent.EXTRA_TITLE) ?: ""
                )
            }
        }

        binding.fontSizeSlider.apply {
            setLabelFormatter {value: Float ->
                return@setLabelFormatter "Size: ${value.roundToInt()}"
            }
            addOnChangeListener { _, value, _ ->
                binding.etQuote.textSize = value
            }
            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) { }

                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.changeFontSize(value)
                }
            })
        }

        binding.maskOverlaySlider.apply {
            setLabelFormatter { value: Float ->
                val percentage = (value/binding.maskOverlaySlider.valueTo)*100
                return@setLabelFormatter "Overlay: ${percentage.roundToInt()}%"
            }
            addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
                binding.ivImageMask.drawable.alpha = value.roundToInt()
            })
            addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
                override fun onStartTrackingTouch(slider: Slider) { }

                override fun onStopTrackingTouch(slider: Slider) {
                    viewModel.setOverlayMask(slider.value.roundToInt())
                }
            })
        }

        binding.fabShare.setOnClickListener {
            val uri = getBitmapUri(binding.clImageQuote.drawToBitmap())
            Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                type = "image/png"
                startActivity(this)
            }
        }

        binding.bottomBar.setOnMenuItemClickListener { item ->
            when(item.itemId){
                R.id.menu_edit_text -> toggleEdit(item)
                R.id.menu_cycle_backgrounds -> cycleBackgroundColours()
                R.id.menu_cycle_fonts -> cycleFonts()
                R.id.menu_pick_image -> pickImage()
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun toggleEdit(item: MenuItem){
        binding.apply {
            etQuote.isEnabled = !etQuote.isEnabled // toggle enabled!
            etAttribution.isEnabled = !etAttribution.isEnabled // do it again!
            if (etQuote.isEnabled) {
                item.icon = ContextCompat.getDrawable(
                    this@QuoteActivity, R.drawable.ic_done_black
                )
            } else {
                item.icon = ContextCompat.getDrawable(
                    this@QuoteActivity, R.drawable.ic_edit_black
                )
                viewModel.updateText(
                    text = etQuote.text.toString(),
                    attribution = etAttribution.text.toString()
                )
            }
        }
    }

    private fun cycleBackgroundColours() = viewModel.changeColor(colorProvider.getNext())

    private fun cycleFonts() = viewModel.changeFont(fontProvider.getNext())

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) {
        lifecycleScope.launch(Dispatchers.IO) {
            it?.let { selectedImageUri ->
                val imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    ImageDecoder.decodeBitmap(ImageDecoder
                        .createSource(contentResolver, selectedImageUri))
                } else {
                    MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                }
                getBitmapUri(imageBitmap)?.let { savedImageUri ->
                    viewModel.setBackgroundImage(savedImageUri.toString())
                }
            }
        }
    }

    private fun pickImage() = imagePicker.launch(
        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
    )

    private fun getBitmapUri(image: Bitmap): Uri?{
        var uri: Uri? = null
        try {
            val imagesFolder = File(cacheDir, "images")
            imagesFolder.mkdirs()
            val file = File(imagesFolder,
                "quottable-${System.currentTimeMillis()}.png")
            val stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(this,
                "com.jessejojojohnson.quottable.fileprovider", file)
        }catch (e: IOException){
            Toast.makeText(this,
                "Quottable could not create the file! ☹️",
                Toast.LENGTH_SHORT).show()
        }
        return uri
    }
}
