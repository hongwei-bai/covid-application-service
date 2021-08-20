package com.hongwei.model.jpa.au.converter

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.hongwei.model.covid19.CovidAuDay
import java.lang.reflect.Type
import javax.persistence.AttributeConverter
import javax.persistence.Converter

@Converter
class CovidAuListConverter : AttributeConverter<List<CovidAuDay>, String?> {
	override fun convertToDatabaseColumn(stringList: List<CovidAuDay>): String? {
		return stringList.let { Gson().toJson(stringList) }
	}

	override fun convertToEntityAttribute(string: String?): List<CovidAuDay> {
		val listType: Type = object : TypeToken<List<CovidAuDay>>() {}.type
		return if (string != null) Gson().fromJson(string, listType) else emptyList()
	}
}