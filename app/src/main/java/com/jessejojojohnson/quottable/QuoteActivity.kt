package com.jessejojojohnson.quottable

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.SimpleAdapter
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import androidx.core.view.isVisible
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlinx.android.synthetic.main.activity_quote.*

class QuoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quote)

        if(intent.hasExtra(Intent.EXTRA_TEXT)){
            etQuote.setText(intent.getStringExtra(Intent.EXTRA_TEXT))
        }
        if(intent.hasExtra(Intent.EXTRA_SUBJECT)){
            tvAttribution.setText(intent.getStringExtra(Intent.EXTRA_SUBJECT))
        }

        tvWatermark.typeface = Typeface.createFromAsset(assets, "fonts/Arapey_Italic.ttf")

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
                tvAttribution.isEnabled = !tvAttribution.isEnabled //do it again!
                if(etQuote.isEnabled){
                    btnEdit.setImageResource(R.drawable.ic_done_black)
                }else{
                    btnEdit.setImageResource(R.drawable.ic_edit_black)
                }
            }
        })

//        tvAttribution.setOnClickListener(object: View.OnClickListener{
//            var visible = true
//            override fun onClick(v: View?){
//                if(visible){
//                    tvAttribution.setTextColor(resources.getColor(R.color.transparent))
//                }else{
//                    tvAttribution.setTextColor(etQuote.currentTextColor)
//                }
//                visible = !visible
//            }
//        })

        btnBackground.setOnClickListener(object: View.OnClickListener{
            val backgrounds = intArrayOf(
                R.color.colorPrimary,
                R.color.whiteTint,
                R.color.red,
                R.drawable.gal_greens,
                R.drawable.gal_polygons,
                R.drawable.gal_triangles,
                R.drawable.gal_clouds,
                R.drawable.gal_pen,
                R.drawable.gal_heart,
                R.drawable.gal_hearts,
                R.drawable.gal_nebulous,
                R.drawable.gal_storm,
                R.drawable.gal_storm_2,
                R.drawable.gal_stars,
                R.drawable.gal_white,
                R.drawable.gal_woods,
                R.drawable.gal_mountain_woods,
                R.drawable.gal_winter,
                R.drawable.gal_turner_rome,
                R.color.colorPrimaryDark)
            var selected = 0
            override fun onClick(v: View?) {
                if(selected == backgrounds.size) selected = 0
                ivBackground.setImageResource(backgrounds[selected])
                selected++
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
            val sizes = intArrayOf(33, 36, 39, 41, 44, 25, 27, 30)
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
                etQuote.setTextColor(resources.getColor(colors[selected]))
                tvAttribution.setTextColor(resources.getColor(colors[selected]))
                selected++
            }
        })
    }

    private fun setUpGallery(){
        val galleryIds: IntArray = intArrayOf(
            R.drawable.gal_nebulous,
            R.drawable.gal_polygons,
            R.drawable.gal_greens,
            R.drawable.gal_pen,
            R.drawable.gal_triangles)
        val from: Array<String> = arrayOf("image")
        val to: IntArray = intArrayOf(R.id.imageView)
        val galleryData = ArrayList<MutableMap<String, Int>>()
        galleryIds.forEach {
            val map = HashMap<String, Int>()
            map["image"] = it
            galleryData.add(map)
        }
        val galleryAdapter = SimpleAdapter(this,
            galleryData, R.layout.gallery_item, from, to)
        lvGallery.adapter = galleryAdapter
        lvGallery.onItemClickListener = object: AdapterView.OnItemClickListener{
            override fun onItemClick(parent: AdapterView<*>?, view: View?,
                                     position: Int, id: Long){
                ivBackground.setImageResource(galleryIds[position])
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
                "com.jessejojojohnson.fileprovider", file)
        }catch (e: IOException){
            Toast.makeText(this,
                "Quottable could not create the file! ☹️",
                Toast.LENGTH_SHORT).show()
        }
        return uri
    }
}
