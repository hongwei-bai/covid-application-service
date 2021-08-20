package com.hongwei.model.jpa.au

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CovidAuRepository : JpaRepository<CovidAuEntity?, Long?> {
	@Query("from CovidAuEntity entity order by entity.dataVersion desc")
	fun findRecentRecord(): CovidAuEntity?
}