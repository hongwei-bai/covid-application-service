package com.hongwei.model.v2.jpa.au


data class LGADataV2(
        val dataCollectTimeStamp: Long = 0L,

        val lastUpdate: String? = null,

        val lastRecordTimeStamp: Long = 0,

        val state: String = "",

        val data: LGADayDataV2,

        val historyData: List<LGADayDataV2> = emptyList()
)
