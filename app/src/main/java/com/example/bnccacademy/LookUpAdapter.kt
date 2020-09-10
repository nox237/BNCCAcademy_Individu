package com.example.bnccacademy

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class LookUpAdapter(private val lookUpList: MutableList<LookUpData>): RecyclerView.Adapter<LookUpViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LookUpViewHolder {
        // Tempate viewHolder di buat / inisialisasi (dipanggil satu kali) atau ketika ada perubahan data
        return LookUpViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_look_up,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LookUpViewHolder, position: Int) {
        // tempat dimana kita akan bind viewholder dengan data
        holder.bind(lookUpList[position])
    }

    override fun getItemCount(): Int {
        // Tempat adapter nanya berapa banyak data yang mw dirender
        return lookUpList.size
    }

    fun updateData(newList: List<LookUpData>) {
        lookUpList.clear()
        lookUpList.addAll(newList)

        notifyDataSetChanged()
    }
}