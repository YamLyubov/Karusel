package com.karusel.neprav

import androidx.recyclerview.widget.DiffUtil

class TopicDiffCallback(
    private val old: List<Topic>,
    private val new: List<Topic>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return old.size
    }

    override fun getNewListSize(): Int {
        return new.size
    }

    override fun areItemsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition].topic_id == new[newPosition].topic_id
    }

    override fun areContentsTheSame(oldPosition: Int, newPosition: Int): Boolean {
        return old[oldPosition] == new[newPosition]
    }

}