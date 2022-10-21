package com.example.multiviewtyperecyclerview.uilayer.uielements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.viewmodel.MainActivityViewModel
import com.example.multiviewtyperecyclerview.utils.LCE
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private val mainActivityViewModel by viewModels<MainActivityViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainActivityViewModel(application) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bindFlows()
        mainActivityViewModel.loadAppData()
    }

    private fun bindFlows() {
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.appDataStateFlow
                    .filterIsInstance<LCE.Content>()
                    .collect {
                        Log.d(TAG, it.appDataList.toString())
                        findViewById<RecyclerView>(R.id.recyclerView).apply {
                            layoutManager = LinearLayoutManager(this@MainActivity)
                            adapter = MultiViewTypeAdapter(it.appDataList)
                        }
                    }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mainActivityViewModel.appDataStateFlow
                    .filterIsInstance<LCE.Error>()
                    .collect {
                        Log.d(TAG, it.toString())
                        Toast.makeText(
                            this@MainActivity,
                            "please check input, unable to parse Json Array \n reason: ${it.errorMessage}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
        }
    }
}