package com.hongwei.controller

import com.hongwei.service.v1.NswDataSetsCovidServiceV1
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/covid/au")
class CovidAuController {
	@Autowired
	private lateinit var nswDataSetsCovidServiceV1: NswDataSetsCovidServiceV1

	@RequestMapping(path = ["/raw.do"])
	@ResponseBody
	fun getCovidRawData(dataVersion: Long, days: Long? = null): ResponseEntity<*> {
		return ResponseEntity.ok(nswDataSetsCovidServiceV1.getAuCovidData(dataVersion, days))
	}
}