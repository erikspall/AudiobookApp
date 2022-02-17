package de.erikspall.audiobookapp.data.handling.import

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import de.erikspall.audiobookapp.data.database.AudiobookRoomDatabase
import de.erikspall.audiobookapp.data.model.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Returns list of all chapters in metadata of given
 */
class ChapterImporter(private val context: Context, private val contentUri: Uri, private val audiobookId: Long): Importer<Chapter> {
    operator fun JSONArray.iterator(): Iterator<JSONObject>
            = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()

    val database: AudiobookRoomDatabase by lazy { AudiobookRoomDatabase.getDatabase(context
    )}

    override fun getAll(): List<Chapter> {
        // Make Uri usable for ffmpeg kit
        val bookPath = FFmpegKitConfig.getSafParameterForRead(context, contentUri)
        // Setup ffprobe command to only retrieve chapters
        val ffprobeCommand = "-hide_banner -loglevel 0 -show_chapters -print_format json " + bookPath
        // Retrieve chapters
        Log.d("Importing Chapters", "Started ffprobe")
        return parseJsonToChapters(FFprobeKit.execute(ffprobeCommand).output)
    }

    suspend fun getAllAsync(): List<Chapter> {
        return withContext(Dispatchers.IO){
            getAll()
        }
    }

    private fun parseJsonToChapters(jsonString: String): List<Chapter>{
        val chapters: MutableList<Chapter> = mutableListOf()
        Log.d("Importing Chapters", "Finished ffprobe")
        Log.d("Importing Chapters", "Started JSON parsing")
        if (jsonString != null && !jsonString.equals("")){
            Log.d("Importing Chapters", "Getting JSON chapter entry")
            val jsonChapterArray = JSONObject(jsonString).getJSONArray("chapters")
            Log.d("Importing Chapters", "Retrieved JSON chapter entry")
            for (c in jsonChapterArray){
                chapters.add(
                    Chapter(
                        audiobookId = audiobookId,
                        time_base = c.getString("time_base"),
                        start = c.getDouble("start"),
                        start_time = c.getString("start_time"),
                        end = c.getDouble("end"),
                        end_time = c.getString("end_time"),
                        title = c.getJSONObject("tags").getString("title")
                    )
                )
                getOrAddChapter(chapters[chapters.size-1])

            }

        } else {
            Log.d("[PROBE]", "No chapters found!")
        }
        return chapters.toList()
    }

    private fun getOrAddChapter(chapter: Chapter): Long{
        if (database.chapterDao().chapterExistsSync(chapter.title, chapter.start, chapter.start_time, chapter.end, chapter.end_time)){
            return database.chapterDao().getChapterSync(chapter.title, chapter.start, chapter.start_time, chapter.end, chapter.end_time).chapterId
        } else {
            return database.chapterDao().insertSync(chapter)
        }
    }
}