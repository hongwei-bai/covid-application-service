package com.hongwei.model.covid19.auGov

import com.hongwei.model.covid19.AuGovCovidLikelyInfectionSource
import com.hongwei.model.covid19.AuGovCovidNotification
import com.hongwei.util.DateTimeParseUtil
import java.util.*

object AuGovCovidMapper {
    private const val MILLIS_PER_DAY = 24 * 60 * 60 * 1000

    fun map(postcodeRepo: AuSuburbRepository, source: AuGovCovidSource): AuGovCovidNotification? {
        val date = DateTimeParseUtil.parseDate(source.date)
        date?.let {
            val today = Calendar.getInstance().apply {
                set(Calendar.MILLISECOND, 0);
                set(Calendar.SECOND, 0);
                set(Calendar.MINUTE, 0);
                set(Calendar.HOUR, 0);
            }.time
            val dayDiff = (today.time - date.time) / MILLIS_PER_DAY
            val suburbInfo = source.postcode?.let { postcodeRepo.findSuburb(it) }
            return AuGovCovidNotification(
                dayDiff = dayDiff,
                date = date,
                postcode = source.postcode,
                council = source.lgaName19,
                suburbs = suburbInfo?.suburbs ?: emptyList(),
                greatArea = source.lhd2010Name,
                state = suburbInfo?.stateCode,
                likelyInfectionSource = AuGovCovidLikelyInfectionSource.parseFromString(source.likelySourceOfInfection)
            )
        }
        return null
    }
}