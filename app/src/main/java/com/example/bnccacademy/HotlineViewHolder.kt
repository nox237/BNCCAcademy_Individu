package com.example.bnccacademy

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_hotline.view.*
import kotlinx.android.synthetic.main.item_look_up.view.tvLookUpProvinceName

class HotlineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(data: HotlineData) {
        itemView.tvHotlineName.text = data.name
        itemView.tvHotLineNumber.text = data.phone
        if (data.imgIcon.isNotBlank()){
            Picasso.get().load(data.imgIcon).into(itemView.image)
        }
        // picasso akan meload data dari gambar yang berdasarkan link tersebut
    }
}