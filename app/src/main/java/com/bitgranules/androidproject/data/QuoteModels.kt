
package com.bitgranules.androidproject.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import retrofit2.http.GET
import retrofit2.http.Url

@Serializable
data class QuoteStruct(
    @SerialName("q") val content: String,
    @SerialName("a") val author: String,
)

@Serializable
data class CustomApiConfig(
    val id: String,
    val name: String,
    val fullUrl: String,
    val contentKey: String,
    val isArray: Boolean,
    val authorKey: String
)

data class FontConfig(
    val fontName: String,
    val fontResId: Int
)

interface QuoteApiService {
    @GET
    suspend fun getCustomQuote(@Url fullUrl: String): JsonElement
}