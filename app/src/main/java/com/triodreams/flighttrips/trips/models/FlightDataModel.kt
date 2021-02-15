package com.triodreams.flighttrips.trips.models

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class FlightDataModel(
        var id: Int,
        var departure_date: String,
        var airline_code: String,
        var flight_number: String,
        var departure_city: String,
        var departure_airport: String,
        var arrival_city: String,
        var arrival_airport: String,
        var scheduled_duration: String,
        var arrival_date: String
)