package com.hongwei.model.v1.covid19.vic

data class VicDataSetsSource(
        val postcode: Int,
        val population: Long = 0L,
        val active: Long,
        val cases: Long,
        val rate: Float,
        val new: Long,
        val band: Long,
        val data_date: String,
        val file_processed_date: String
)