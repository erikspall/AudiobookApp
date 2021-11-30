package de.erikspall.audiobookapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.dummy.DummyDataSource
import de.erikspall.audiobookapp.data.dummy.model.DummyAttribute

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */
class AudioBookCardAdapter (
    private val context: Context?,
    private val layout: Int
): RecyclerView.Adapter<AudioBookCardAdapter.AudioBookCardViewHolder>(){
    // Initialize the dummy data for testing
    val data = DummyDataSource.DUMMY_AUDIOBOOKS

    /**
     * Init view elements
     */
    abstract class AudioBookCardViewHolder(view: View?): RecyclerView.ViewHolder(view!!) {

    }

    class GridCardViewHolder(view: View?): AudioBookCardViewHolder(view) {
        // Declare and init all of the list item UI components
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_progress_indicator: LinearProgressIndicator = view!!.findViewById(R.id.book_progress_indicator)
    }

    class ListViewHolder(view: View?): AudioBookCardViewHolder(view) {
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_author: TextView = view!!.findViewById(R.id.book_author)
        val book_duration: TextView = view!!.findViewById(R.id.book_duration)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioBookCardViewHolder {
        //  Use a conditional to determine the layout type and set it accordingly.
        //  if the layout variable is Layout.GRID the grid list item should be used. Otherwise the
        //  the vertical/horizontal list item should be used.
        val layoutToUse = when (layout){
            Layout.GRID -> R.layout.grid_list_item
            else -> R.layout.list_item
        }
        //  Inflate the layout
        val adapterLayout = LayoutInflater.from(parent.context).inflate(layoutToUse, parent, false)

        return when(layout){
            Layout.LIST -> ListViewHolder(adapterLayout)
            else -> GridCardViewHolder(adapterLayout)
        }
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



        // Shared among both layouts

        val authors = item.chipAttributes[DummyAttribute.AUTHOR]

        when (layout) {
            Layout.LIST -> { // If it's list
                holder as ListViewHolder



                holder.book_image.setImageResource(item.imageResourceId)
                holder.book_title.text = item.title
                holder.book_progress.text = resources?.getString(R.string.progress_text_view_simple, item.progress.toString())
                holder.book_duration.text = "00:00:00" // TODO: Obviously
                if (authors != null)
                    holder.book_author.text = authors[0] // only display first

            }
            else -> { // Grid
                holder as GridCardViewHolder

                holder.book_image.setImageResource(item.imageResourceId)
                holder.book_title.text = item.title
                holder.book_progress.text = resources?.getString(R.string.progress_text_view, item.progress.toString())
                holder.book_progress_indicator.setProgress(item.progress, false)

            }
        }




    }

}