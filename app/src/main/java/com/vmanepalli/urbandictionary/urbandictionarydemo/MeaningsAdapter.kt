package com.vmanepalli.urbandictionary.urbandictionarydemo

import android.icu.text.SimpleDateFormat
import android.media.MediaPlayer
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vmanepalli.urbandictionary.urbandictionarydemo.models.Meaning
import kotlinx.android.synthetic.main.meaning_layout.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.random.Random

class MeaningsAdapter(var meanings: List<Meaning>) :
    RecyclerView.Adapter<MeaningsAdapter.MeaningHolder>() {

    private var mediaPlayer: MediaPlayer = MediaPlayer()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeaningHolder {
        return MeaningHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.meaning_layout, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return meanings.size
    }

    override fun onBindViewHolder(holder: MeaningHolder, position: Int) {
        holder.updateViews(meanings[position])
    }

    //region Update/sort meanings data
    fun replaceData(meanings: List<Meaning>, sortBy: Boolean) {
        this.meanings = meanings
        sortedBy(sortBy)
    }

    // Just sorts in ascending order if parameter is true, otherwise false.
    fun sortedBy(ascendingOrder: Boolean) {
        meanings = if (ascendingOrder) {
            meanings.sortedBy { it.thumbs_up }
        } else {
            this.meanings.sortedByDescending { it.thumbs_up }
        }
    }
    //endregion

    //region Media Player functions
    private fun play(url: String) {
        GlobalScope.launch(Dispatchers.Default) {
            try {
                killPlayer()
                mediaPlayer = MediaPlayer()
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepare()
                mediaPlayer.start()
            } catch (e: Exception) {
                print("Failed to initialize media player - ${e.localizedMessage}")
            }
        }
    }

    private fun killPlayer() {
        if (!mediaPlayer.isPlaying) {
            return
        }
        mediaPlayer.reset()
    }

    private fun getSound(urls: List<String>): String {
        if (urls.isEmpty()) {
            return ""
        }
        val position = Random.nextInt(0, urls.size)
        print(urls[position])
        return urls[position]
    }
    //endregion

    //region ViewHolder class
    inner class MeaningHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var wordView = itemView.word
        private var thumbsUpView = itemView.thumbs_up_label
        private var thumbsDownView = itemView.thumbs_down_label
        private var definitionView = itemView.definition
        private var exampleView = itemView.example
        private var authorView = itemView.author
        private var dateView = itemView.date
        private var playView = itemView.play

        fun updateViews(meaning: Meaning) {
            wordView.text = meaning.word
            thumbsUpView.text = meaning.thumbs_up.toString()
            thumbsDownView.text = meaning.thumbs_down.toString()
            definitionView.text = meaning.definition
            exampleView.text = meaning.example
            authorView.text = meaning.author
            dateView.text = convertDate(meaning.written_on)
            if (meaning.sound_urls.isEmpty()) {
                playView?.visibility = View.INVISIBLE
                return
            } else {
                playView?.visibility = View.VISIBLE
            }
            playView?.setOnClickListener { play(getSound(meaning.sound_urls)) }
        }
    }
    //endregion

    companion object {
        fun convertDate(dateString: String): String {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val formatter =
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                val date = LocalDate.parse(dateString, formatter)
                date.month.name.substring(
                    0,
                    3
                ) + " " + date.dayOfMonth + ", " + date.year.toString()
            } else {
                val formatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.ENGLISH)
                val date = formatter.parse(dateString)
                date.month.toString().substring(0, 3) + " " + date.day + ", " + date.year.toString()
            }
        }
    }
}