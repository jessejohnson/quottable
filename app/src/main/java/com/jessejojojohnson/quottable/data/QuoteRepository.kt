package com.jessejojojohnson.quottable.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import com.jessejojojohnson.quottable.dataStore
import com.jessejojojohnson.quottable.ld
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class QuotesRepository(private val context: Context) {

    private val PREVIOUS_QUOTE = stringPreferencesKey("previous_quote")
    private val gson = Gson()

    fun getPreviousFromDataStore(): Flow<Quote> {
        return context.dataStore.data.map {
            try {
                val quoteJsonString = it[PREVIOUS_QUOTE]
                val quote = gson.fromJson(quoteJsonString, Quote::class.java)
                ld("Retrieved $quote from DataStore")
                quote ?: Quote.EMPTY
            } catch (e: Exception) {
                Quote.EMPTY
            }
        }
    }

    suspend fun saveQuoteToDataStore(quote: Quote) {
        context.dataStore.edit {
            it[PREVIOUS_QUOTE] = gson.toJson(quote)
        }
    }
}