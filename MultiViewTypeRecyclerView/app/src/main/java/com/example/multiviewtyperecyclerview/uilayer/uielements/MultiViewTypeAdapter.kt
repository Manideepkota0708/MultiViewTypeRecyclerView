package com.example.multiviewtyperecyclerview.uilayer.uielements

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData


class MultiViewTypeAdapter(
    private val appDataList: List<AppData>,
    private val onImageClicked: (imageView: ImageView) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int {
        return appDataList[position].type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_itemview_photo, parent, false)
                return PhotoViewHolder(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_itemview_single_choice, parent, false)
                return SingleChoiceViewHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.layout_itemview_comment, parent, false)
                return CommentViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val appData = appDataList[position]
        when (holder.itemViewType) {
            0 -> {
                val photoViewHolder = holder as PhotoViewHolder
                photoViewHolder.titleView.text = appData.title
                photoViewHolder.imageView.setOnClickListener {
                    onImageClicked(it as ImageView)
                }
            }
            1 -> {
                val singleChoiceViewHolder = holder as SingleChoiceViewHolder
                singleChoiceViewHolder.titleView.text = appData.title
                appData.dataMap?.options?.let {
                    singleChoiceViewHolder.addItemsToRadioGroup(it)
                }
            }
            else -> {
                val commentView = holder as CommentViewHolder

            }
        }
    }

    override fun getItemCount() = appDataList.size

    inner class PhotoViewHolder(photoView: View) : RecyclerView.ViewHolder(photoView) {
        val titleView: TextView = photoView.findViewById(R.id.title)
        val imageView: ImageView = photoView.findViewById(R.id.imageView)
    }

    inner class SingleChoiceViewHolder(singleChoiceView: View) :
        RecyclerView.ViewHolder(singleChoiceView) {
        val titleView: TextView = singleChoiceView.findViewById(R.id.title)
        private val radioGroup: RadioGroup = singleChoiceView.findViewById(R.id.radioGroup)

        fun addItemsToRadioGroup(stringList: List<String>) {
            radioGroup.apply {
                removeAllViews()
                stringList.forEach {
                    addView(RadioButton(this.context).apply {
                        text = it
                    })
                }
            }
        }
    }

    inner class CommentViewHolder(commentView: View) : RecyclerView.ViewHolder(commentView) {

    }

}