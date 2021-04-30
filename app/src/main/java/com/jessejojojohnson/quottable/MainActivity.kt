package com.jessejojojohnson.quottable

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/*
* We'll leave MainActivity open for future modification
* and have the app proper in QuoteActivity*/
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivity(Intent(this, QuoteActivity::class.java))
        finish()
    }
}
