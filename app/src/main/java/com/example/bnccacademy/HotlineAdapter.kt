package com.example.bnccacademy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class HotlineAdapter(private val lookUpList: MutableList<HotlineData>): RecyclerView.Adapter<HotlineViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HotlineViewHolder {
        // Tempat viewHolder di buat / inisialisasi (dipanggil satu kali) atau ketika ada perubahan data
        return HotlineViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_hotline,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: HotlineViewHolder, position: Int) {
        // tempat dimana kita akan bind viewholder dengan data
        holder.bind(lookUpList[position])
    }

    override fun getItemCount(): Int {
        // Tempat adapter nanya berapa banyak data yang mw dirender
        return lookUpList.size
    }

    fun updateData(newList: List<HotlineData>) {
        lookUpList.clear()
        lookUpList.addAll(newList)

        notifyDataSetChanged()
    }
}