package com.hongwei.service

import com.hongwei.model.v2.jpa.au.LGADataV2

interface StateDataSetsServiceInterface {
    fun parseCsv(): LGADataV2?
}