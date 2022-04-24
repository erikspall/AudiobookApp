package de.erikspall.audiobookapp.data.source.local.disk.import

import android.content.Context
import android.net.Uri
import android.util.Log
import com.arthenica.ffmpegkit.FFmpegKitConfig
import com.arthenica.ffmpegkit.FFprobeKit
import de.erikspall.audiobookapp.data.source.local.disk.ChapterMetadata
import org.json.JSONArray
import org.json.JSONObject

class ChapterImporter(
    private val context: Context,
    private val contentUri: Uri
) : Importer<ChapterMetadata> {
    operator fun JSONArray.iterator(): Iterator<JSONObject> =
        (0 until length()).asSequence().map { get(it) as JSONObject }.iterator()


    /**
     * Rather use getChaptersAsJsonString()!!!
     */
    override fun getAll(): List<ChapterMetadata> {
        // Make Uri usable for ffmpeg kit
        val bookPath = FFmpegKitConfig.getSafParameterForRead(context, contentUri)
        // Setup ffprobe command to only retrieve chapters
        val ffprobeCommand =
            "-hide_banner -loglevel 0 -show_chapters -print_format json " + bookPath
        return parseJsonToMetadata(FFprobeKit.execute(ffprobeCommand).output)
    }

    private fun parseJsonToMetadata(jsonString: String): List<ChapterMetadata> {
        val chapters = mutableListOf<ChapterMetadata>()
        if (jsonString != "") {

            val jsonChapterArray = JSONObject(jsonString).getJSONArray("chapters")

            for (c in jsonChapterArray) {
                chapters +=
                    ChapterMetadata(
                        time_base = c.getString("time_base"),
                        start = c.getDouble("start"),
                        start_time = c.getString("start_time"),
                        end = c.getDouble("end"),
                        end_time = c.getString("end_time"),
                        title = c.getJSONObject("tags").getString("title")
                    )
            }
        } else {
            Log.d("[PROBE]", "No chapters found!")
            return emptyList()
        }
        return chapters.toList()
    }

}