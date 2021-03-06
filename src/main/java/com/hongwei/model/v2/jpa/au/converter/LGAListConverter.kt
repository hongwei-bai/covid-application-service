package com.hongwei.model.v2.jpa.au.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.v2.jpa.au.StateLGADataV2
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class LGAListConverter : AttributeConverter<List<StateLGADataV2>, String?> {
    override fun convertToDatabaseColumn(obj: List<StateLGADataV2>): String? {
        return obj.let { Gson().toJson(obj) }
    }

    override fun convertToEntityAttribute(string: String?): List<StateLGADataV2> {
        val type: Type = object : TypeToken<List<StateLGADataV2>>() {}.type
        return if (string != null) Gson().fromJson(string, type) else emptyList()
    }
}