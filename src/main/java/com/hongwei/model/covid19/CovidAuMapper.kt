package com.hongwei.model.covid19

import com.hongwei.model.jpa.au.CovidAuEntity
import com.hongwei.util.DateTimeParseUtil.toDisplay
import com.hongwei.util.TimeStampUtil
import org.apache.log4j.LogManager
import org.apache.log4j.Logger

object CovidAuMapper {
	private val logger: Logger = LogManager.getLogger(CovidAuMapper::class.java)

	fun map(postcodeRepo: AuSuburbRepository, mid: List<AuGovCovidNotification>): CovidAuEntity =
		CovidAuEntity(
			dataVersion = TimeStampUtil.getTimeVersionWithHour(),
			dataByDay = mid.groupBy { it.dayDiff }.mapNotNull { dayDiffToNotificationMap ->
				val notificationsByDay = dayDiffToNotificationMap.value
				dayDiffToNotificationMap.value.firstOrNull()?.let { firstNotification ->
					CovidAuDay(
						dayDiff = firstNotification.dayDiff,
						dateUnixTimeStamp = firstNotification.date?.time ?: 0L,
						dateDisplay = toDisplay(firstNotification.date),
						caseByState = notificationsByDay.groupBy { it.state }.mapNotNull { stateToNotificationsMap ->
							stateToNotificationsMap.key?.let { stateCode ->
								CovidAuCaseByState(
									stateCode = stateCode.toUpperCase(),
									stateName = AuState.valueOf(stateCode.toLowerCase()).fullName,
									cases = stateToNotificationsMap.value.size
								)
							}
						}.sortedByDescending { it.cases },
						caseExcludeFromStates = notificationsByDay.filter { it.state == null }.size,
						caseTotal = notificationsByDay.size,
						caseByPostcode = notificationsByDay.groupBy { it.postcode }.mapNotNull { postcodeToNotificationsMap ->
							postcodeToNotificationsMap.key?.let { postcode ->
								val postcodeData = postcodeRepo.findSuburb(postcode)
								val suburbs = postcodeData?.suburbs ?: emptyList<String>()
								if (postcodeData != null) {
									CovidAuCaseByPostcode(
										postcode = postcode,
										suburbs = suburbs,
										suburbBrief = getSuburbBrief(suburbs) ?: "",
										latitude = postcodeData.latitude,
										longitude = postcodeData.longitude,
										accuracy = postcodeData.accuracy,
										state = postcodeData.stateCode.toUpperCase(),
										cases = postcodeToNotificationsMap.value.size
									)
								} else null
							}
						}.sortedByDescending { it.cases }
					)
				}
			}.sortedBy { it.dayDiff }
		)

	fun getSuburbBrief(suburbs: List<String>): String? = when (suburbs.size) {
		0 -> null
		1 -> suburbs.first()
		else -> {
			var result: String? = null
			// Priority No.1
			suburbs.forEachIndexed { i, suburbOut ->
				suburbs.forEachIndexed { j, suburbInner ->
					if (i != j && suburbOut.contains(suburbInner)) {
						result = suburbInner
					}
				}
			}

			// Priority No.2
			if (result == null) {
				val wordRepeatMap = hashMapOf<String, Int>()
				suburbs.forEach {
					val words = it.split(" ")
					words.forEach { word ->
						if (!SuburbMeaninglessWords.contains(word)) {
							wordRepeatMap[word] = 1 + (wordRepeatMap[word] ?: 0)
						}
					}
				}
				val list = wordRepeatMap.toList().sortedByDescending { it.second }
				if (list.first().second >= 2) {
					result = list.first().first
				}
			}

			result ?: suburbs.first()
		}
	}

	private val SuburbMeaninglessWords = listOf(
		"West", "East", "North", "South", "Park", "Hill", "Hills", "Point", "River", "Estate", "Old", "Mountain", "Valley", "Ridge", "Creek", "Island", "Junction", "Vale", "Bay",
		"Harbour", "Farm", "Cove", "Beach", "Centre", "Shore", "Hospital", "Head", "Airport", "Grove", "Heights", "Plateau", "Caves", "Town", "Forest", "Flat", "Walls", "Crossing", "DC"
	)
}