package com.example.multiviewtyperecyclerview.uilayer.uielements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData


class MultiViewTypeAdapter(
    private val appDataList: List<AppData>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            0 -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_itemview_photo, parent, false)
            }
            else -> {
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_itemview_photo, parent, false)
            }
        }
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appData = appDataList[position]
        when (holder.itemViewType) {
            0 -> {
                val photoViewHolder = holder as PhotoViewHolder
                photoViewHolder.titleView.text = appData.title
            }
        }
    }

    override fun getItemCount() = appDataList.size

    inner class PhotoViewHolder(photoView: View) : RecyclerView.ViewHolder(photoView) {
        val titleView: TextView = photoView.findViewById<TextView>(R.id.title)
    }

}