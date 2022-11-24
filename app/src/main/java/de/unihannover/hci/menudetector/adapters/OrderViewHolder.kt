package de.unihannover.hci.menudetector.adapters

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.view.menu.MenuView.ItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.hci.menudetector.R

class OrderViewHolder(private val itemView:View):RecyclerView.ViewHolder(itemView) {
    val dish_name:TextView = itemView.findViewById(R.id.dish_name)
    val dish_price:TextView = itemView.findViewById(R.id.dish_price)
    val dish_qty:EditText = itemView.findViewById(R.id.dish_qty)
    val del_button:Button = itemView.findViewById(R.id.delete_dish)
    val sayit_button:Button = itemView.findViewById(R.id.say_it)
    val disch_img:ImageView = itemView.findViewById(R.id.dish_pic)




}