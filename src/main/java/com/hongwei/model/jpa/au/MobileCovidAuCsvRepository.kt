package com.hongwei.model.jpa.au

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MobileCovidAuCsvRepository : JpaRepository<MobileCovidAuCsvEntity?, Long?> {
	@Query("from MobileCovidAuCsvEntity entity order by entity.id desc")
	fun findAllRecentRecords(): List<MobileCovidAuCsvEntity>
}