package com.example.multiviewtyperecyclerview.uilayer.dataclass

class AppData(
    val type: ContentType,
    val id: String,
    val title: String,
    val dataMap: DataMap
)

class DataMap(
    val options: List<String>
)

enum class ContentType {
    PHOTO, SINGLE_CHOICE, COMMENT
}
