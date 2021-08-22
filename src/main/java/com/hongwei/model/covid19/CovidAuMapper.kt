package com.hongwei.model.covid19

import com.hongwei.model.covid19.auGov.AuGovCovidSource
import com.hongwei.model.covid19.auGov.PostcodeToStateMap
import com.hongwei.model.jpa.au.MobileCovidAuEntity
import com.hongwei.util.DateTimeParseUtil
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

object CovidAuMapper {
	private val logger: Logger = LogManager.getLogger(CovidAuMapper::class.java)

	fun map(source: List<AuGovCovidSource>, lastUpdate: String, lastRecordDate: String, recordsCount: Int): MobileCovidAuEntity =
		MobileCovidAuEntity(
			dataVersion = TimeStampUtil.getTimeVersionWithHour(),
			lastUpdate = lastUpdate,
			lastRecordDate = lastRecordDate,
			recordsCount = recordsCount,
			dataByDay = source.asSequence().map {
				AuGovCovidRecord(
					date = DateTimeParseUtil.parseDate(it.date)?.time,
					postcode = it.postcode,
					council = it.lgaName19,
					greatArea = it.lhd2010Name,
					state = PostcodeToStateMap.toState(it.postcode),
					likelyInfectionSource = AuGovCovidLikelyInfectionSource.parseFromString(it.likelySourceOfInfection)
				)
			}.groupBy { it.date }.filter {
				val mid = it
				true
			}.mapNotNull { dateToNotificationMap ->
				val recordsByDay = dateToNotificationMap.value
				dateToNotificationMap.value.firstOrNull()?.let { firstRecordByDay ->
					CovidAuDay(
						date = firstRecordByDay.date ?: 0L,
						caseByState = recordsByDay.groupBy { it.state }.mapNotNull { stateToRecordsMap ->
							stateToRecordsMap.key?.let { stateCode ->
								CovidAuCaseByState(
									stateCode = stateCode.name.toUpperCase(),
									stateName = stateCode.fullName,
									cases = stateToRecordsMap.value.size
								)
							}
						}.sortedByDescending { it.cases },
						caseExcludeFromStates = recordsByDay.filter { it.state == null }.size,
						caseTotal = recordsByDay.size,
						caseByPostcode = recordsByDay.groupBy { it.postcode }.mapNotNull { postcodeToRecordsMap ->
							postcodeToRecordsMap.key?.let { postcode ->
								CovidAuCaseByPostcode(
									postcode = postcode,
									cases = postcodeToRecordsMap.value.size
								)
							}
						}.sortedByDescending { it.cases }
					)
				}
			}.sortedByDescending { it.date }.toList()
		)

	data class AuGovCovidRecord(
		val date: Long? = null,
		val postcode: Long? = null,
		val council: String? = null,
		val greatArea: String? = null,
		val state: AuState? = null,
		val likelyInfectionSource: AuGovCovidLikelyInfectionSource? = null
	)
}