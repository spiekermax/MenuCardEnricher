package de.unihannover.hci.menudetector.fragments.menu.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.models.Dish

class MenuItemAdapter(private val dataSet: List<Dish>) :
    RecyclerView.Adapter<MenuItemAdapter.ViewHolder>() {

    var itemClickListener: ((Dish) -> Unit)? = null
    var addToOrdersClickListener: ((TextView, Dish) -> Unit)? = null
    var removeFromOrdersClickListener: ((TextView, Dish) -> Unit)? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val textViewTitle: TextView
        val textViewPrice: TextView
        private val dishCounter: TextView
        private val addToOrdersButton: ImageButton
        private val removeFromOrdersButton: ImageButton

        init {

            textViewTitle = view.findViewById(R.id.dish_title)
            textViewPrice = view.findViewById(R.id.dish_price)
            dishCounter = view.findViewById(R.id.counter)
            addToOrdersButton = view.findViewById(R.id.add_to_orders)
            removeFromOrdersButton = view.findViewById(R.id.remove_from_orders)

            view.setOnClickListener {
                itemClickListener?.invoke(dataSet[adapterPosition])
            }

            addToOrdersButton.setOnClickListener {
                addToOrdersClickListener?.invoke(dishCounter, dataSet[adapterPosition])
            }

            removeFromOrdersButton.setOnClickListener {
                removeFromOrdersClickListener?.invoke(dishCounter, dataSet[adapterPosition])

            }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val adapterLayout = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_menuitem, viewGroup, false)

        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val dish: Dish = dataSet[position]
        val price = dish.price.toString() + "€"
        viewHolder.textViewTitle.text = dish.name
        viewHolder.textViewPrice.text = price
    }

    override fun getItemCount() = dataSet.size

}