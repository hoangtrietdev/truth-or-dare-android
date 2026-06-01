package com.ninstudio.truthordare.data.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("id") val id: String,
    @SerializedName("type") val type: String, // "truth" or "dare"
    @SerializedName("rating") val rating: String, // "normal" or "adult_18"
    @SerializedName("translations") val translations: Map<String, String>
)

object QuestionType {
    const val TRUTH = "truth"
    const val DARE = "dare"
}

object QuestionRating {
    const val NORMAL = "normal"
    const val ADULT = "adult_18"
}
