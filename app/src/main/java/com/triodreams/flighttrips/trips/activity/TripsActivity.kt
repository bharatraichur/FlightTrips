package com.triodreams.flighttrips.trips.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.triodreams.flighttrips.R
import com.triodreams.flighttrips.databinding.ActivityTripsBinding
import com.triodreams.flighttrips.flightHeaderItem
import com.triodreams.flighttrips.flightListItem
import com.triodreams.flighttrips.trips.models.FlightDataModel
import com.triodreams.flighttrips.trips.network.FlightDataService
import com.triodreams.flighttrips.trips.viewmodel.TripsViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime

class TripsActivity : AppCompatActivity() {
    private lateinit var layoutTripsBinding: ActivityTripsBinding

    private lateinit var tripsViewModel: TripsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        layoutTripsBinding = DataBindingUtil.setContentView(this, R.layout.activity_trips)

        supportActionBar?.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar?.setCustomView(R.layout.toolbar_title)

        tripsViewModel = ViewModelProviders.of(this).get(TripsViewModel::class.java)

        layoutTripsBinding.apply {
            viewmodel = tripsViewModel
        }

        layoutTripsBinding.recyclerView.layoutManager = StickyHeaderLinearLayoutManager(this)

        val service = Retrofit.Builder()
            .baseUrl(getString(R.string.flight_data_base_url))
            .addConverterFactory(MoshiConverterFactory.create())
            .build()

        val flightDataAPI = service.create(FlightDataService::class.java)

        val flightDataCall = flightDataAPI.getFlightData("media", "81d93056-9c7f-451d-94b6-3e88eb6fa9ad")

        flightDataCall.enqueue(object: Callback<List<FlightDataModel>> {
            override fun onFailure(call: Call<List<FlightDataModel>>, t: Throwable) {
                Log.e("Error", t.localizedMessage, t)
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
                                    } catch (e: ParseException) {
                                        Log.e("Error", e.localizedMessage)
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
                                    } catch (e: ParseException) {
                                        Log.e("Error", e.localizedMessage)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        })
    }
}