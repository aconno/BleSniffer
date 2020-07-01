package com.aconno.blesniffer.ui.base

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.aconno.blesniffer.R

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }

}