// NwsAlertsResponse.kt
package com.exoticdg.weatheralerts.data.alerts // Adjust package name as needed

import com.google.gson.annotations.SerializedName

data class NwsAlertsResponse(
    @SerializedName("features") // Matches the key in the JSON
    val features: List<AlertFeature>? = null
)

data class AlertFeature(
    @SerializedName("properties")
    val properties: AlertProperties? = null
)

data class AlertProperties(
    @SerializedName("event")
    val event: String? = null, // Name of the alert/warning/watch

    @SerializedName("headline")
    val headline: String? = null, // A concise summary

    @SerializedName("description")
    val description: String? = null, // Detailed description

    @SerializedName("severity")
    val severity: String? = null, // e.g., "Severe", "Moderate", "Minor"

    @SerializedName("areaDesc")
    val areaDesc: String? = null, // Description of the affected area

    @SerializedName("effective")
    val effective: String? = null, // Start time (ISO 8601 format)

    @SerializedName("expires")
    val expires: String? = null, // End time (ISO 8601 format)

    @SerializedName("certainty")
    val certainty: String? = null, // certainty of the alert (e.g., "Observed", "Likely", "Possible")

    @SerializedName("instruction")
    val instruction: String? = null // instructions for proceeding


// Add other properties you need from the API response
)