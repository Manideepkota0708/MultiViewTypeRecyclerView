package com.example.multiviewtyperecyclerview.utils

import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData

sealed class LCE {
    object YetToFetch: LCE()
    object Loading : LCE()
    data class Content(val appDataList: List<AppData>) : LCE()
    data class Error(val errorMessage: String) : LCE()
}