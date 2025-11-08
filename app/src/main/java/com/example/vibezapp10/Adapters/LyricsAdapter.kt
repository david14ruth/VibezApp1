package com.example.vibezapp10.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.vibezapp10.Models.LyricLine
import com.example.vibezapp10.R



class LyricsAdapter(
     val lyrics: List<LyricLine>
) : RecyclerView.Adapter<LyricsAdapter.LyricsViewHolder>() {

    inner class LyricsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val lyricText: TextView = itemView.findViewById(R.id.lyric_line_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LyricsViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_lyric_line, parent, false)
        return LyricsViewHolder(view)
    }

    override fun onBindViewHolder(holder: LyricsViewHolder, position: Int) {
        val line = lyrics[position]
        holder.lyricText.text = line.text
    }

    override fun getItemCount(): Int = lyrics.size
}
