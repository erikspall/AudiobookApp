package de.erikspall.audiobookapp.old.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import de.erikspall.audiobookapp.old.utils.Conversion
import kotlin.math.roundToInt

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */
class AudioBookCardAdapter(
    private val context: Context?,
    private val onItemClicked: (AudiobookWithPersons, Int) -> Unit,
    private val layout: Int,
    private val currentlyPlayingPosition: Int
) : ListAdapter<AudiobookWithPersons, AudioBookCardAdapter.AudiobookViewHolder>(AUDIOBOOKS_COMPARATOR) {
    /**
     * Init view elements
     */

    abstract class AudiobookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        abstract fun bind(audiobookWithPersons: AudiobookWithPersons, position: Int)
    }


    class GridCardViewHolder(
        view: View,
        private val context: Context?,
        private val onItemClicked: (AudiobookWithPersons, Int) -> Unit,
        var currentlyPlayingPosition: Int
    ) : AudiobookViewHolder(view) {
        // Declare and init all of the list item UI components
        val book_image: ImageView = view.findViewById(R.id.book_image)
        val book_title: TextView = view.findViewById(R.id.book_title)
        val book_progress: TextView = view.findViewById(R.id.book_progress)
        val book_progress_indicator: LinearProgressIndicator =
            view.findViewById(R.id.book_progress_indicator)
        val playButton: FloatingActionButton = view.findViewById(R.id.play_button)

        override fun bind(audiobookWithPersons: AudiobookWithPersons, position: Int) {
            Glide.with(context!!)
                .load(audiobookWithPersons.audiobook.coverUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_image)
                .into(book_image)
            val progressInPercent =
                ((audiobookWithPersons.audiobook.position.toDouble() / audiobookWithPersons.audiobook.duration) * 100.0).roundToInt()
            // Log.d("AudiobookAdapter", "$audiobookWithAuthor")
            // Log.d("AudiobookAdapter", "Calculated progress: $progressInPercent")
            book_title.text = audiobookWithPersons.audiobook.title
            book_progress.text = context.resources?.getString(
                R.string.progress_text_view,
                progressInPercent.toString()
            )
            book_progress_indicator.setProgress(progressInPercent, true)

            if (currentlyPlayingPosition == position)
                playButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_pause))

            playButton.setOnClickListener {
                onItemClicked(audiobookWithPersons, position)
            }
        }


        companion object {
            fun create(
                parent: ViewGroup,
                context: Context?,
                onItemClicked: (AudiobookWithPersons, Int) -> Unit,
                currentlyPlayingPosition: Int
            ): GridCardViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.grid_list_item, parent, false)
                return GridCardViewHolder(view, context, onItemClicked, currentlyPlayingPosition)
            }
        }
    }

    class ListViewHolder(
        view: View,
        private val context: Context?
    ) : AudiobookViewHolder(view) {
        val book_image: ImageView = view.findViewById(R.id.book_image)
        val book_title: TextView = view.findViewById(R.id.book_title)
        val book_progress: TextView = view.findViewById(R.id.book_progress)
        val book_author: TextView = view.findViewById(R.id.book_author)
        val book_duration: TextView = view.findViewById(R.id.book_duration)

        override fun bind(audiobookWithPersons: AudiobookWithPersons, position: Int) {
            Glide.with(context!!)
                .load(audiobookWithPersons.audiobook.coverUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_image)
                .into(book_image)
            book_title.text = audiobookWithPersons.audiobook.title
            book_progress.text = context.resources?.getString(
                R.string.progress_text_view,
                ((audiobookWithPersons.audiobook.position / audiobookWithPersons.audiobook.duration) * 100.0).roundToInt()
                    .toString()
            )
            book_author.text = audiobookWithPersons.author.toString()
            book_duration.text = Conversion.millisToStr(audiobookWithPersons.audiobook.duration)
        }

        companion object {
            fun create(parent: ViewGroup, context: Context?): ListViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item, parent, false)
                return ListViewHolder(view, context)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudiobookViewHolder {
        //  Use a conditional to determine the layout type and set it accordingly.
        //  if the layout variable is Layout.GRID the grid list item should be used. Otherwise the
        //  the vertical/horizontal list item should be used.
        return when (layout) {
            Layout.LIST -> ListViewHolder.create(parent, context)
            else -> GridCardViewHolder.create(parent, context, onItemClicked, currentlyPlayingPosition)
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
        holder.bind(current, position)
    }

    companion object {
        const val PAYLOAD_COVER = 1
        const val PAYLOAD_POSITION = 2
        const val PAYLOAD_TITLE = 3
        const val PAYLOAD_AUTHOR = 4

        private val AUDIOBOOKS_COMPARATOR = object : DiffUtil.ItemCallback<AudiobookWithPersons>() {
            override fun areItemsTheSame(
                oldItem: AudiobookWithPersons,
                newItem: AudiobookWithPersons
            ): Boolean {
                // Log.d("Diffutil", "areItemsTheSame: ${oldItem === newItem}")
                return oldItem.audiobook.audiobookId == newItem.audiobook.audiobookId
            }

            override fun areContentsTheSame(
                oldItem: AudiobookWithPersons,
                newItem: AudiobookWithPersons
            ): Boolean {
                //Log.d("Diffutil", "areContentsTheSame: ${oldItem == newItem}")
                // Only check the visible diffrence
                return oldItem.audiobook.coverUri == newItem.audiobook.coverUri &&
                        oldItem.audiobook.position == newItem.audiobook.position &&
                        oldItem.audiobook.title == newItem.audiobook.title &&
                        oldItem.audiobook.authorId == newItem.audiobook.authorId
            }

            override fun getChangePayload(
                oldItem: AudiobookWithPersons,
                newItem: AudiobookWithPersons
            ): Any? {
                val changes = HashMap<Int, Any?>()
                if (oldItem.audiobook.coverUri != newItem.audiobook.coverUri)
                    changes[PAYLOAD_COVER] = newItem.audiobook.coverUri
                if (oldItem.audiobook.position != newItem.audiobook.position)
                    changes[PAYLOAD_POSITION] = newItem.audiobook.position
                if (oldItem.audiobook.title != newItem.audiobook.title)
                    changes[PAYLOAD_TITLE] = newItem.audiobook.title
                if (oldItem.audiobook.authorId != newItem.audiobook.authorId)
                    changes[PAYLOAD_AUTHOR] = newItem.audiobook.authorId


                return if (changes.isEmpty()) changes else null
            }
        }
    }
}