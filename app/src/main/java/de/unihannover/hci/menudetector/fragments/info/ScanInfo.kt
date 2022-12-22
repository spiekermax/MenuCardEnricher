package de.unihannover.hci.menudetector.fragments.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton
import de.unihannover.hci.menudetector.R

class ScanInfo: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View= inflater.inflate(R.layout.fragment_info_scan,container,false)

        val closeButton: MaterialButton = rootView.findViewById(R.id.info_scan_close_btn)
        closeButton.setOnClickListener {
            dismiss()
        }
        return rootView
    }


}