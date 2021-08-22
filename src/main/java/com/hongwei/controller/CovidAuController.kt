package com.hongwei.controller

import com.hongwei.service.AuGovCovidService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/covid/au")
class CovidAuController {
	@Autowired
	private lateinit var auGovCovidService: AuGovCovidService

	@RequestMapping(path = ["/raw.do"])
	@ResponseBody
	fun getCovidRawData(dataVersion: Long, days: Long? = null): ResponseEntity<*> {
		return ResponseEntity.ok(auGovCovidService.getAuCovidData(dataVersion, days))
	}
}