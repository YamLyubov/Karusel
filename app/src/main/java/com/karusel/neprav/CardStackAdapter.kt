package com.karusel.neprav

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CardStackAdapter (
    private var topics: List<Topic> = emptyList()
) : RecyclerView.Adapter<CardStackAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(inflater.inflate(R.layout.topic_layout, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = topics[position]
        holder.topicText.text = topic.text
//        holder.itemView.setOnClickListener { v ->
//            Toast.makeText(v.context, topic.text, Toast.LENGTH_SHORT).show()
//        }
    }

    override fun getItemCount(): Int {
        return topics.size
    }

    fun setTopics(topics: List<Topic>) {
        this.topics = topics
    }

    fun getTopics(): List<Topic> {
        return topics
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val topicText: TextView = view.findViewById(R.id.topicTextView)
    }
}
