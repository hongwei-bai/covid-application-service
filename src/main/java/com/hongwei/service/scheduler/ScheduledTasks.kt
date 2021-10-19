package com.hongwei.service.scheduler

import com.hongwei.service.AuCovidService
import com.hongwei.service.v1.NswDataSetsCovidServiceV1
import com.hongwei.util.TimeStampUtil.SYDNEY
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.apache.log4j.LogManager
import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.util.*


@Component
class ScheduledTasks {
    private val logger: Logger = LogManager.getLogger(ScheduledTasks::class.java)

    @Autowired
    private lateinit var nswDataSetsCovidServiceV1: NswDataSetsCovidServiceV1

    @Autowired
    private lateinit var auCovidService: AuCovidService

    private var onStart = true

    // 60 mins : 60 min x 60 s x 1000 ms = 1,800,000, For copy:3600000
    @Scheduled(fixedRate = 3600000)
    fun reportCurrentTime() {
        val sydTime = Calendar.getInstance(TimeZone.getTimeZone(SYDNEY))
        val hour = sydTime.get(Calendar.HOUR_OF_DAY)

        runBlocking {
            when {
                onStart -> {
                    logger.info("[ON_START]start running tasks...")
                    onStart = false
                    auCovidService.fetchDataFromSource()
                    logger.info("[ON_START]run V2 task completed.")
                }
                AustralianCovidUpdateHours.contains(hour) -> {
                    logger.info("[scheduled][$sydTime][h$hour]start running tasks...")
                    auCovidService.fetchDataFromSource()
                    logger.info("[scheduled][$sydTime][h$hour]run V2 task completed.")
                }
                else -> Unit
            }
        }
    }

    companion object {
        val AustralianCovidUpdateHours = listOf(0, 6, 8, 10, 12, 14, 16, 18, 20, 22)
    }
}