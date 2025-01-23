package com.exoticdg.weatheralerts

import com.squareup.moshi.Json

data class AlertDTO(
    @field:Json(name = "alert")
    val alertData: Request


)
