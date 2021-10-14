package com.hongwei.model.v2.jpa.au.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.v2.jpa.au.LGADataV2
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class LGAListConverter : AttributeConverter<List<LGADataV2>, String?> {
    override fun convertToDatabaseColumn(stringList: List<LGADataV2>): String? {
        return stringList.let { Gson().toJson(stringList) }
    }

    override fun convertToEntityAttribute(string: String?): List<LGADataV2> {
        val listType: Type = object : TypeToken<List<LGADataV2>>() {}.type
        return if (string != null) Gson().fromJson(string, listType) else emptyList()
    }
}