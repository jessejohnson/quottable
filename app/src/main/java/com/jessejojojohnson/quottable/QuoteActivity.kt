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
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import kotlinx.android.synthetic.main.activity_quote.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class QuoteActivity : AppCompatActivity() {

    private val PERMISSION_REQUEST_ALL_NECESSARY = 9091
    private val PICK_IMAGE = 19081
    private val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            etQuote.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
        if(intent.hasExtra(Intent.EXTRA_SUBJECT)){
            etAttribution.setText(intent.getStringExtra(Intent.EXTRA_SUBJECT))
        }

        tvWatermark.typeface = Typeface.createFromAsset(assets, "fonts/Arapey_Italic.ttf")

        btnPickImage.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?){
                if(checkPermissionsAndRequest(this@QuoteActivity, REQUIRED_PERMISSIONS,
                        PERMISSION_REQUEST_ALL_NECESSARY)){
                    getMedia()
                }else{
                    Toast.makeText(this@QuoteActivity,
                        "No permissions media️",
                        Toast.LENGTH_SHORT).show()
                }
            }
        })

        btnShare.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                val uri = saveImage(clImageQuote.drawToBitmap())
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.type = "image/png"
                startActivity(intent)
            }
        })

        btnEdit.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?){
                etQuote.isEnabled = !etQuote.isEnabled //toggle enabled!
                etAttribution.isEnabled = !etAttribution.isEnabled //do it again!
                if(etQuote.isEnabled){
                    btnEdit.setImageResource(R.drawable.ic_done_black)
                }else{
                    btnEdit.setImageResource(R.drawable.ic_edit_black)
                }
            }
        })

        btnFont.setOnClickListener(object: View.OnClickListener{
            val fonts = arrayOf(
                "fonts/Roboto-Light.ttf",
                "fonts/Roboto-Medium.ttf",
                "fonts/EBGaramond-Regular.ttf",
                "fonts/Roboto-ThinItalic.ttf",
                "fonts/Fanwood.otf",
                "fonts/Arapey_Italic.ttf",
                "fonts/imfell.otf",
                "fonts/PTSerif.ttc")
            var selectedFont = 0
            override fun onClick(v: View?){
                if(selectedFont == fonts.size) selectedFont = 0
                etQuote.typeface = Typeface.createFromAsset(assets, fonts[selectedFont])
                selectedFont++
            }
        })

        btnSize.setOnClickListener(object: View.OnClickListener{
            val sizes = intArrayOf(33, 36, 39, 41, 44, 18, 20, 23, 25, 27, 30)
            var selected = 0
            override fun onClick(v: View?) {
                if(selected == sizes.size) selected = 0
                etQuote.textSize = sizes[selected].toFloat()
                selected++
            }
        })

        btnColor.setOnClickListener(object: View.OnClickListener{
            val colors = intArrayOf(
                R.color.red,
                R.color.black,
                R.color.yellow,
                R.color.colorPrimary,
                R.color.white)
            var selected = 0
            override fun onClick(v: View?) {
                if(selected == colors.size) selected = 0
                ivBackground.setImageResource(colors[selected])
                selected++
            }
        })
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
                builder.setMessage("Quottable need permission to pick images from your gallery")
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
