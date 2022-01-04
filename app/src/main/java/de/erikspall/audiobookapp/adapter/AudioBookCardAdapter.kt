package de.erikspall.audiobookapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import kotlin.math.roundToInt

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */
class AudioBookCardAdapter (
    private val context: Context?,
    private val layout: Int
): ListAdapter<AudiobookWithAuthor, AudioBookCardAdapter.AudiobookViewHolder>(AUDIOBOOKS_COMPARATOR){

    /**
     * Init view elements
     */
    abstract class AudiobookViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(audiobookWithAuthor: AudiobookWithAuthor, context: Context?)
    }

    class GridCardViewHolder(view: View): AudiobookViewHolder(view) {
        // Declare and init all of the list item UI components
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_progress_indicator: LinearProgressIndicator = view!!.findViewById(R.id.book_progress_indicator)
        val playButton: FloatingActionButton = view.findViewById(R.id.play_button)

        override fun bind(audiobookWithAuthor: AudiobookWithAuthor, context: Context?) {
            Glide.with(context!!)
                .load(audiobookWithAuthor.audiobook.coverUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_image)
                .into(book_image)
            book_title.text = audiobookWithAuthor.audiobook.title
            book_progress.text = context?.resources?.getString(R.string.progress_text_view, ((audiobookWithAuthor.audiobook.position / audiobookWithAuthor.audiobook.duration)*100.0).roundToInt().toString())
            book_progress_indicator.setProgress(((audiobookWithAuthor.audiobook.position / audiobookWithAuthor.audiobook.duration)*100.0).roundToInt(), false)


        }

        companion object {
            fun create(parent: ViewGroup): GridCardViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.grid_list_item, parent, false)
                return GridCardViewHolder(view)
            }
        }
    }

    class ListViewHolder(view: View): AudiobookViewHolder(view) {
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_author: TextView = view!!.findViewById(R.id.book_author)
        val book_duration: TextView = view!!.findViewById(R.id.book_duration)

        override fun bind(audiobookWithAuthor: AudiobookWithAuthor, context: Context?) {
            Glide.with(context!!)
                .load(audiobookWithAuthor.audiobook.coverUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_image)
                .into(book_image)
            book_title.text = audiobookWithAuthor.audiobook.title
            book_progress.text = context?.resources?.getString(R.string.progress_text_view, ((audiobookWithAuthor.audiobook.position / audiobookWithAuthor.audiobook.duration)*100.0).roundToInt().toString())
            book_author.text = audiobookWithAuthor.author.toString()
            book_duration.text = audiobookWithAuthor.audiobook.duration.toString()
        }

        companion object {
            fun create(parent: ViewGroup): ListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
                return ListViewHolder(view)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiobookViewHolder {
        //  Use a conditional to determine the layout type and set it accordingly.
        //  if the layout variable is Layout.GRID the grid list item should be used. Otherwise the
        //  the vertical/horizontal list item should be used.
        return when(layout){
            Layout.LIST -> ListViewHolder.create(parent)
            else -> GridCardViewHolder.create(parent)
        }
    }

    /*override fun getItemCount(): Int {
        return data.size
    }*/

    override fun onBindViewHolder(holder: AudiobookViewHolder, position: Int) {
        /*
        1. Get data at current position
        2. Set image resource for current Book
        3. Set title for current book
        4. Set author for current book
        5. Set progress for current book
         */

        val current = getItem(position)
        holder.bind(current, context)

    }
    companion object {
        private val AUDIOBOOKS_COMPARATOR = object : DiffUtil.ItemCallback<AudiobookWithAuthor>() {
            override fun areItemsTheSame(oldItem: AudiobookWithAuthor, newItem: AudiobookWithAuthor): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: AudiobookWithAuthor, newItem: AudiobookWithAuthor): Boolean {
                return oldItem == newItem
            }
        }
    }
}