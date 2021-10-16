package com.hongwei.model.v2.jpa.au


data class StateLGADataV2(
        val lastUpdate: String? = null,

        val lastRecordTimeStamp: Long = 0,

        val state: String = "",

        var lga: List<LGADataV2> = emptyList()
)
