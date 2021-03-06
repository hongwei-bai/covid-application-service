package com.hongwei.model.v1.jpa.au

import com.hongwei.model.v1.covid19.CovidAuDay
import com.hongwei.model.v1.jpa.au.converter.CovidAuListConverter
import javax.persistence.*


@Entity
data class MobileCovidAuEntity(
	@Id @Column(nullable = false)
	val dataVersion: Long = 0L,

	@Column(nullable = false)
	val lastUpdate: String = "",

	@Column(nullable = false)
	val recordsCount: Int = 0,

	@Column(nullable = false)
	val lastRecordDate: String = "",

	@Lob @Convert(converter = CovidAuListConverter::class) @Column(nullable = true)
	var dataByDay: List<CovidAuDay> = emptyList()
)