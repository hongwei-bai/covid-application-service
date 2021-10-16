package com.hongwei.model.v2.jpa.au.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.v2.jpa.au.StateDataV2
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class StateDataConverter : AttributeConverter<StateDataV2, String?> {
    override fun convertToDatabaseColumn(obj: StateDataV2): String? {
        return obj.let { Gson().toJson(obj) }
    }

    override fun convertToEntityAttribute(string: String?): StateDataV2? {
        val type: Type = object : TypeToken<StateDataV2?>() {}.type
        return if (string != null) Gson().fromJson(string, type) else null
    }
}