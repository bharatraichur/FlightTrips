package com.triodreams.flighttrips.trips.network

import com.triodreams.flighttrips.trips.models.FlightDataModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface FlightDataService {
    @GET("developer-test-flights-list.json")
    fun getFlightData(
        @Query("alt") alt: String,
        @Query("token") token: String
    ) : Call<List<FlightDataModel>>
}