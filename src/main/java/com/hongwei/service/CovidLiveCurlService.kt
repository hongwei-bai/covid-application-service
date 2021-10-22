package com.hongwei.service

import com.hongwei.model.common.AuState
import com.hongwei.model.v2.jpa.au.StateDataV2
import com.hongwei.util.curl.CUrlWrapper
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service
import java.util.regex.Pattern


@Service
class CovidLiveCurlService {
    private val logger: Logger = LogManager.getLogger(CovidLiveCurlService::class.java)

    companion object {
        private const val LIVE_DASHBOARD_URL = "https://covidlive.com.au/"

        private const val SECTION_NEW_CASES = "CASES STD-3"

        private const val PATTERN_NUMBER_IN_HTML_TAG = "\\>[\\d,]+\\<\\/"

    }

    fun parseWebContent(): List<StateDataV2>? =
            CUrlWrapper.curl(LIVE_DASHBOARD_URL)?.let {
                parseDoc(it)
            }

    private fun parseDoc(doc: Document): List<StateDataV2> {
        val section = doc.getElementsByClass(SECTION_NEW_CASES)

        val evenList = section.first().getElementsByClass("even")
        val oddList = section.first().getElementsByClass("odd")
        return listOf(evenList, oddList).flatten().mapNotNull {
            parseStateDoc(it)
        }
    }

    private fun parseStateDoc(stateElement: Element): StateDataV2? {
        val stateString = stateElement.getElementsByClass("COL1 STATE").first()
                .getElementsByTag("a").attr("href")
                .replace("/", "")
        val totalCases = parseNumberInHtmlTagByRegex(stateElement
                .getElementsByClass("COL2 CASES").toString())
        val overseasCases = parseNumberInHtmlTagByRegex(stateElement
                .getElementsByClass("COL3 OSEAS").toString())
        val netCases = parseNumberInHtmlTagByRegex(stateElement
                .getElementsByClass("COL5 NET").toString())
        return if (netCases != null && totalCases != null) {
            StateDataV2(
                    state = stateString.mapStateString(),
                    totalCases = totalCases,
                    overseasCases = overseasCases ?: 0,
                    newCases = netCases
            )
        } else {
            null
        }
    }

    private fun parseNumberInHtmlTagByRegex(line: String): Long? =
            try {
                val pattern = Pattern.compile(PATTERN_NUMBER_IN_HTML_TAG)
                val matcher = pattern.matcher(line)
                if (matcher.find()) {
                    matcher.group(0)
                            .replace(">", "")
                            .replace("</", "")
                            .replace(",", "")
                            .toLong()
                } else null
            } catch (e: Exception) {
                logger.error("parseNumberInHtmlTagByRegex exception caught: ${e.localizedMessage}")
                e.printStackTrace()
                null
            }

    private fun String.mapStateString(): String =
            when (this) {
                "nsw" -> AuState.Nsw.name
                "vic" -> AuState.Vic.name
                "qld" -> AuState.Qld.name
                "act" -> AuState.Act.name
                "wa" -> AuState.Wa.name
                "sa" -> AuState.Sa.name
                "tas" -> AuState.Tas.name
                "nt" -> AuState.Nt.name
                "australia" -> AuState.Total.name
                else -> AuState.Total.name
            }
}