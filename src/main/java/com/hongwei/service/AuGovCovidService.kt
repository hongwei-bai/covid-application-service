package com.hongwei.service

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.constants.NoContent
import com.hongwei.constants.ResetContent
import com.hongwei.model.covid19.CovidAuCaseByPostcodeBrief
import com.hongwei.model.covid19.CovidAuDayBrief
import com.hongwei.model.covid19.CovidAuMapper
import com.hongwei.model.covid19.CovidAuSuburbBreif
import com.hongwei.model.covid19.auGov.AuGovCovidMapper
import com.hongwei.model.covid19.auGov.AuGovCovidSource
import com.hongwei.model.jpa.au.*
import com.hongwei.util.CsvUtil
import com.hongwei.util.LocalJsonReaderUtil
import com.hongwei.util.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


@Service
class AuGovCovidService {
	private val logger: Logger = LogManager.getLogger(AuGovCovidService::class.java)

	companion object {
		private const val AU_GOV_COVID_DATA_URL = "https://data.nsw.gov.au/data/dataset/nsw-covid-19-cases-by-location-and-likely-source-of-infection/resource/2776dbb8-f807-4fb2-b1ed-184a6fc2c8aa"

		private const val LOCATE_STRING_OPEN = "\"DCTERMS.Identifier\""
	}

	@Autowired
	private lateinit var covidAuRepository: CovidAuRepository

	fun getAuCovidBriefData(dataVersion: Long, inDays: Long, top: Int, followedPostcodes: List<Long>): CovidAuSuburbBreif {
		val entityDb = covidAuRepository.findRecentRecord()
		entityDb?.let {
			if (dataVersion < entityDb.dataVersion) {
				return CovidAuSuburbBreif(
					dataVersion = entityDb.dataVersion,
					dataByDay = entityDb.dataByDay.filterIndexed { index, _ -> index < inDays }
						.mapNotNull { raw ->
							CovidAuDayBrief(
								dayDiff = raw.dayDiff,
								dateDisplay = raw.dateDisplay,
								caseByState = raw.caseByState,
								caseExcludeFromStates = raw.caseExcludeFromStates,
								caseTotal = raw.caseTotal,
								caseByPostcodeTops = raw.caseByPostcode
									.sortedByDescending { it.cases }
									.filterIndexed { index, _ -> index < top }
									.map {
										CovidAuCaseByPostcodeBrief(
											postcode = it.postcode,
											suburbBrief = it.suburbBrief,
											state = it.state,
											cases = it.cases
										)
									},
								caseByPostcodeFollowed = raw.caseByPostcode
									.filter { followedPostcodes.contains(it.postcode) }
									.sortedByDescending { it.cases }
									.map {
										CovidAuCaseByPostcodeBrief(
											postcode = it.postcode,
											suburbBrief = it.suburbBrief,
											state = it.state,
											cases = it.cases
										)
									}
							)
						}
				)
			} else throw ResetContent
		} ?: throw NoContent
	}

	fun getAuCovidData(dataVersion: Long, inDays: Long?): CovidAuEntity {
		val entityDb = covidAuRepository.findRecentRecord()
		entityDb?.let {
			if (dataVersion < entityDb.dataVersion) {
				entityDb.dataByDay = entityDb.dataByDay.filterIndexed { index, _ -> index < inDays ?: 1 }
				return entityDb
			} else throw ResetContent
		} ?: throw NoContent
	}

	fun parseCsv(): CovidAuEntity? {
		val lines = CsvUtil.readCSVFromUrl(getCSVUrl())
		val sourceList = mutableListOf<AuGovCovidSource>()
		lines.forEach {
			val data = it.split(",")
			when (data.size) {
				7 -> AuGovCovidSource(
					date = data.first(),
					postcode = data[1].toLongOrNull() ?: 0L,
					likelySourceOfInfection = data[2],
					lhd2010Code = data[3],
					lhd2010Name = data[4],
					lgaCode19 = data[5].toLongOrNull() ?: 0L,
					lgaName19 = data[6]
				)
				else -> {
					logger.debug("unrecognized record: $data")
					null
				}
			}?.let { record ->
				sourceList.add(record)
			}
		}
		val notifications = sourceList.mapNotNull { AuGovCovidMapper.map(auSuburbRepository, it) }
		val entityDb = covidAuRepository.findRecentRecord()
		val entity = CovidAuMapper.map(auSuburbRepository, notifications)
		if (entity != null && entityDb != entity) {
			if (covidAuRepository.findAll().isNotEmpty()) {
				covidAuRepository.deleteAll()
			}
			covidAuRepository.save(entity)
			return entity
		}
		return entityDb
	}

	private fun getCSVUrl(): String {
		CUrlWrapper.curl(AU_GOV_COVID_DATA_URL)?.toString()?.run {
			val index0 = indexOf(LOCATE_STRING_OPEN)
			val mid2 = substring(index0 + LOCATE_STRING_OPEN.length)
			val index1 = mid2.indexOf("\">")
			val mid3 = mid2.substring(0, index1)
			val mid4 = mid3.replace("content=\"", "").trim()
			return mid4
		}
		return ""
	}
}