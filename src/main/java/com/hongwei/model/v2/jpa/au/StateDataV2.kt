package com.hongwei.model.v2.jpa.au


data class StateDataV2(
        val state: String = "",

        val totalCases: Long = 0,

        val overseasCases: Long = 0,

        val newCases: Long = 0
)
