package de.unihannover.hci.menudetector.adapters

// Android
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

// Glide
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

// Internal dependencies
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish


class RecyclerViewDishAdapter(
    dishes: List<Dish> = listOf(),
    private val showQuantity: Boolean = true,
    private val isQuantityEditable: Boolean = true,
    private val showImage: Boolean = true,
    private val isDishModifiable: Boolean = false,
) : RecyclerView.Adapter<RecyclerViewDishAdapter.ViewHolder>() {

    /* ATTRIBUTES */

    var clickListener: ((Dish) -> Unit)? = null
    var incrementCountListener: ((Dish) -> Unit)? = null
    var decrementCountListener: ((Dish) -> Unit)? = null
    var sayItListener: ((Dish) -> Unit)? = null
    var deleteDishListener: ((Dish) -> Unit)? = null
    var editDishListener: ((Dish) -> Unit)? = null

    private val differ = AsyncListDiffer(this, object : DiffUtil.ItemCallback<Dish>() {
        override fun areItemsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Dish, newItem: Dish): Boolean {
            return oldItem == newItem
        }
    })


    /* VIEW HOLDER */

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView
        val priceTextView: TextView
        val quantityTextView: TextView

        val previewImageView: ImageView
        private val incrementCountButton: ImageButton
        private val decrementCountButton: ImageButton
        private val sayItButton: ImageButton
        private val deleteDishButton: ImageButton
        private val editDishButton: ImageButton

        init {
            titleTextView = view.findViewById(R.id.text_title)
            priceTextView = view.findViewById(R.id.text_price)
            quantityTextView = view.findViewById(R.id.text_quantity)

            previewImageView = view.findViewById(R.id.preview_image)

            incrementCountButton = view.findViewById(R.id.button_increment_count)
            decrementCountButton = view.findViewById(R.id.button_decrement_count)
            sayItButton = view.findViewById(R.id.button_say)
            deleteDishButton = view.findViewById(R.id.delete_dish)
            editDishButton = view.findViewById(R.id.edit_dish)

            if (!showQuantity) {
                quantityTextView.visibility = View.GONE
                incrementCountButton.visibility = View.GONE
                decrementCountButton.visibility = View.GONE
            } else {
                quantityTextView.visibility = View.VISIBLE
            }

            if (!isQuantityEditable) {
                incrementCountButton.visibility = View.GONE
                decrementCountButton.visibility = View.GONE
            } else {
                incrementCountButton.visibility = View.VISIBLE
                decrementCountButton.visibility = View.VISIBLE
            }

            if (!showImage) {
                previewImageView.visibility = View.GONE
            } else {
                previewImageView.visibility = View.VISIBLE
            }

            if (isDishModifiable) {
                sayItButton.visibility = View.GONE
                deleteDishButton.visibility = View.VISIBLE
                editDishButton.visibility = View.VISIBLE
            } else {
                sayItButton.visibility = View.VISIBLE
                deleteDishButton.visibility = View.GONE
                editDishButton.visibility = View.GONE
            }

            view.setOnClickListener {
                clickListener?.invoke(differ.currentList[adapterPosition])
            }

            incrementCountButton.setOnClickListener {
                incrementCountListener?.invoke(differ.currentList[adapterPosition])
            }

            decrementCountButton.setOnClickListener {
                decrementCountListener?.invoke(differ.currentList[adapterPosition])
            }

            sayItButton.setOnClickListener {
                sayItListener?.invoke(differ.currentList[adapterPosition])
            }

            deleteDishButton.setOnClickListener {
                deleteDishListener?.invoke(differ.currentList[adapterPosition])
            }

            editDishButton.setOnClickListener {
                editDishListener?.invoke(differ.currentList[adapterPosition])
            }
        }
    }


    /* LIFECYCLE */

    init {
        differ.submitList(dishes)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.layout_dish_item, viewGroup, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dish: Dish = differ.currentList[position]

        val name = dish.name
        val price = "${dish.price}€"
        val quantity = "${dish.quantity}"

        viewHolder.titleTextView.text = name
        viewHolder.priceTextView.text = price
        viewHolder.quantityTextView.text = quantity

        Glide.with(viewHolder.itemView.context)
            .load("https://pkmlimo.depok.go.id/assets/images/default.jpg")
            .placeholder(R.drawable.placeholder)
            .override(225, 225)
            .centerCrop()
            .transform(RoundedCorners(20))
            .into(viewHolder.previewImageView)
    }


    /* METHODS */

    override fun getItemCount() = differ.currentList.size

    fun updateItems(newDishes: List<Dish>) {
        differ.submitList(newDishes)
    }

}