package com.hongwei.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper


object LocalJsonReaderUtil {
    fun readJsonFileInResource(jsonPath: String): String? {
        try {
            Thread.currentThread().contextClassLoader.getResourceAsStream(jsonPath).use { `in` ->
                //pass InputStream to JSON-Library, e.g. using Jackson
                val mapper = ObjectMapper()
                val jsonNode: JsonNode = mapper.readValue(`in`,
                        JsonNode::class.java)
                return mapper.writeValueAsString(jsonNode)
            }
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}