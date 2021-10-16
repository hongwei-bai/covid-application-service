package com.hongwei.controller

import com.hongwei.service.AuCovidService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/covid-v2/au")
class CovidAuControllerV2 {
    @Autowired
    private lateinit var auCovidService: AuCovidService

    @RequestMapping(path = ["/raw.do"])
    @ResponseBody
    fun getCovidRawData(dataVersion: Long, followedSuburbs: String? = null): ResponseEntity<*> {
        return ResponseEntity.ok(auCovidService.getAuCovidData(dataVersion, followedSuburbs))
    }

    @RequestMapping(path = ["/fetch.do"])
    @ResponseBody
    fun fetchDataManually(): ResponseEntity<*> {
        return ResponseEntity.ok(auCovidService.fetchDataFromSource())
    }
}