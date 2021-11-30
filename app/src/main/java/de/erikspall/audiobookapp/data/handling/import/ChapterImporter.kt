package de.erikspall.audiobookapp.data.handling.import

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import de.erikspall.audiobookapp.data.model.Chapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Returns list of all chapters in metadata of given
 */
class ChapterImporter(private val context: Context, private val contentUri: Uri): Importer<Chapter> {
    operator fun JSONArray.iterator(): Iterator<JSONObject>
            = (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()



    override fun getAll(): List<Chapter> {
        // Make Uri usable for ffmpeg kit
        val bookPath = FFmpegKitConfig.getSafParameterForRead(context, contentUri)
        // Setup ffprobe command to only retrieve chapters
        val ffprobeCommand = "-hide_banner -loglevel 0 -show_chapters -print_format json " + bookPath
        // Retrieve chapters
        return parseJsonToChapters(FFprobeKit.execute(ffprobeCommand).output)
    }

    suspend fun getAllAsync(): List<Chapter> {
        return withContext(Dispatchers.IO){
            getAll()
        }
    }

    private fun parseJsonToChapters(jsonString: String): List<Chapter>{
        val chapters: MutableList<Chapter> = mutableListOf()
        if (jsonString != null && !jsonString.equals("")){
            val jsonChapterArray = JSONObject(jsonString).getJSONArray("chapters")

            for (c in jsonChapterArray){
                chapters.add(
                    Chapter(
                        c.getInt("id"),
                        c.getString("time_base"),
                        c.getLong("start"),
                        c.getString("start_time"),
                        c.getLong("end"),
                        c.getString("end_time"),
                        c.getJSONObject("tags").getString("title")
                    )
                )
            }

        } else {
            Log.d("[PROBE]", "No chapters found!")
        }
        return chapters.toList()
    }
}