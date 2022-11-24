package de.unihannover.hci.menudetector.fragments


import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.unihannover.hci.menudetector.R
import de.unihannover.hci.menudetector.adapters.OrderAdapter
import de.unihannover.hci.menudetector.models.Dish
import de.unihannover.hci.menudetector.models.DishDetail
import de.unihannover.hci.menudetector.models.Order
import de.unihannover.hci.menudetector.models.OrderItem
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel
import java.nio.ByteBuffer


/**
 * TODO:
 * - Total sum sticky footer
 * - Text-to-speech
 */
class OrderFragment : Fragment(R.layout.fragment_order) {

    private val viewModel by activityViewModels<MainActivityViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*
        //val orderListView = view.findViewById<ListView>(R.id.order_listview)
        val dish1 = Dish("Flafel", 55.0, DishDetail("Homos", null, null, null))
        val dish2 = Dish("Shish", 100.0, DishDetail("Chicken", null, null, null))
        val dish3 = Dish("Yalanji", 25.0, DishDetail("Grapes Leaf", null, null, null))
        val dish4 = Dish("Shawrma", 40.0, DishDetail("chicken Wrap", null, null, null))
        val dish5 = Dish("kobby", 50.0, DishDetail("dough with meat", null, null, null))
        val orderitem1 = OrderItem(dish1, 1)
        val orderitem2 = OrderItem(dish2, 1)
        val orderitem3 = OrderItem(dish3, 3)
        val orderitem4 = OrderItem(dish4, 1)
        val orderitem5 = OrderItem(dish5, 1)
        val orderItems = mutableListOf<OrderItem>(
            orderitem1,orderitem2,orderitem3, orderitem4, orderitem5
        )
        val order = Order(orderItems)
                 */
        //val orderDishesTitles = orderItems.map { it.dish.name }.toTypedArray()

        val orderAdapter = OrderAdapter(viewModel.state.order!!.dishes.toMutableList())
        val orderItemsView = view.findViewById<RecyclerView>(R.id.orderItemsView)
        orderItemsView.layoutManager = LinearLayoutManager(requireContext())
        orderItemsView.adapter = orderAdapter
        //ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, orderDishesTitles)
        //orderListView.adapter = listAdapter


        /*  orderListView.setOnItemClickListener { parent, view, position, id ->
              val movieTitle = listAdapter.getItemAtPosition(position)
              //Toast.makeText(this@ OrderFragment, movieTitle, Toast.LENGTH_SHORT).show()
          }*/


    }
}

