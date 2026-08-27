package com.bitgranules.androidproject.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bitgranules.androidproject.data.CustomApiConfig
import com.bitgranules.androidproject.data.QuoteApiService
import com.bitgranules.androidproject.data.QuoteStruct
import com.bitgranules.androidproject.data.SettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit


class QuoteModelView(private val settingsRepository: SettingsRepository) : ViewModel() {

    //DarkMode               DarkMode
    val isDarkMode: StateFlow<Boolean> = settingsRepository.isDarkMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), true
    )

    fun toggleDarkMode() {
        viewModelScope.launch {
            settingsRepository.setDarkMode(enabled = !isDarkMode.value)
        }
    }


    //SnackBar         SNACK BAR
    private val _snackbarChannel = Channel<String>(Channel.BUFFERED)
    val snackbarEvents = _snackbarChannel.receiveAsFlow()
    fun showTransientMessage(mssg: String) {
        viewModelScope.launch { _snackbarChannel.send(mssg) }
    }


    //Quote                   Quote

    val DEFAULT_ZENQUOTES = CustomApiConfig(
        id = "default_zen",
        name = "Default (ZenQuotes)",
        fullUrl = "https://zenquotes.io/api/quotes",
        isArray = true,
        contentKey = "q",
        authorKey = "a"
    )

    private val jsonEngine = Json { ignoreUnknownKeys = true }
    private val customClient = OkHttpClient.Builder().connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS).build()
    private val retrofit = Retrofit.Builder().baseUrl("https://localhost/").client(customClient)
        .addConverterFactory(jsonEngine.asConverterFactory("application/json".toMediaType()))
        .build()

    private val apiService = retrofit.create(QuoteApiService::class.java)

    val customApis: StateFlow<List<CustomApiConfig>> = settingsRepository.customApis.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    // ---Immutable batch pool & pointer index
    // Change your old initialization to a read-through state property linked to disk storage
    val cachedQuoteList: StateFlow<List<QuoteStruct>> = settingsRepository.cachedQuotes.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList() // Default empty state until Disk I/O responds (~10ms)
    )

    private val _currentQuoteIndex = MutableStateFlow(0)
    val currentQuoteIndex: StateFlow<Int> = _currentQuoteIndex.asStateFlow()

    private val _isFetching = MutableStateFlow(false)
    val isFetching: StateFlow<Boolean> = _isFetching.asStateFlow()

    val activeQuote: StateFlow<QuoteStruct> =
        combine(cachedQuoteList, _currentQuoteIndex) { list, index ->
            if (list.isNotEmpty() && index in list.indices) {
                list[index]
            } else {
                QuoteStruct(
                    "Load a fresh batch of Quotes to begin.", "System"
                )
            }
        }.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            QuoteStruct("Load a fresh batch of Quotes to begin.", "System")
        )

    fun setCurrentQuoteIndex(currentPage: Int) {
        _currentQuoteIndex.value = currentPage
    }

    fun navigateToNextQuote() {
        if (currentQuoteIndex.value < cachedQuoteList.value.lastIndex) {
            _currentQuoteIndex.value += 1
        }
    }

    fun navigateToPreviousQuote() {
        if (_currentQuoteIndex.value > 0) {
            _currentQuoteIndex.value -= 1
        }
    }

    fun fetchFreshQuoteBatch(config: CustomApiConfig = DEFAULT_ZENQUOTES) {
        if (_isFetching.value) return

        viewModelScope.launch(Dispatchers.IO) {
            _isFetching.value = true
            try {
                val rawResponse = apiService.getCustomQuote(config.fullUrl)
                val freshBatch = mutableListOf<QuoteStruct>()

                if (rawResponse is JsonArray) {
                    rawResponse.forEach { element ->
                        val obj = element.jsonObject
                        val content = obj[config.contentKey]?.jsonPrimitive?.content ?: ""
                        val author = obj[config.authorKey]?.jsonPrimitive?.content ?: "Unknown"
                        if (content.isNotEmpty()) freshBatch.add(QuoteStruct(content, author))
                    }
                } else if (rawResponse is JsonObject) {
                    val content = rawResponse[config.contentKey]?.jsonPrimitive?.content ?: ""
                    val author = rawResponse[config.authorKey]?.jsonPrimitive?.content ?: "Unknown"
                    if (content.isNotEmpty()) freshBatch.add(QuoteStruct(content, author))
                }

                if (freshBatch.isNotEmpty()) {
                    // PERSISTENCE ENGINE TRIGGERS HERE:
                    // Saves data directly to DataStore file infrastructure via disk IO
                    settingsRepository.saveQuoteCache(freshBatch)
                    _currentQuoteIndex.value = 0
                }

            } catch (e: Exception) {
                val errrr = e.localizedMessage ?: "Unknown Network Error"
                showTransientMessage("Error: $errrr")
            } finally {
                _isFetching.value = false
            }
        }
    }

    fun clearCachedQuotes() {
        viewModelScope.launch {
            settingsRepository.saveQuoteCache(emptyList())
            _currentQuoteIndex.value = 0
        }
    }

    fun addUserApi(config: CustomApiConfig) {
        viewModelScope.launch {
            settingsRepository.addUserApi(config)
            showTransientMessage("Saved '${config.name}' Source Layout.")
        }
    }

    fun deleteUserApi(config: CustomApiConfig) {
        viewModelScope.launch {
            settingsRepository.deleteUserApi(config)
            showTransientMessage("Source configuration dropped.")
        }
    }

    private val _defaultQuote = MutableStateFlow<QuoteStruct>(
        QuoteStruct(
            "In the middle of difficulty lies opportunity.", "Albert Einstein"
        )
    )
    val defaultQuote: StateFlow<QuoteStruct> = _defaultQuote.asStateFlow()

    //BG IMAGES        BG images
    val bgImages: StateFlow<List<String>> = settingsRepository.bgImages.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )
    private val _selectedBgIndex = MutableStateFlow(0)
    val selectedBgIndex: StateFlow<Int> = _selectedBgIndex.asStateFlow()

    fun setSelectedBgIndex(index: Int) {
        _selectedBgIndex.value = index
    }

    fun addMultipleBgImages(uris: List<String>) {
        viewModelScope.launch {
            settingsRepository.addMultipleBgImages(uris)
        }
        showTransientMessage("Added ${uris.size} Images")
    }

    fun deleteBgImage(index: Int) {
        viewModelScope.launch {
            if (index in bgImages.value.indices) {
                val imageTarget = bgImages.value[index]

                // If deleting the active item, change index away from it right before dropping it
                if (_selectedBgIndex.value == index && _selectedBgIndex.value > 0) {
                    _selectedBgIndex.value -= 1
                }

                settingsRepository.deleteBgImage(imageTarget)
                showTransientMessage("Image removed from background options.")
            }
        }
    }

    fun navigateToNextBg() {
        if (_selectedBgIndex.value < bgImages.value.lastIndex) {
            _selectedBgIndex.value += 1
        }
    }

    fun navigateToPreviousBg() {
        if (_selectedBgIndex.value > 0) {
            _selectedBgIndex.value -= 1
        }
    }

    fun setSelectedBgByString(imageUri: String) {
        val index = bgImages.value.indexOf(imageUri)
        _selectedBgIndex.value = index
    }

    fun deleteBgImageByString(imageUri: String) {
        val index = bgImages.value.indexOf(imageUri)
        deleteBgImage(index)
    }

}