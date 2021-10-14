package com.hongwei.model.v1.covid19.vic

data class VicDataSetsSource(
        val postcode: Int,
        val population: Long = 0L,
        val active: Int,
        val cases: Int,
        val rate: Float,
        val new: Int,
        val band: Int,
        val data_date: String,
        val file_processed_date: String
)