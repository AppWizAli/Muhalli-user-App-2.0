package com.hiskytechs.muhallinewuserapp.network

import android.net.Uri
import android.os.Handler
import android.os.Looper
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executors

object ApiConfig {
    const val BASE_URL = "https://hiskytechs.com/muhali/api/index.php"
    const val STATIC_BUYER_ID = 1
    const val STATIC_SUPPLIER_ID = 1
}

object AppSession {
    var buyerId: Int = ApiConfig.STATIC_BUYER_ID
    var supplierId: Int = ApiConfig.STATIC_SUPPLIER_ID
}

class ApiException(message: String) : Exception(message)

object BackgroundWork {
    private val executor = Executors.newCachedThreadPool()
    private val mainHandler = Handler(Looper.getMainLooper())

    fun <T> run(
        task: () -> T,
        onSuccess: (T) -> Unit,
        onError: (String) -> Unit
    ) {
        executor.execute {
            try {
                val result = task()
                mainHandler.post { onSuccess(result) }
            } catch (throwable: Throwable) {
                val message = throwable.message?.takeIf { it.isNotBlank() } ?: "Something went wrong."
                mainHandler.post { onError(message) }
            }
        }
    }
}

object ApiClient {
    fun getDataObject(endpoint: String, queryParams: Map<String, Any?> = emptyMap()): JSONObject {
        val response = requestJson(
            method = "GET",
            endpoint = endpoint,
            queryParams = queryParams
        )
        val data = response.opt("data")
        return data as? JSONObject ?: JSONObject()
    }

    fun getDataArray(endpoint: String, queryParams: Map<String, Any?> = emptyMap()): JSONArray {
        val response = requestJson(
            method = "GET",
            endpoint = endpoint,
            queryParams = queryParams
        )
        val data = response.opt("data")
        return data as? JSONArray ?: JSONArray()
    }

    fun postDataObject(endpoint: String, bodyParams: Map<String, Any?> = emptyMap()): JSONObject {
        val response = requestJson(
            method = "POST",
            endpoint = endpoint,
            bodyParams = bodyParams
        )
        val data = response.opt("data")
        return data as? JSONObject ?: JSONObject()
    }

    fun postDataArray(endpoint: String, bodyParams: Map<String, Any?> = emptyMap()): JSONArray {
        val response = requestJson(
            method = "POST",
            endpoint = endpoint,
            bodyParams = bodyParams
        )
        val data = response.opt("data")
        return data as? JSONArray ?: JSONArray()
    }

    private fun requestJson(
        method: String,
        endpoint: String,
        queryParams: Map<String, Any?> = emptyMap(),
        bodyParams: Map<String, Any?> = emptyMap()
    ): JSONObject {
        val url = URL(buildUrl(endpoint, queryParams))
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 15000
            readTimeout = 15000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/json; charset=UTF-8")
            doInput = true
        }

        if (method == "POST") {
            connection.doOutput = true
            connection.outputStream.use { output ->
                output.write(mapToJsonObject(bodyParams).toString().toByteArray(StandardCharsets.UTF_8))
            }
        }

        return try {
            val statusCode = connection.responseCode
            val body = readBody(
                if (statusCode in 200..299) {
                    connection.inputStream
                } else {
                    connection.errorStream ?: connection.inputStream
                }
            )

            val json = if (body.isBlank()) JSONObject() else JSONObject(body)
            if (!json.optBoolean("success", statusCode in 200..299)) {
                throw ApiException(json.optString("message", "Request failed."))
            }
            json
        } finally {
            connection.disconnect()
        }
    }

    private fun buildUrl(endpoint: String, queryParams: Map<String, Any?>): String {
        val uriBuilder = Uri.parse(ApiConfig.BASE_URL).buildUpon()
            .appendQueryParameter("endpoint", endpoint)

        queryParams.forEach { (key, value) ->
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value.toString())
            }
        }

        return uriBuilder.build().toString()
    }

    private fun readBody(stream: InputStream?): String {
        if (stream == null) return ""
        return BufferedReader(InputStreamReader(stream)).use { reader ->
            buildString {
                var line = reader.readLine()
                while (line != null) {
                    append(line)
                    line = reader.readLine()
                }
            }
        }
    }

    private fun mapToJsonObject(values: Map<String, Any?>): JSONObject {
        val json = JSONObject()
        values.forEach { (key, value) ->
            json.put(key, toJsonValue(value))
        }
        return json
    }

    private fun toJsonValue(value: Any?): Any {
        return when (value) {
            null -> JSONObject.NULL
            is JSONObject -> value
            is JSONArray -> value
            is Map<*, *> -> {
                val nested = JSONObject()
                value.forEach { (nestedKey, nestedValue) ->
                    if (nestedKey != null) {
                        nested.put(nestedKey.toString(), toJsonValue(nestedValue))
                    }
                }
                nested
            }
            is Iterable<*> -> {
                val array = JSONArray()
                value.forEach { item -> array.put(toJsonValue(item)) }
                array
            }
            is Array<*> -> {
                val array = JSONArray()
                value.forEach { item -> array.put(toJsonValue(item)) }
                array
            }
            else -> value
        }
    }
}
