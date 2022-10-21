package com.example.multiviewtyperecyclerview.uilayer.uielements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.viewmodel.MainActivityViewModel

class MainActivity : AppCompatActivity() {

    val mainActivityViewModel by viewModels<MainActivityViewModel> ()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}