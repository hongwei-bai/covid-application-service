package com.hongwei.model.v2.jpa.au

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface MobileCovidAuRepositoryV2 : JpaRepository<MobileCovidAuEntityV2?, Long?> {
    @Query("from MobileCovidAuEntityV2 entity order by entity.dataCollectTimeStamp desc")
    fun findRecentRecord(): MobileCovidAuEntityV2?
}