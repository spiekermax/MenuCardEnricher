package de.unihannover.hci.menudetector.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.hci.menudetector.models.OrderItem
import de.unihannover.hci.menudetector.R

class OrderAdapter(private val orderitems:MutableList<OrderItem>):
    RecyclerView.Adapter<OrderViewHolder>() {
    lateinit var view: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        this.view = view
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
      holder.dish_name.text = orderitems[position].dish.name
        holder.dish_price.text = orderitems[position].dish.price.toString()+"€"
        holder.dish_qty.setText("x"+ orderitems[position].qty.toString())
        holder.del_button.setOnClickListener{
            orderitems.removeAt(position)
            notifyItemRemoved(holder.adapterPosition)
        }


        holder.dish_qty.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
             val x = s.toString().toInt();
                orderitems[holder.adapterPosition].qty = x
                var total = 0.0
                for (orderitem in orderitems)
                {
                    total+= (orderitem.qty * orderitem.dish.price)
                }
            }
            override fun beforeTextChanged(s: CharSequence, start: Int,
                                           count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int,
                                       before: Int, count: Int) {
            }
        })

    }

    override fun getItemCount(): Int {
        return  orderitems.size
    }
}