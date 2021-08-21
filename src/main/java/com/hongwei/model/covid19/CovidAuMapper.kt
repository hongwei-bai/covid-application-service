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

	fun map(source: List<AuGovCovidSource>): MobileCovidAuEntity =
		MobileCovidAuEntity(
			dataVersion = TimeStampUtil.getTimeVersionWithHour(),
			dataByDay = source.map { source ->
				AuGovCovidRecord(
					date = DateTimeParseUtil.parseDate(source.date)?.time,
					postcode = source.postcode,
					council = source.lgaName19,
					greatArea = source.lhd2010Name,
					state = PostcodeToStateMap.toState(source.postcode),
					likelyInfectionSource = AuGovCovidLikelyInfectionSource.parseFromString(source.likelySourceOfInfection)
				)
			}.groupBy { it.date }.mapNotNull { dayDiffToNotificationMap ->
				val notificationsByDay = dayDiffToNotificationMap.value
				dayDiffToNotificationMap.value.firstOrNull()?.let { firstNotification ->
					CovidAuDay(
						date = firstNotification.date ?: 0L,
						caseByState = notificationsByDay.groupBy { it.state }.mapNotNull { stateToNotificationsMap ->
							stateToNotificationsMap.key?.let { stateCode ->
								CovidAuCaseByState(
									stateCode = stateCode.name.toUpperCase(),
									stateName = stateCode.fullName,
									cases = stateToNotificationsMap.value.size
								)
							}
						}.sortedByDescending { it.cases },
						caseExcludeFromStates = notificationsByDay.filter { it.state == null }.size,
						caseTotal = notificationsByDay.size,
						caseByPostcode = notificationsByDay.groupBy { it.postcode }.mapNotNull { postcodeToNotificationsMap ->
							postcodeToNotificationsMap.key?.let { postcode ->
								CovidAuCaseByPostcode(
									postcode = postcode,
									cases = postcodeToNotificationsMap.value.size
								)
							}
						}.sortedByDescending { it.cases }
					)
				}
			}.sortedBy { it.date }
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