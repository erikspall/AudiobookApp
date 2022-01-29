package de.erikspall.audiobookapp.adapter

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.uamp.MediaItemTree
import de.erikspall.audiobookapp.uamp.PlaybackService
import kotlin.math.roundToInt

/**
 * Adapter to inflate the appropriate list item layout and populate the view with information
 * from the appropriate data source
 */
@androidx.media3.common.util.UnstableApi
class AudioBookCardAdapter (
    private val context: Context?,
    private val activity: Activity,
    private val layout: Int
): ListAdapter<AudiobookWithAuthor, AudioBookCardAdapter.AudiobookViewHolder>(AUDIOBOOKS_COMPARATOR){



    /**
     * Init view elements
     */
    abstract class AudiobookViewHolder(view: View): RecyclerView.ViewHolder(view) {
        abstract fun bind(audiobookWithAuthor: AudiobookWithAuthor)
    }

    class GridCardViewHolder(
        view: View,
        private val context: Context?,
        private val activity: Activity
    ): AudiobookViewHolder(view) {
        private lateinit var controllerFuture: ListenableFuture<MediaController>
        private val controller: MediaController?
            get() = if (controllerFuture.isDone) controllerFuture.get() else null

        private lateinit var browserFuture: ListenableFuture<MediaBrowser>
        private val browser: MediaBrowser?
            get() = if (browserFuture.isDone) browserFuture.get() else null

        init {
            val sessionToken = SessionToken(context!!, ComponentName(activity, PlaybackService::class.java))
            controllerFuture = MediaController.Builder(
                context,
                sessionToken
            ).buildAsync()
            browserFuture = MediaBrowser.Builder(
                context,
                sessionToken
            ).buildAsync()

            controllerFuture.addListener(
                { /* Do stuff when controller is done */ },
                MoreExecutors.directExecutor()
            )
            browserFuture.addListener(
                { /* Do stuff when browser is done */ },
                MoreExecutors.directExecutor()
            )
        }

        // Declare and init all of the list item UI components
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_progress_indicator: LinearProgressIndicator = view!!.findViewById(R.id.book_progress_indicator)
        val playButton: FloatingActionButton = view.findViewById(R.id.play_button)

        override fun bind(audiobookWithAuthor: AudiobookWithAuthor) {
            Glide.with(context!!)
                .load(audiobookWithAuthor.audiobook.coverUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_image)
                .into(book_image)
            book_title.text = audiobookWithAuthor.audiobook.title
            book_progress.text = context?.resources?.getString(R.string.progress_text_view, ((audiobookWithAuthor.audiobook.position / audiobookWithAuthor.audiobook.duration)*100.0).roundToInt().toString())
            book_progress_indicator.setProgress(((audiobookWithAuthor.audiobook.position / audiobookWithAuthor.audiobook.duration)*100.0).roundToInt(), false)

            playButton.setOnClickListener {
                val mediaItem = MediaItemTree.getItemFromTitle(audiobookWithAuthor.audiobook.title)
                if (mediaItem == null)
                    Log.e("Playback", "MediaItem with Title: \"" + audiobookWithAuthor.audiobook.title + "\" not found")
                else {
                    controller?.setMediaItem(mediaItem)
                    controller?.prepare()
                    controller?.playWhenReady = true
                }
            }
        }



        companion object {
            fun create(parent: ViewGroup, context: Context?, activity: Activity): GridCardViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.grid_list_item, parent, false)
                return GridCardViewHolder(view, context, activity)
            }
        }
    }

    class ListViewHolder(
        view: View,
        private val context: Context?
    ): AudiobookViewHolder(view) {
        val book_image: ImageView = view!!.findViewById(R.id.book_image)
        val book_title: TextView = view!!.findViewById(R.id.book_title)
        val book_progress: TextView = view!!.findViewById(R.id.book_progress)
        val book_author: TextView = view!!.findViewById(R.id.book_author)
        val book_duration: TextView = view!!.findViewById(R.id.book_duration)

        override fun bind(audiobookWithAuthor: AudiobookWithAuthor) {
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
        return when(layout){
            Layout.LIST -> ListViewHolder.create(parent, context)
            else -> GridCardViewHolder.create(parent, context, activity)
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
        holder.bind(current)

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