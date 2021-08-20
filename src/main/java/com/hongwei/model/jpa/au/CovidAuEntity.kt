package com.hongwei.model.jpa.au

import com.hongwei.model.covid19.CovidAuDay
import com.hongwei.model.jpa.au.converter.CovidAuListConverter
import javax.persistence.*


@Entity
data class CovidAuEntity(
	@Id @Column(nullable = false)
	val dataVersion: Long = 0L,

	@Lob @Convert(converter = CovidAuListConverter::class) @Column(nullable = true)
	var dataByDay: List<CovidAuDay> = emptyList()
)