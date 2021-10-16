package com.hongwei.service

import com.hongwei.model.v2.jpa.au.StateLGADataV2

interface StateDataSetsServiceInterface {
    fun parseCsv(): StateLGADataV2?
}