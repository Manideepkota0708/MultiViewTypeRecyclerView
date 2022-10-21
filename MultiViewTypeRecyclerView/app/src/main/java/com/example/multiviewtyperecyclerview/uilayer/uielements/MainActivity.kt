package com.example.multiviewtyperecyclerview.uilayer.uielements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.*
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
                }
            }
        }
    }
}