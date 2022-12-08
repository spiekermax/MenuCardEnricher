package de.unihannover.hci.menudetector.fragments.dish

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetails
import java.net.URL

class DetailsRetrieval {

    companion object {

        private fun conductGET(urlSpec: String): String {
            var url = URL(urlSpec)

            return url.readText()
        }

        public fun fetch(dish: Dish): DishDetails {
            // TODO: Enhance wiki data parser(s)
            val wikiEntry: CharSequence = conductGET("https://de.wikipedia.org/wiki/" + dish.name)
            var fetchedDescription: String? = """<p>((?!<\/p>)(\s|.))+<\/p>""".toRegex()
                .find(wikiEntry)?.value   // TODO: Improve precision
            if (fetchedDescription != null) {
                fetchedDescription = fetchedDescription
                    .replace("""<\/?[^>]+>""".toRegex(), "")
            }

            val wikiMedia: CharSequence =
                conductGET("https://commons.wikimedia.org/w/index.php?search=" + dish.name + "&title=Special:MediaSearch&go=Go&type=image")
            //var fetchedImageSource: String? = """<img class=\"sd-image\"((?!src=\")(\s|.))+src=\"[^"]+\"""".toRegex()
            //                                  .find(wikiMedia)?.value   // TODO: Improve precision
            var fetchedImageSource =
                "https://upload.wikimedia.org/wikipedia/commons/6/6f/5aday_spinach.jpg"    // MOCK

            var fetchedBitmap: Bitmap? = null
            if (fetchedImageSource != null) {
                val `in` = URL(fetchedImageSource).openStream()
                fetchedBitmap = BitmapFactory.decodeStream(`in`)
            }

            return DishDetails(
                bitmap = fetchedBitmap,
                description = fetchedDescription
            );
        }

    }

}