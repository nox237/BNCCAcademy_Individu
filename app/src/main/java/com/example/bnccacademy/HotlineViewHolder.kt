package com.example.bnccacademy

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_hotline.view.*

class HotlineViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    fun bind(data: HotlineData) {
        itemView.tvHotlineName.text = data.name
        itemView.tvHotLineNumber.text = data.phone
        if (data.imgIcon.isNotBlank()){
            Picasso.get().load(data.imgIcon).into(itemView.image)
        }
        // picasso akan meload data dari gambar yang berdasarkan link tersebut
    }

    init {
        itemView.setOnClickListener{
            actionDial(itemView, this.itemView.tvHotLineNumber.text)
        }
    }

    private fun actionDial(itemView: View, phoneNumber: CharSequence) {
        val intent = Intent().apply {
            action = Intent.ACTION_DIAL
            data = Uri.parse("tel:$phoneNumber")
        }
        itemView.context.startActivity(intent)
    }
}