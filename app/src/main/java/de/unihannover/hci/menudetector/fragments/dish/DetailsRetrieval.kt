package de.unihannover.hci.menudetector.fragments.dish

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetails
import java.net.URL

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

        fun fetchDescription(queryPart: String): String {
            val urlSafeQueryPart: String = java.net.URLEncoder.encode(queryPart.trim(), "utf-8")

            return parseGET(
                "https://de.wikipedia.org/wiki/" + urlSafeQueryPart.replace("""\+""".toRegex(), "_"),
                """<p>((?!<\/p>)(\s|.))+<\/p>"""
            )!!
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
            } catch(e: Exception) {
                val firstWord: String? = """\s*[^\s_-]+"""
                    .toRegex()
                    .find(dish.name)
                    ?.value

                if(firstWord === null) {
                    throw e
                }

                parsedDescription = fetchDescription(firstWord)
            }

            var parsedBitmap: Bitmap? = null
            try {
                parsedBitmap = fetchBitmap(dish.name
                    .trim()
                    .replace("""^([^\s]+)$""".toRegex(), "$1 Gericht")
                )
            } catch(e: Exception) {
                try {
                    val firstWord: String? = """\s*[^\s_-]+"""
                        .toRegex()
                        .find(dish.name)
                        ?.value

                    if(firstWord === null) {
                        throw e
                    }

                    parsedBitmap = fetchBitmap(firstWord)
                } catch(e: Exception) {}
            }

            return DishDetails(
                bitmap = parsedBitmap,
                description = parsedDescription
                    .replace("""<\/?[^>]+>""".toRegex(), "")
                    .replace("""\[\/?[^>]+\]""".toRegex(), "")
                    .replace("""\(\/?[^>]+\)""".toRegex(), "")
                    .replace(""" {2,}""".toRegex(), " ")
                    .replace(""" ([.,:;])""".toRegex(), "$1")
            );
        }

    }

}