package com.hongwei.model.jpa.au

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id


@Entity
data class MobileCovidAuCsvEntity(
	@Id @GeneratedValue
	val id: Long = 0L,

	@Column(nullable = false)
	val dataVersion: Long = 0L,

	@Column(nullable = false)
	val lastUpdate: String = "",

	@Column(nullable = false)
	val recordsCount: Int = 0,

	@Column(nullable = false)
	val lastRecordDate: String = "",

	@Column(nullable = false)
	var csvPath: String = ""
)