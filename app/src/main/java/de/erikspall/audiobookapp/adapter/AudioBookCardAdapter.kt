package de.erikspall.audiobookapp.adapter

import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.progressindicator.LinearProgressIndicator
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.model.Audiobook
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */
class AudioBookCardAdapter (
    private val context: Context?,
    private val layout: Int
): ListAdapter<Audiobook, AudioBookCardAdapter.AudiobookViewHolder>(AUDIOBOOKS_COMPARATOR){

    /**
     * Init view elements
     */
    abstract class AudiobookViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(audiobook: Audiobook, context: Context?)
    }

    class GridCardViewHolder(view: View): AudiobookViewHolder(view) {
        // Declare and init all of the list item UI components
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_progress_indicator: LinearProgressIndicator = view!!.findViewById(R.id.book_progress_indicator)
        val mmr = MediaMetadataRetriever()

        override fun bind(audiobook: Audiobook, context: Context?) {

                val mainLooper = Looper.getMainLooper()
                GlobalScope.launch {
                    Log.d("Timing", "Setting Datasource...")
                    mmr.setDataSource(context, Uri.parse(audiobook.uri))
                    Log.d("Timing", "Datasource set!")
                    Log.d("Timing", "Getting embeddedPicture...")
                    val cover = mmr.embeddedPicture
                    Log.d("Timing", "embeddedPicture fetched!")
                    Log.d("Timing", "Using glide...")
                    val bitmap = Glide.with(context!!)
                        .asBitmap()
                        .centerCrop()
                        .load(cover)
                        .placeholder(R.drawable.ic_image)
                        .submit()
                        .get()
                    Log.d("Timing", "Glide used!")

                    Handler(mainLooper).post {
                        book_image.setImageBitmap(bitmap)
                    }
                }



            book_title.text = audiobook.title
            book_progress.text = context?.resources?.getString(R.string.progress_text_view, ((audiobook.position / audiobook.duration)*100.0).roundToInt().toString())
            book_progress_indicator.setProgress(((audiobook.position / audiobook.duration)*100.0).roundToInt(), false)
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
        val mmr = MediaMetadataRetriever()

        override fun bind(audiobook: Audiobook, context: Context?) {
            val mainLooper = Looper.getMainLooper()
            GlobalScope.launch {
                Log.d("Timing", "Setting Datasource...")
                mmr.setDataSource(context, Uri.parse(audiobook.uri))
                Log.d("Timing", "Datasource set!")
                Log.d("Timing", "Getting embeddedPicture...")
                val cover = mmr.embeddedPicture
                Log.d("Timing", "embeddedPicture fetched!")
                Log.d("Timing", "Using glide...")
                val bitmap = Glide.with(context!!)
                    .asBitmap()
                    .centerCrop()
                    .placeholder(R.drawable.ic_image)
                    .load(cover)
                    .submit()
                    .get()
                Log.d("Timing", "Glide used!")

                Handler(mainLooper).post {
                    book_image.setImageBitmap(bitmap)
                }
            }

            book_title.text = audiobook.title
            book_progress.text = context?.resources?.getString(R.string.progress_text_view, ((audiobook.position / audiobook.duration)*100.0).roundToInt().toString())
            book_author.text = "Dummy Author"
            book_duration.text = audiobook.duration.toString()
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

        /*val resources = context?.resources
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
        }*/


    }
    companion object {
        private val AUDIOBOOKS_COMPARATOR = object : DiffUtil.ItemCallback<Audiobook>() {
            override fun areItemsTheSame(oldItem: Audiobook, newItem: Audiobook): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Audiobook, newItem: Audiobook): Boolean {
                return (oldItem.uri == newItem.uri)
            }
        }
    }
}