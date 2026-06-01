package com.ninstudio.truthordare.data.api

import retrofit2.http.GET
import retrofit2.http.Query

data class ApiQuestion(
    val id: String,
    val question: String,
    val type: String,
    val rating: String,
    val translations: Map<String, String>? = null
)

interface TruthOrDareApi {
    @GET("truth")
    suspend fun getTruth(@Query("rating") rating: String): ApiQuestion

    @GET("dare")
    suspend fun getDare(@Query("rating") rating: String): ApiQuestion

    @GET("wyr")
    suspend fun getWyr(@Query("rating") rating: String): ApiQuestion
}
