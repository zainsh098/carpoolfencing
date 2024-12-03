package com.example.carpoolfencing.models

data class GeocodingModelResponse(
    val summary: Summary,
    val results: List<Result>,
)

data class Summary(
    val query: String,
    val queryType: String,
    val queryTime: Long,
    val numResults: Long,
    val offset: Long,
    val totalResults: Long,
    val fuzzyLevel: Long,
)

data class Result(
    val type: String,
    val id: String,
    val score: Double,
    val entityType: String,
    val matchConfidence: MatchConfidence,
    val address: Address,
    val position: Position,
    val viewport: Viewport,
    val boundingBox: BoundingBox,
    val dataSources: DataSources,
)

data class MatchConfidence(
    val score: Long,
)

data class Address(
    val municipality: String,
    val countrySubdivision: String,
    val countrySubdivisionName: String,
    val countrySubdivisionCode: String,
    val countryCode: String,
    val country: String,
    val countryCodeIso3: String,
    val freeformAddress: String,
)

data class Position(
    val lat: Double,
    val lon: Double,
)

data class Viewport(
    val topLeftPoint: TopLeftPoint,
    val btmRightPoint: BtmRightPoint,
)

data class TopLeftPoint(
    val lat: Double,
    val lon: Double,
)

data class BtmRightPoint(
    val lat: Double,
    val lon: Double,
)

data class BoundingBox(
    val topLeftPoint: TopLeftPoint2,
    val btmRightPoint: BtmRightPoint2,
)

data class TopLeftPoint2(
    val lat: Double,
    val lon: Double,
)

data class BtmRightPoint2(
    val lat: Double,
    val lon: Double,
)

data class DataSources(
    val geometry: Geometry,
)

data class Geometry(
    val id: String,
)