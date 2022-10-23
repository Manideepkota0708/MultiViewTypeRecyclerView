package com.example.multiviewtyperecyclerview.uilayer.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData
import com.example.multiviewtyperecyclerview.utils.LCE
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val gson: Gson = Gson()

    private val appDataMutableStateFlow = MutableStateFlow<LCE>(LCE.YetToFetch)

    val appDataStateFlow: StateFlow<LCE> = appDataMutableStateFlow

    val imageMap = hashMapOf<String, Uri>()

    fun loadAppData() {
        viewModelScope.launch {
            if (appDataStateFlow.value == LCE.YetToFetch || appDataStateFlow.value is LCE.Error) {
                appDataMutableStateFlow.emit(LCE.Loading)
                try {
                    val appData = readDataFromRawFolder()
                    appDataMutableStateFlow.emit(LCE.Content(appData))
//                while(true){
//                    delay(2000)
//                    val newAppData = (appDataMutableStateFlow.value as LCE.Content).appDataList.toMutableList()
//                    if(newAppData.size == 0) break
//                    newAppData.removeAt(0)
//                    appDataMutableStateFlow.emit(LCE.Content(newAppData))
//                }
                } catch (exception: Exception) {
                    appDataMutableStateFlow.emit(LCE.Error(exception.toString()))
                }
            } else return@launch
        }
    }


    private suspend fun readDataFromRawFolder(): List<AppData> {
        return withContext(Dispatchers.IO) {
            val content =
                (getApplication() as Application).resources.openRawResource(R.raw.test_content)
                    .bufferedReader()
                    .use { it.readText() }
            val type = object : TypeToken<List<AppData>>() {}.type
            gson.fromJson(content, type)
        }
    }

}