package de.erikspall.audiobookapp.adapter.bottomsheets

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.common.MediaItem
import androidx.recyclerview.widget.RecyclerView
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.utils.Conversion

class ChapterItemAdapter(
    private val dataSet: Array<MediaItem>
): RecyclerView.Adapter<ChapterItemAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val isPlayingImageView: ImageView
        val chapterTitleTextView: TextView
        val chapterDurationTextView: TextView

        init {
            // Define click listener for the ViewHolder's View.
            isPlayingImageView = view.findViewById(R.id.image_view_is_playing)
            chapterTitleTextView = view.findViewById(R.id.chapter_title_text_view)
            chapterDurationTextView = view.findViewById(R.id.chapter_duration_text_view)
        }

        @SuppressLint("UnsafeOptInUsageError")
        fun bind(item: MediaItem) {
            Log.d("ChapterSheet", "Binding: ${item.mediaMetadata.title}")
            chapterTitleTextView.text = item.mediaMetadata.title
            chapterDurationTextView.text = Conversion.millisToStr(item.clippingConfiguration.endPositionMs - item.clippingConfiguration.startPositionMs)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.chapter_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
}