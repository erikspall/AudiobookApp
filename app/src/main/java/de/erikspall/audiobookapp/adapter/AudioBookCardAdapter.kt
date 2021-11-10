package de.erikspall.audiobookapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.size
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.data.dummy.DummyDataSource
import de.erikspall.audiobookapp.model.Attribute

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */
class AudioBookCardAdapter (
    private val context: Context?,
): RecyclerView.Adapter<AudioBookCardAdapter.AudioBookCardViewHolder>(){
    // Initialize the dummy data for testing
    val data = DummyDataSource.audioBooks

    /**
     * Init view elements
     */
    class AudioBookCardViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {
        // Declare and init all of the list item UI components
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_chips: ChipGroup = view!!.findViewById(R.id.chip_group)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_progress_indicator: LinearProgressIndicator = view!!.findViewById(R.id.book_progress_indicator)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioBookCardViewHolder {
        /* ~Change layout here~ */
        val layoutToUse = R.layout.grid_list_item

        val adapterLayout = LayoutInflater.from(parent.context).inflate(layoutToUse, parent, false)
        return AudioBookCardViewHolder(adapterLayout)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: AudioBookCardViewHolder, position: Int) {
        /*
        1. Get data at current position
        2. Set image resource for current Book
        3. Set title for current book
        4. Set author for current book
        5. Set progress for current book
         */
        val resources = context?.resources
        val item = data[position]

        holder.book_image.setImageResource(item.imageResourceId)
        holder.book_title.text = item.title
        holder.book_progress.text = resources?.getString(R.string.progress_text_view, item.progress.toString())
        holder.book_progress_indicator.setProgress(item.progress, false)



        holder.book_chips.removeAllViews()
        /* Add all Info as Chips here */
        val authors = item.chipAttributes[Attribute.AUTHOR]
        if (authors != null) {
            for (author in authors!!) {
                createAndAddChip(author, holder.book_chips)
            }
        }


        val genres = item.chipAttributes[Attribute.GENRE]
        if (genres != null) {
            for (genre in genres!!) {
                createAndAddChip(genre, holder.book_chips)
            }
        }

    }

    private fun createAndAddChip(text: String, group: ChipGroup){
        val chip = Chip(this.context)
        chip.text = text
        chip.isCheckable = false
        chip.isClickable = false
        group.addView(chip)
    }
}