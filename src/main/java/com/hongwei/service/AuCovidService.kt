package com.hongwei.service

import com.hongwei.constants.ResetContent
import com.hongwei.model.common.AuState
import com.hongwei.model.v2.jpa.au.MobileCovidAuEntityV2
import com.hongwei.model.v2.jpa.au.MobileCovidAuRepositoryV2
import com.hongwei.model.v2.jpa.au.StateDataV2
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
        val dataVersion = getTimeVersionWithHour()

        val previousStateData = mobileCovidAuRepositoryV2.findRecentRecord().firstOrNull()?.stateData
        val stateData: MutableList<StateDataV2>? = covidLiveCurlService.parseWebContent()?.toMutableList()
        stateData?.apply {
            val stateDataMap = mapIndexed { i, stateDataV2 -> stateDataV2.state to i }.toMap()
            AuState.values().forEach { state ->
                val stateCode = state.name
                if (!map { it.state }.contains(stateCode)) {
                    val previousStateDataVersion = previousStateData?.firstOrNull { it.state == stateCode }?.lastValidDataVersion
                    add(StateDataV2(
                            state = stateCode,
                            totalCases = previousStateData?.firstOrNull { it.state == stateCode }?.totalCases ?: 0,
                            overseasCases = previousStateData?.firstOrNull { it.state == stateCode }?.overseasCases ?: 0,
                            newCases = previousStateData?.firstOrNull { it.state == stateCode }?.newCases ?: 0,
                            isObsoletedData = true,
                            lastValidDataVersion = if (previousStateDataVersion != null && previousStateDataVersion > 0) {
                                previousStateDataVersion
                            } else {
                                dataVersion
                            }
                    ))
                } else {
                    val index = stateDataMap[stateCode]
                    index?.let {
                        val itemData = get(index)
                        itemData.lastValidDataVersion = dataVersion
                        set(index, itemData)
                    }
                }
            }
        }

        val lgaData = stateSource.mapNotNull {
            it.parseCsv()
        }

        val entity = MobileCovidAuEntityV2(
                dataVersion = dataVersion,
                nationData = stateData?.firstOrNull { it.state == AuState.Total.name },
                stateData = stateData?.filter { it.state != AuState.Total.name } ?: emptyList(),
                lgaData = lgaData
        )
        logger.debug("fetch MobileCovidAuEntityV2: $entity")
        mobileCovidAuRepositoryV2.save(entity)

        return entity
    }
}