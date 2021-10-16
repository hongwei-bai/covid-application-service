package com.hongwei.model.v2.jpa.au

import com.hongwei.model.v2.jpa.au.converter.LGAListConverter
import com.hongwei.model.v2.jpa.au.converter.StateDataConverter
import com.hongwei.model.v2.jpa.au.converter.StateDataListConverter
import javax.persistence.*


@Entity
data class MobileCovidAuEntityV2(
        @Id @Column(nullable = false)
        val dataVersion: Long = 0L,

        @Lob @Convert(converter = StateDataConverter::class) @Column(nullable = true)
        val nationData: StateDataV2? = null,

        @Lob @Convert(converter = StateDataListConverter::class) @Column(nullable = true)
        val stateData: List<StateDataV2> = emptyList(),

        @Lob @Convert(converter = LGAListConverter::class) @Column(nullable = true)
        var lgaData: List<StateLGADataV2> = emptyList()
)