package com.project.cureconnect.data.api.Api

data class Candidate(
    val content: Content,
    val finishReason: String,
    val safetyRatings: List<SafetyRating>
)