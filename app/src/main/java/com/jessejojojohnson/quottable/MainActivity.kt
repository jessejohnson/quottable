package com.jessejojojohnson.quottable

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ivShare.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                var bitmap = clImageQuote.drawToBitmap()
                var uri = saveImage(bitmap)
                val intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, uri)
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                intent.type = "image/png"
                startActivity(intent)
            }
        })
    }

    private fun saveImage(image: Bitmap): Uri?{
        var imagesFolder = File(cacheDir, "images")
        var uri: Uri? = null
        try {
            imagesFolder.mkdirs()
            var file = File(imagesFolder,
                "quottable-${System.currentTimeMillis()}.png")
            var stream = FileOutputStream(file)
            image.compress(Bitmap.CompressFormat.PNG, 90, stream)
            stream.flush()
            stream.close()
            uri = FileProvider.getUriForFile(this,
                "com.jessejojojohnson.fileprovider", file)
        }catch (e: IOException){
            Log.e("qtb",e.localizedMessage )
        }
        return uri
    }
}
