package com.example.accessible.recyclerView

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.accessible.R
import java.util.Random

class MyAdapter(private val itemList: List<Item>) : RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private val colors = intArrayOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.CYAN)
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val descriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = itemList[position]
        holder.titleTextView.text = currentItem.title
        holder.descriptionTextView.text = currentItem.description

        holder.titleTextView.setOnClickListener {
            // 随机选择一个颜色
            val random = Random()
            val randomColor: Int = colors[random.nextInt(colors.size)]

            // 设置按钮背景颜色
            holder.titleTextView.setBackgroundColor(randomColor)
        }
        holder.descriptionTextView.setOnClickListener {
            // 随机选择一个颜色
            val random = Random()
            val randomColor: Int = colors[random.nextInt(colors.size)]

            // 设置按钮背景颜色
            holder.descriptionTextView.setBackgroundColor(randomColor)
        }

    }

    override fun getItemCount(): Int {
        return itemList.size
    }
}
data class Item(val title: String, val description: String)

