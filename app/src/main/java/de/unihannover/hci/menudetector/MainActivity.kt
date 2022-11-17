package de.unihannover.hci.menudetector

// Android
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

// Google Material
import com.google.android.material.floatingactionbutton.FloatingActionButton

// Internal dependencies
import de.unihannover.hci.menudetector.viewmodels.MainActivityViewModel


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel: MainActivityViewModel by viewModels()

        val counterLabel: TextView = findViewById(R.id.counter_label)
        val counterButton: FloatingActionButton = findViewById(R.id.counter_button)

        viewModel.exampleCounterChanges.observe(this) {
            counterLabel.text = it.toString()
        }
        counterButton.setOnClickListener {
            viewModel.incrementExampleCounter()
        }
    }
}