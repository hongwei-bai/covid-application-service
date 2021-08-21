package com.hongwei.model.covid19.auGov

import com.hongwei.model.covid19.AuState

object PostcodeToStateMap {
	fun toState(postcode: Long?): AuState? =
		postcode?.let {
			when (postcode) {
				in 1000..1999 -> AuState.nsw
				in 2000..2599 -> AuState.nsw
				in 2619..2899 -> AuState.nsw
				in 2921..2999 -> AuState.nsw

				in 200..299 -> AuState.act
				in 2600..2618 -> AuState.act
				in 2900..2920 -> AuState.act

				in 3000..3999 -> AuState.vic
				in 8000..8999 -> AuState.vic

				in 4000..4999 -> AuState.qld
				in 9000..9999 -> AuState.qld

				in 5000..5799 -> AuState.sa
				in 5800..5999 -> AuState.sa

				in 6000..6797 -> AuState.wa
				in 6800..6999 -> AuState.wa

				in 7000..7799 -> AuState.tas
				in 7800..7999 -> AuState.tas

				in 800..899 -> AuState.nt
				in 900..999 -> AuState.nt

				else -> null
			}
		}
}