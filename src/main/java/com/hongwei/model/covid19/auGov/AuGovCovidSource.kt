package com.hongwei.model.covid19.auGov

data class AuGovCovidSource(
        val date: String,
        val postcode: Long? = 0L,
        val likelySourceOfInfection: String,
        val lhd2010Code: String? = null,
        val lhd2010Name: String? = null,
        val lgaCode19: Long? = null,
        val lgaName19: String? = null
)