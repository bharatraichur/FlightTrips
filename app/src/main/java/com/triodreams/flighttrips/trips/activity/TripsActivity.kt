package com.triodreams.flighttrips.trips.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.triodreams.flighttrips.R

class TripsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_trips)

        title = getString(R.string.trips)
    }
}