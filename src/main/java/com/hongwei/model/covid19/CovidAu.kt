package com.hongwei.model.covid19

data class CovidAuDay(
	var date: Long,
	var caseByState: List<CovidAuCaseByState> = emptyList(),
	var caseExcludeFromStates: Int = 0,
	var caseTotal: Int = 0,
	var caseByPostcode: List<CovidAuCaseByPostcode> = emptyList()
)

data class CovidAuCaseByState(
	val stateCode: String,
	val stateName: String,
	val cases: Int
)

data class CovidAuCaseByPostcode(
	val postcode: Long,
	val cases: Int
)

enum class AuState(val fullName: String) {
	act("Australian Capital Territory"),
	nsw("New South Wales"),
	vic("Victoria"),
	sa("South Australia"),
	wa("Western Australia"),
	nt("Northern Territory"),
	qld("Queensland"),
	tas("Tasmania")
}
