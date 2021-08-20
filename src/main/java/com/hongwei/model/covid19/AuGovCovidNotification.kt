package com.hongwei.model.covid19

import java.util.*


data class AuGovCovidNotification(
        val dayDiff: Long = 0L,
        val date: Date? = null,
        val postcode: Long? = null,
        val suburbs: List<String> = emptyList(),
        val council: String? = null,
        val greatArea: String? = null,
        val state: String? = null,
        val likelyInfectionSource: AuGovCovidLikelyInfectionSource? = null
)