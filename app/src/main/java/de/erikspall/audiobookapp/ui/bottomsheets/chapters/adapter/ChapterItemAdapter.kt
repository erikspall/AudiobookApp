package de.erikspall.audiobookapp.ui.bottomsheets.chapters.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.domain.model.Chapter
import de.erikspall.audiobookapp.domain.util.Conversion

class ChapterItemAdapter(
    private val onItemClicked: (Int) -> Unit
): ListAdapter<Chapter, ChapterItemAdapter.ViewHolder>(
    CHAPTER_COMPARATOR
) {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(
        view: View,
        private val context: Context,
        private val clickAtPosition: (Int) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val isPlayingImageView: ImageView = view.findViewById(R.id.image_view_is_playing)
        private val chapterTitleTextView: TextView = view.findViewById(R.id.chapter_title_text_view)
        private val chapterDurationTextView: TextView = view.findViewById(R.id.chapter_duration_text_view)

        init {
            itemView.setOnClickListener {
                clickAtPosition(absoluteAdapterPosition)
            }
        }

        @SuppressLint("UnsafeOptInUsageError")
        fun bind(chapter: Chapter) {
            chapterTitleTextView.text = chapter.title
            chapterDurationTextView.text = Conversion.millisToStr(((chapter.end_time.toDouble() - chapter.start_time.toDouble())*1000).toLong())
            if (chapter.isPlaying)
                isPlayingImageView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play))
            else
                isPlayingImageView.setImageDrawable(null)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.chapter_list_item, viewGroup, false)

        return ViewHolder(view, viewGroup.context) { chapterIndex ->
            onItemClicked(chapterIndex)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(getItem(position))
    }

    companion object {
        private val CHAPTER_COMPARATOR = object : DiffUtil.ItemCallback<Chapter>() {
            override fun areItemsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: Chapter, newItem: Chapter): Boolean {
                return (
                        oldItem.isPlaying == newItem.isPlaying &&
                                oldItem.title == newItem.title &&
                                oldItem.end_time == newItem.end_time &&
                                oldItem.start_time == newItem.start_time
                        )
            }
        }
    }
}