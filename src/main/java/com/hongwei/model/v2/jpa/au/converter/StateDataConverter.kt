package com.hongwei.model.v2.jpa.au.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.v2.jpa.au.AllStateDataV2
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class StateDataConverter : AttributeConverter<AllStateDataV2, String?> {
    override fun convertToDatabaseColumn(stringList: AllStateDataV2): String? {
        return stringList.let { Gson().toJson(stringList) }
    }

    override fun convertToEntityAttribute(string: String?): AllStateDataV2? {
        val listType: Type = object : TypeToken<AllStateDataV2?>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else null
    }
}