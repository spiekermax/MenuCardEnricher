package de.unihannover.hci.menudetector.adapters

// Android
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish


class RecyclerViewDishAdapter(
    private var dishes: List<Dish> = listOf(),
) : RecyclerView.Adapter<RecyclerViewDishAdapter.ViewHolder>() {

    /* ATTRIBUTES */

    var clickListener: ((Dish) -> Unit)? = null
    var incrementCountListener: ((Dish) -> Unit)? = null
    var decrementCountListener: ((Dish) -> Unit)? = null


    /* VIEW HOLDER */

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView
        val priceTextView: TextView
        val quantityTextView: TextView

        private val incrementCountButton: ImageButton
        private val decrementCountButton: ImageButton

        init {
            titleTextView = view.findViewById(R.id.text_title)
            priceTextView = view.findViewById(R.id.text_price)
            quantityTextView = view.findViewById(R.id.text_quantity)

            incrementCountButton = view.findViewById(R.id.button_increment_count)
            decrementCountButton = view.findViewById(R.id.button_decrement_count)

            view.setOnClickListener {
                clickListener?.invoke(dishes[adapterPosition])
            }

            incrementCountButton.setOnClickListener {
                incrementCountListener?.invoke(dishes[adapterPosition])
            }

            decrementCountButton.setOnClickListener {
                decrementCountListener?.invoke(dishes[adapterPosition])
            }
        }
    }


    /* LIFECYCLE */

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.layout_dish_item, viewGroup, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dish: Dish = dishes[position]

        val name = dish.name
        val price = "${dish.price}€"
        val quantity = "${dish.quantity}"

        viewHolder.titleTextView.text = name
        viewHolder.priceTextView.text = price
        viewHolder.quantityTextView.text = quantity
    }


    /* METHODS */

    override fun getItemCount() = dishes.size

    fun updateItems(newDishes: List<Dish>) {
        dishes = newDishes
        notifyDataSetChanged()
    }

}