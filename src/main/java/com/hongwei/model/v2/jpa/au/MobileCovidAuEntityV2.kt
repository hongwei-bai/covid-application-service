package com.hongwei.model.v2.jpa.au

import com.hongwei.model.v2.jpa.au.converter.LGAListConverter
import com.hongwei.model.v2.jpa.au.converter.StateDataConverter
import javax.persistence.*


@Entity
data class MobileCovidAuEntityV2(
        @Id @Column(nullable = false)
        val dataCollectTimeStamp: Long = 0L,

        @Lob @Convert(converter = StateDataConverter::class) @Column(nullable = true)
        var allStateData: AllStateDataV2? = null,

        @Lob @Convert(converter = LGAListConverter::class) @Column(nullable = true)
        var lgaData: List<LGADataV2>? = emptyList()
)