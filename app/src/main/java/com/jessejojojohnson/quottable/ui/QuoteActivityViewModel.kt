package com.jessejojojohnson.quottable.ui

import android.content.Context
import androidx.lifecycle.*
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.jessejojojohnson.quottable.data.Quote
import com.jessejojojohnson.quottable.data.QuotesRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class QuoteActivityViewModel(
    private val quotesRepository: QuotesRepository
) : ViewModel() {

    private var _quote = Quote.EMPTY

    fun getQuoteFlow() = quotesRepository
        .getPreviousFromDataStore()
        .distinctUntilChanged()
        .onEach {
            _quote = it
        }

    fun changeColor(color: Int) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(
                    background = color,
                    backgroundImageUri = Quote.EMPTY.backgroundImageUri
                )
            )
        }
    }

    fun setBackgroundImage(uriString: String) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(
                    backgroundImageUri = uriString,
                    background = Quote.EMPTY.background
                )
            )
        }
    }

    fun changeFont(fontName: String) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(fontName = fontName)
            )
        }
    }

    fun updateText(text: String) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(text = text)
            )
        }
    }

    fun updateText(text: String, attribution: String) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(text = text, attribution = attribution)
            )
        }
    }

    fun updateAttribution(text: String) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(attribution = text)
            )
        }
    }

    fun setOverlayMask(maskPercentage: Int) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(maskPercentage = maskPercentage)
            )
        }
    }

    fun changeFontSize(fontSize: Float) {
        viewModelScope.launch {
            quotesRepository.saveQuoteToDataStore(
                _quote.copy(fontSize = fontSize)
            )
        }
    }

    companion object {
        val Factory : ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val repository = QuotesRepository(this[APPLICATION_KEY] as Context)
                QuoteActivityViewModel(repository)
            }
        }
    }
}