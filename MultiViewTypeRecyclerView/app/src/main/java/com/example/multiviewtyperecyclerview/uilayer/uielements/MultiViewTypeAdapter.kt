package com.example.multiviewtyperecyclerview.uilayer.uielements

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.multiviewtyperecyclerview.R
import com.example.multiviewtyperecyclerview.uilayer.dataclass.AppData


private const val TAG = "MultiViewTypeAdapter"

class MultiViewTypeAdapter(
    private val appDataList: List<AppData>,
    private val imageMap: Map<String, Uri?>,
    private val radioButtonMap: Map<String, Int>,
    private val switchMap: Map<String, Boolean>,
    private val commentMap: Map<String, String>,
    private val onImageClicked: (imageView: ImageView, id: String) -> Unit,
    private val onCancelImageClicked: (id: String) -> Unit,
    private val onRadioButtonClicked: (id: String, index: Int) -> Unit,
    private val onSwitchCompatClicked: (id: String, isEnabled: Boolean) -> Unit
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
                photoViewHolder.imageView.setImageDrawable(
                    ResourcesCompat.getDrawable(
                        photoViewHolder.imageView.resources,
                        R.drawable.outline_add_photo_alternate_24,
                        null
                    )
                )
                imageMap[appData.id]?.let {
                    photoViewHolder.imageView.setImageURI(it)
                }
                photoViewHolder.imageView.setOnClickListener(null)
                photoViewHolder.imageView.setOnClickListener {
                    onImageClicked(it as ImageView, appData.id)
                }
                photoViewHolder.cancelImageView.setOnClickListener(null)
                photoViewHolder.cancelImageView.setOnClickListener {
                    onCancelImageClicked(appData.id)
                    photoViewHolder.imageView.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            photoViewHolder.imageView.resources,
                            R.drawable.outline_add_photo_alternate_24,
                            null
                        )
                    )
                }
            }
            1 -> {
                val singleChoiceViewHolder = holder as SingleChoiceViewHolder
                singleChoiceViewHolder.titleView.text = appData.title
                appData.dataMap?.options?.let {
                    Log.d(TAG, "position: $position, id: ${appData.id}, index: ${radioButtonMap[appData.id]!!}")
                    singleChoiceViewHolder.addItemsToRadioGroup(
                        it,
                        appData.id,
                        radioButtonMap[appData.id]!!
                    )
                }
            }
            else -> {
                val commentView = holder as CommentViewHolder
                switchMap[appData.id]!!.also {
                    commentView.switchCompat.setOnCheckedChangeListener(null)
                    commentView.switchCompat.isChecked = it
                    commentView.switchCompat.setOnCheckedChangeListener { _, isChecked ->
                        onSwitchCompatClicked(appData.id, isChecked)
                        notifyItemChanged(position)
                    }
                    commentView.editText.visibility = if (it) View.VISIBLE else View.GONE
                    commentView.editText.setText(commentMap[appData.id])
                }
            }
        }
    }

    override fun getItemCount() = appDataList.size

    inner class PhotoViewHolder(photoView: View) : RecyclerView.ViewHolder(photoView) {
        val titleView: TextView = photoView.findViewById(R.id.title)
        val imageView: ImageView = photoView.findViewById(R.id.imageView)
        val cancelImageView: ImageView = photoView.findViewById(R.id.cancelButton)
    }

    inner class SingleChoiceViewHolder(singleChoiceView: View) :
        RecyclerView.ViewHolder(singleChoiceView) {
        val titleView: TextView = singleChoiceView.findViewById(R.id.title)
        private val radioGroup: RadioGroup = singleChoiceView.findViewById(R.id.radioGroup)

        fun addItemsToRadioGroup(stringList: List<String>, id: String, selectedIndex: Int) {
            radioGroup.apply {
                setOnCheckedChangeListener(null)
                removeAllViews()
                stringList.forEachIndexed { index, string ->
                    addView(RadioButton(this.context).apply {
                        text = string
                    })
                }
                if (selectedIndex != -1) radioGroup.check(radioGroup.getChildAt(selectedIndex).id)
                setOnCheckedChangeListener { _, checkedId ->
                    onRadioButtonClicked(id, (0 until radioGroup.childCount).find { radioGroup.getChildAt(it).id == checkedId }!!)
                }
            }
        }

    }

    inner class CommentViewHolder(commentView: View) : RecyclerView.ViewHolder(commentView) {
        val switchCompat: SwitchCompat = commentView.findViewById(R.id.switchCompat)
        val editText: EditText = commentView.findViewById(R.id.editText)
    }

}