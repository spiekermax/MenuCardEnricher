package de.unihannover.hci.menudetector.fragments.dish

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.net.URL
import kotlin.math.abs

class DetailsRetrieval {

    companion object {

        private fun parseGET(urlSpec: String, pattern: String): String? {
            val url = URL(urlSpec)

            val responseText = url.readText()

            val match: String? = pattern
                .toRegex()
                .find(responseText)
                ?.value

            return match
        }

        private val client = OkHttpClient()
        private val rateLimiter = RateLimiter()
        private val cache: MutableMap<String, String> = mutableMapOf()

        suspend fun fetchImageUrl(dish: Dish) = withContext(Dispatchers.IO) {
            val query = dish.originalName.trim().lowercase()

            if (cache.containsKey(query)) return@withContext cache[query]

            rateLimiter.acquire().run {
                val request = Request.Builder()
                    .url("https://bing-image-search1.p.rapidapi.com/images/search?q=${query}")
                    .get()
                    .addHeader(
                        "X-RapidAPI-Key",
                        "84e7d71769msh5981438aed63723p1df9f8jsn6ce5f085f781"
                    )
                    .addHeader("X-RapidAPI-Host", "bing-image-search1.p.rapidapi.com")
                    .build()

                Log.d("ASD", "Starting new request");
                val response = client.newCall(request).execute()
                val json = JSONObject(response.body?.string() ?: "null")

                Log.d("ASD", json.toString() ?: "null")

                val url = json.getJSONArray("value").getJSONObject(0).getString("contentUrl")
                cache[query] = url

                return@withContext url
            }
        }

        fun fetchDescription(queryPart: String): String {
            val urlSafeQueryPart: String = java.net.URLEncoder.encode(queryPart.trim(), "utf-8")

            return parseGET(
                "https://de.wikipedia.org/wiki/" + urlSafeQueryPart.replace(
                    """\+""".toRegex(),
                    "_"
                ),
                """<p>((?!<\/p>)(\s|.))+<\/p>(\s*<p>((?!<\/p>)(\s|.))+<\/p>)?"""
            )!!
                .replace(""" {2,}""".toRegex(), " ")
                .replace("""<\/p>""".toRegex(), "\n\n")
                .replace("""<\/?[^>]+>""".toRegex(), "")
                .replace("""\[\/?[^>]+\]""".toRegex(), "")
                .replace("""\(\/?[^>]+\)""".toRegex(), "")
                .replace(""" ([.,:;])""".toRegex(), "$1")
                .trim()
        }

        fun fetchBitmap(queryPart: String): Bitmap {
            val urlSafeQueryPart: String = java.net.URLEncoder.encode(queryPart.trim(), "utf-8")

            val parsedPhotoSource: String = parseGET(
                "https://commons.wikimedia.org/w/index.php?search=" + urlSafeQueryPart + "&title=Special:MediaSearch&go=Go&type=image",
                """<img[^>]+data-src=\"[^"]+\""""
            )!!

            val photoImageSource: String = parsedPhotoSource
                .replace("""^[^"]+\"""".toRegex(), "")
                .replace("""\"(.|\s)*$""".toRegex(), "")

            val `in` = URL(photoImageSource).openStream()

            return BitmapFactory.decodeStream(`in`)
        }

        fun fetch(dish: Dish): DishDetails? {
            var parsedDescription: String
            try {
                parsedDescription = fetchDescription(dish.name)
            } catch (e: Exception) {
                val firstWord: String? = """\s*[^\s_-]+"""
                    .toRegex()
                    .find(dish.name)
                    ?.value

                if (firstWord === null) {
                    throw e
                }

                parsedDescription = fetchDescription(firstWord)
            }

            var parsedBitmap: Bitmap? = null
            try {
                parsedBitmap = fetchBitmap(
                    dish.name
                        .trim()
                        .replace("""^([^\s]+)$""".toRegex(), "$1 Gericht")
                )
            } catch (e: Exception) {
                try {
                    val firstWord: String? = """\s*[^\s_-]+"""
                        .toRegex()
                        .find(dish.name)
                        ?.value

                    if (firstWord === null) {
                        throw e
                    }

                    parsedBitmap = fetchBitmap(firstWord)
                } catch (e: Exception) {
                }
            }

            return DishDetails(
                bitmap = parsedBitmap,
                description = parsedDescription
            );
        }

    }

}

class RateLimiter() {
    var lastTime = 0L
    val semaphore = Semaphore(1)

    suspend fun acquire(): Unit {
        semaphore.acquire()
        try {
            val currentTime = System.currentTimeMillis()
            if (abs(currentTime - lastTime) < 1000) {
                delay(1000)
            }
            lastTime = System.currentTimeMillis()
        } finally {
            semaphore.release()
        }
    }
}