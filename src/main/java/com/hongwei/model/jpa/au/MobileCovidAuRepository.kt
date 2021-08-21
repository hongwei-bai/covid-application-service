package com.hongwei.model.jpa.au

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MobileCovidAuRepository : JpaRepository<MobileCovidAuEntity?, Long?> {
	@Query("from MobileCovidAuEntity entity order by entity.dataVersion desc")
	fun findRecentRecord(): MobileCovidAuEntity?
}