package com.ninstudio.truthordare.data.repository

import android.content.Context
import com.ninstudio.truthordare.data.model.Question
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.IOException

class QuestionRepository(private val context: Context) {
    private val gson = Gson()

    fun loadQuestions(): List<Question> {
        val jsonString = try {
            context.assets.open("questions.json").bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return emptyList()
        }

        val listType = object : TypeToken<List<Question>>() {}.type
        return gson.fromJson(jsonString, listType)
    }
}
