package com.example.multiviewtyperecyclerview.utils

import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData

sealed class LCE {
    object Loading : LCE()
    data class Content(val appData: AppData): LCE()
    object Error : LCE()
}