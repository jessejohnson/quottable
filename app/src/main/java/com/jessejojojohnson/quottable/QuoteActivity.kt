package com.jessejojojohnson.quottable

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.google.android.material.slider.Slider
import kotlinx.android.synthetic.main.activity_quote.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.math.roundToInt

class QuoteActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_ALL_NECESSARY = 9091
    private val PICK_IMAGE = 19081
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE)
    private val fonts = arrayOf(
        "fonts/Roboto-Light.ttf",
        "fonts/Roboto-Medium.ttf",
        "fonts/EBGaramond-Regular.ttf",
        "fonts/Roboto-ThinItalic.ttf",
        "fonts/Fanwood.otf",
        "fonts/Arapey_Italic.ttf",
        "fonts/imfell.otf",
        "fonts/PTSerif.ttc")
    private val colors = intArrayOf(
        R.color.red,
        R.color.black,
        R.color.yellow,
        R.color.orange,
        R.color.colorPrimary,
        R.color.white)
    private var selectedFont = 0
    private var selectedColor = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            etQuote.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
        if(intent.hasExtra(Intent.EXTRA_SUBJECT)){
            etAttribution.setText(intent.getStringExtra(Intent.EXTRA_SUBJECT))
        }

        tvWatermark.typeface =  Typeface.createFromAsset(assets, fonts[selectedFont])
        etQuote.textSize = fontSizeSlider.value
        ivImageMask.drawable.alpha = maskOverlaySlider.value.toInt()

        fontSizeSlider.setLabelFormatter {value: Float ->
            return@setLabelFormatter "Size: ${value.roundToInt()}"
        }
        fontSizeSlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            etQuote.textSize = value
        })

        maskOverlaySlider.setLabelFormatter {value: Float ->
            val percentage = (value/maskOverlaySlider.valueTo)*100
            return@setLabelFormatter "Overlay: ${percentage.roundToInt()}%"
        }
        maskOverlaySlider.addOnChangeListener(Slider.OnChangeListener { _, value, _ ->
            ivImageMask.drawable.alpha = value.roundToInt()
        })

        fabShare.setOnClickListener {
            val uri = saveImage(clImageQuote.drawToBitmap())
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(Intent.EXTRA_STREAM, uri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.type = "image/png"
            startActivity(intent)
        }

        bottomBar.setOnMenuItemClickListener { item ->
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
        etQuote.isEnabled = !etQuote.isEnabled //toggle enabled!
        etAttribution.isEnabled = !etAttribution.isEnabled //do it again!
        if (etQuote.isEnabled) {
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_done_black)
        } else {
            item.icon = ContextCompat.getDrawable(this, R.drawable.ic_edit_black)
        }
    }

    private fun cycleBackgroundColours(){
        if(selectedColor == colors.size) selectedColor = 0
        ivBackground.setImageResource(colors[selectedColor])
        selectedColor++
    }

    private fun cycleFonts(){
        if(selectedFont == fonts.size) selectedFont = 0
        etQuote.typeface = Typeface.createFromAsset(assets, fonts[selectedFont])
        selectedFont++
    }

    private fun pickImage(){
        if(checkPermissionsAndRequest(this@QuoteActivity, REQUIRED_PERMISSIONS,
                PERMISSION_REQUEST_ALL_NECESSARY)){
            getMedia()
        }else{
            Toast.makeText(this@QuoteActivity,
                "Quottable needs your permission to do this!️", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMedia(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if(intent.resolveActivity(packageManager) != null){
            startActivityForResult(intent, PICK_IMAGE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK){
            if(data != null && data.data != null){
                ivBackground.setImageURI(data.data)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_ALL_NECESSARY) {
            if (!hasPermissions(this, *REQUIRED_PERMISSIONS)){
                //no permissions granted!
                val builder =
                    AlertDialog.Builder(this)
                builder.setMessage("Quottable needs permission to pick images from your gallery")
                    .setTitle("We Don't Have Permission!")
                    .setPositiveButton(android.R.string.ok, null)
                    .show()
            }else{
                getMedia()
            }
        }
    }

    private fun saveImage(image: Bitmap): Uri?{
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
