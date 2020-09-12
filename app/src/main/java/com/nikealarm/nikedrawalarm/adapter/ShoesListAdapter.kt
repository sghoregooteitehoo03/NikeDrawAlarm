package com.nikealarm.nikedrawalarm.adapter

import android.app.AlarmManager
import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.nikealarm.nikedrawalarm.component.MyAlarmReceiver
import com.nikealarm.nikedrawalarm.R
import com.nikealarm.nikedrawalarm.database.ShoesDataModel
import com.nikealarm.nikedrawalarm.other.Contents
import com.nikealarm.nikedrawalarm.ui.dialog.AlarmDialog
import java.util.*

class ShoesListAdapter(
    private val mContext: Context,
    private val fragmentManager: FragmentManager
) :
    PagedListAdapter<ShoesDataModel, ShoesListAdapter.DrawListViewHolder>(
        diffCallback
    ) {

    private lateinit var itemListener: ItemClickListener
    private lateinit var imageListener: ImageClickListener

    interface ItemClickListener {
        fun onClickItem(newUrl: String?)
    }

    interface ImageClickListener {
        fun onClickImage(newUrl: String, shoesImageUrl: String, imageView: ImageView)
    }

    fun setOnItemClickListener(listener: ItemClickListener) {
        itemListener = listener
    }

    fun setOnImageClickListener(listener: ImageClickListener) {
        imageListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DrawListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.shoes_listitem, parent, false)
        return DrawListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DrawListViewHolder, position: Int) {
        holder.bindView(getItem(position))
    }

    inner class DrawListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val shoesImage = itemView.findViewById<ImageView>(R.id.drawList_imgItem)
        val shoesSubTitleText = itemView.findViewById<TextView>(R.id.drawList_shoesSubTitle_text)
        val shoesTitleText = itemView.findViewById<TextView>(R.id.drawList_shoesTitle_text)
        val howToEventText = itemView.findViewById<TextView>(R.id.drawList_howToEvent_text)
        val learnMoreText = itemView.findViewById<TextView>(R.id.drawList_learnMore_text)

        fun bindView(data: ShoesDataModel?) {
            with(shoesImage) {
                Glide.with(mContext).load(data?.shoesImageUrl).into(this)
                transitionName = data?.shoesUrl

                setOnClickListener {
                    imageListener.onClickImage(
                        data?.shoesUrl!!,
                        data.shoesImageUrl!!,
                        shoesImage
                    )
                }
            }
            shoesSubTitleText.text = data?.shoesSubTitle
            shoesTitleText.text = data?.shoesTitle
            howToEventText.text = data?.shoesPrice

            learnMoreText.setOnClickListener {
                itemListener.onClickItem(data?.shoesUrl)
            }
        }
    }

    companion object {
        private val diffCallback = object : DiffUtil.ItemCallback<ShoesDataModel>() {
            override fun areItemsTheSame(
                oldItem: ShoesDataModel,
                newItem: ShoesDataModel
            ): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(
                oldItem: ShoesDataModel,
                newItem: ShoesDataModel
            ): Boolean =
                oldItem.shoesTitle == newItem.shoesTitle
        }
    }
}