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

        fun fetch(dish: Dish): DishDetails? {
            val urlSafeName: String = java.net.URLEncoder.encode(dish.name, "utf-8")

            var parsedDescription: String = parseGET(
                "https://de.wikipedia.org/wiki/" + urlSafeName.replace("""\+""".toRegex(), "_"),
                """<p>((?!<\/p>)(\s|.))+<\/p>"""
            )!!

            var parsedBitmap: Bitmap? = null
            try {
                val parsedPhotoSource: String = parseGET(
                    "https://commons.wikimedia.org/w/index.php?search=" + urlSafeName + "&title=Special:MediaSearch&go=Go&type=image",
                    """<img[^>]+data-src=\"[^"]+\""""
                )!!

                val photoImageSource: String = parsedPhotoSource
                    .replace("""^[^"]+\"""".toRegex(), "")
                    .replace("""\"(.|\s)*$""".toRegex(), "")

                println(photoImageSource)
                val `in` = URL(photoImageSource).openStream()
                parsedBitmap = BitmapFactory.decodeStream(`in`)
            } catch(e: Exception) {}

            return DishDetails(
                bitmap = parsedBitmap,
                description = parsedDescription
                    .replace("""<\/?[^>]+>""".toRegex(), "")
                    .replace("""\[\/?[^>]+\]""".toRegex(), "")
                    .replace("""\(\/?[^>]+\)""".toRegex(), "")
            );
        }

    }

}