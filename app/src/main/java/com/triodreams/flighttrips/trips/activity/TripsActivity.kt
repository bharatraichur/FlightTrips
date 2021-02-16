package com.triodreams.flighttrips.trips.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.triodreams.flighttrips.R
import com.triodreams.flighttrips.databinding.ActivityTripsBinding
import com.triodreams.flighttrips.flightHeaderItem
import com.triodreams.flighttrips.flightListItem
import com.triodreams.flighttrips.trips.models.FlightDataModel
import com.triodreams.flighttrips.trips.network.FlightDataService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.SimpleDateFormat

class TripsActivity : AppCompatActivity() {
    private lateinit var layoutTripsBinding: ActivityTripsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutTripsBinding = DataBindingUtil.setContentView(this, R.layout.activity_trips)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_title)

        layoutTripsBinding.recyclerView.layoutManager = StickyHeaderLinearLayoutManager(this)

        layoutTripsBinding.progressBar.visibility = View.VISIBLE

        initFlightData()
    }

    private fun initFlightData() {
        val service = Retrofit.Builder()
            .baseUrl(getString(R.string.flight_data_base_url))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val flightDataAPI = service.create(FlightDataService::class.java)

        val flightDataCall = flightDataAPI.getFlightData("media", "81d93056-9c7f-451d-94b6-3e88eb6fa9ad")

        flightDataCall.enqueue(object: Callback<List<FlightDataModel>> {
            override fun onFailure(call: Call<List<FlightDataModel>>, t: Throwable) {
                Log.e("Error", t.localizedMessage, t)
                layoutTripsBinding.progressBar.visibility = View.GONE
                Toast.makeText(applicationContext, "Unable to retrieve Flight data, Please try again later.", Toast.LENGTH_SHORT).show()
            }

            override fun onResponse(call: Call<List<FlightDataModel>>, response: Response<List<FlightDataModel>>) {
                if (response.isSuccessful) {
                    val flightDataList = response.body()
                    flightDataList?.let { flightDataListInfo ->
                        layoutTripsBinding.recyclerView.withModels {
                            flightDataListInfo.forEachIndexed { pos, _ ->
                                flightHeaderItem {
                                    try {
                                        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                        val outFormat = SimpleDateFormat("EEE, dd MMMM")
                                        val departureDate = outFormat.format(parser.parse(flightDataListInfo[pos].departure_date))
                                        id(flightDataListInfo[pos].id)
                                        date(departureDate)
                                    } catch (e: Exception) {
                                        Log.e("FlightData", e.localizedMessage)
                                    }
                                }
                                flightListItem {
                                    try {
                                        val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                                        val outFormat = SimpleDateFormat("HH:mm")
                                        val departureTime = outFormat.format(parser.parse(flightDataListInfo[pos].departure_date))
                                        val arrivalTime = outFormat.format(parser.parse(flightDataListInfo[pos].arrival_date))
                                        id(flightDataListInfo[pos].id)
                                        flightTo("Flight to ${flightDataListInfo[pos].arrival_city.split(',')[0]}")
                                        flightDuration(flightDataListInfo[pos].scheduled_duration)
                                        flightDepartureAirport(flightDataListInfo[pos].departure_airport)
                                        flightArrivalAirport(flightDataListInfo[pos].arrival_airport)
                                        flightDepartureTime(departureTime)
                                        flightArrivalTime(arrivalTime)
                                        flightDepartureCity(flightDataListInfo[pos].departure_city.split(',')[0])
                                        flightArrivalCity(flightDataListInfo[pos].arrival_city.split(',')[0])
                                        onClick { _ ->
                                            val intent = Intent(applicationContext, TripDetailsActivity::class.java)
                                            intent.putExtra("FlightData", flightDataListInfo[pos])
                                            startActivity(intent)
                                        }
                                    } catch (e: Exception) {
                                        Log.e("FlightData", e.localizedMessage)
                                        Toast.makeText(applicationContext, "Unable to retrieve Flight data, Please try again later.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                } else {
                    Toast.makeText(applicationContext, "Unable to retrieve Flight data, Please try again later.", Toast.LENGTH_SHORT).show()
                }
                layoutTripsBinding.progressBar.visibility = View.GONE
            }
        })
    }
}