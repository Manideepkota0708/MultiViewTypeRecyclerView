package com.example.multiviewtyperecyclerview.uilayer.uielements

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData


class MultiViewTypeAdapter(
    private val appDataList: List<AppData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount() = appDataList.size
}