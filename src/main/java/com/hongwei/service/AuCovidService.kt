package com.hongwei.service

import com.hongwei.model.v1.jpa.au.MobileCovidAuEntity
import com.hongwei.model.v1.jpa.au.MobileCovidAuRepository
import com.hongwei.service.v1.NswDataSetsCovidServiceV1
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AuCovidService {
    private val logger: Logger = LogManager.getLogger(AuCovidService::class.java)

    @Autowired
    private lateinit var mobileCovidAuRepository: MobileCovidAuRepository

    @Autowired
    private lateinit var nswDataSetsCovidService: NswDataSetsCovidService

    @Autowired
    private lateinit var vicDataSetsCovidService: VicDataSetsCovidService

    @Autowired
    private lateinit var covidLiveCurlService: CovidLiveCurlService

    fun getAuCovidData(dataVersion: Long, inDays: Long?): MobileCovidAuEntity {
        throw InternalError()
    }

    fun parseCsv(): MobileCovidAuEntity? {
        val stateSource: List<StateDataSetsServiceInterface> = listOf(nswDataSetsCovidService, vicDataSetsCovidService)
        
        stateSource.forEach {

        }

        throw InternalError()
    }
}