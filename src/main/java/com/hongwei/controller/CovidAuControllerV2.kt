package com.hongwei.controller

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.hongwei.model.CovidNewsJsonData
import com.hongwei.service.AuCovidService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestController
import java.io.File

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

    @RequestMapping(path = ["/news.do"])
    @ResponseBody
    fun getNews(): ResponseEntity<*> {
        val newsJson = ObjectMapper().registerModule(KotlinModule())
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .readValue(File("/home/covidNews.json"), CovidNewsJsonData::class.java)
        return ResponseEntity.ok(newsJson)
    }
}