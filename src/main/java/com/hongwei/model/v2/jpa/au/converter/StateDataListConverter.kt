package com.hongwei.model.v2.jpa.au.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.v2.jpa.au.StateDataV2
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class StateDataListConverter : AttributeConverter<List<StateDataV2>, String?> {
    override fun convertToDatabaseColumn(obj: List<StateDataV2>): String? {
        return obj.let { Gson().toJson(obj) }
    }

    override fun convertToEntityAttribute(string: String?): List<StateDataV2> {
        val type: Type = object : TypeToken<List<StateDataV2>>() {}.type
        return if (string != null) Gson().fromJson(string, type) else emptyList()
    }
}