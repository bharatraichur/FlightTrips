package com.triodreams.flighttrips.trips.activity

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.triodreams.flighttrips.R
import com.triodreams.flighttrips.databinding.ActivityTripDetailsBinding
import com.triodreams.flighttrips.trips.models.FlightDataModel
import java.text.SimpleDateFormat

class TripDetailsActivity : AppCompatActivity() {
    private lateinit var layoutTripDetailsBinding: ActivityTripDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutTripDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_trip_details)

        supportActionBar?.let { actionBar ->
            actionBar.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
            actionBar.setCustomView(R.layout.toolbar_title)
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        val flightData = intent.getParcelableExtra<FlightDataModel>("FlightData")

        initFlightDetails(flightData)
    }

    private fun initFlightDetails(flightData: FlightDataModel?) {
        flightData?.let { flightInfo ->
            try {
                layoutTripDetailsBinding.flightDepartureCode = flightInfo.departure_airport
                layoutTripDetailsBinding.flightArrivalCode = flightInfo.arrival_airport
                layoutTripDetailsBinding.flightDepartureCity = flightInfo.departure_city.split(",")[0]
                layoutTripDetailsBinding.flightArrivalCity = flightInfo.arrival_city.split(",")[0]

                val dateTimeParser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")

                val dateFormat = SimpleDateFormat("EEE, dd MMM")
                layoutTripDetailsBinding.flightDepartureDate = dateFormat.format(dateTimeParser.parse(flightInfo.departure_date))
                layoutTripDetailsBinding.flightArrivalDate = dateFormat.format(dateTimeParser.parse(flightInfo.arrival_date))

                val timeFormat = SimpleDateFormat("hh:mm a")
                layoutTripDetailsBinding.flightDepartureTime = timeFormat.format(dateTimeParser.parse(flightInfo.departure_date))
                layoutTripDetailsBinding.flightArrivalTime = timeFormat.format(dateTimeParser.parse(flightInfo.arrival_date))

                layoutTripDetailsBinding.flightAirlineCodeNumber = "Flight : ${flightInfo.airline_code.plus(flightInfo.flight_number)}"
                layoutTripDetailsBinding.flightAirportTerminalNumber = "Terminal : -"
                layoutTripDetailsBinding.flightAirportGateNumber = "Gate : -"
                layoutTripDetailsBinding.flightPlaneSeatNumber = "Seat : -"
            } catch (e: Exception) {
                Log.e("FlightDetails", e.localizedMessage)
                Toast.makeText(applicationContext, "Unable to retrieve Flight details, Please try again later.", Toast.LENGTH_SHORT).show()
            }
        } ?: Toast.makeText(applicationContext, "Unable to retrieve Flight details, Please try again later.", Toast.LENGTH_SHORT).show()
    }
}