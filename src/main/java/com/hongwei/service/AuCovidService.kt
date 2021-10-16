package com.hongwei.service

import com.hongwei.constants.ResetContent
import com.hongwei.model.common.AuState
import com.hongwei.model.v2.jpa.au.MobileCovidAuEntityV2
import com.hongwei.model.v2.jpa.au.MobileCovidAuRepositoryV2
import com.hongwei.util.TimeStampUtil.getTimeVersionWithHour
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AuCovidService {
    private val logger: Logger = LogManager.getLogger(AuCovidService::class.java)

    @Autowired
    private lateinit var mobileCovidAuRepositoryV2: MobileCovidAuRepositoryV2

    @Autowired
    private lateinit var nswDataSetsCovidService: NswDataSetsCovidService

    @Autowired
    private lateinit var vicDataSetsCovidService: VicDataSetsCovidService

    @Autowired
    private lateinit var covidLiveCurlService: CovidLiveCurlService

    fun getAuCovidData(dataVersion: Long, followedSuburbs: String? = null): MobileCovidAuEntityV2? {
        val entity = mobileCovidAuRepositoryV2.findRecentRecord().firstOrNull()
        if (entity?.dataVersion == dataVersion) {
            throw ResetContent
        }
        entity?.apply {
            followedSuburbs?.let {
                val followedPostcodeList = followedSuburbs.split(",").mapNotNull {
                    it.trim().toIntOrNull()
                }
                lgaData.forEach { stateLgaData ->
                    stateLgaData.apply {
                        lga = lga.filter {
                            followedPostcodeList.contains(it.postcode)
                        }
                    }
                }
            }
        }
        return entity
    }

    fun fetchDataFromSource(): MobileCovidAuEntityV2 {
        val stateSource: List<StateDataSetsServiceInterface> = listOf(nswDataSetsCovidService, vicDataSetsCovidService)

        val stateData = covidLiveCurlService.parseWebContent()
        val lgaData = stateSource.mapNotNull {
            it.parseCsv()
        }

        val entity = MobileCovidAuEntityV2(
                dataVersion = getTimeVersionWithHour(),
                nationData = stateData?.firstOrNull {
                    it.state == AuState.Total.name
                },
                stateData = stateData?.filter {
                    it.state != AuState.Total.name
                } ?: emptyList(),
                lgaData = lgaData
        )
        mobileCovidAuRepositoryV2.save(entity)

        return entity
    }
}