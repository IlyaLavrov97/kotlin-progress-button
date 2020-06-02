package com.example.custombutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        customButton.setOnClickListener {
            if (customButton.isLoading) {
                customButton.stopLoading()
            } else {
                customButton.startLoading()
            }
        }
    }
}
