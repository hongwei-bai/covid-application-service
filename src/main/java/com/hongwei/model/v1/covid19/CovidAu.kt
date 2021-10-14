package com.hongwei.model.v1.covid19

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