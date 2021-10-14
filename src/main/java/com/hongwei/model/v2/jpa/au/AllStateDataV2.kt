package com.hongwei.model.v2.jpa.au


data class AllStateDataV2(
        val dataCollectTimeStamp: Long = 0L,

        val list: List<StateDataV2> = emptyList()
)
