package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.product.ProductRepository
import com.blackbunny.boomerang.domain.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author kimdoyoon
 * Created 8/29/23 at 4:51 PM
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository
) : ViewModel() {

    private val TAG = "SearchViewModel"

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()


    fun updateSearchKeyword(newValue: TextFieldValue) {
        _uiState.update {
            it.copy(
                searchKeyword = newValue
            )
        }
    }


    /**
     * SearchProductByKeyword
     */
    fun searchProductByKeyword() {
        _uiState.update {
            it.copy(
                searchResult = emptyList()
            )
        }
        viewModelScope.launch {
            productRepository.searchProductByKeyword(_uiState.value.searchKeyword.text)
                .cancellable()
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { searchResult ->
                    searchResult.fold(
                        onSuccess = { product ->
                            Log.i(TAG, "Successfully received data. $product")
                            // Update UI State.
                            _uiState.update {
                                it.copy(
                                    searchResult = it.searchResult.notifyChanged(product)
                                )
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to received data.")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    private suspend fun searchProductByKeywordContinuous() {
        _uiState.update {
            it.copy(
                searchResult = emptyList()
            )
        }
        productRepository.searchProductByKeyword(_uiState.value.searchKeyword.text)
            .cancellable()
            .flowOn(Dispatchers.IO)
            .map { Result.success(it) }
            .catch { emit(Result.failure(it)) }
            .collectLatest { searchResult ->
                searchResult.fold(
                    onSuccess = { product ->
                        Log.i(TAG, "Successfully received data. $product")
                        // Update UI State.
                        _uiState.update {
                            it.copy(
                                searchResult = it.searchResult.notifyChanged(product)
                            )
                        }
                    },
                    onFailure = {
                        Log.d(TAG, "Unable to received data.")
                        it.printStackTrace()
                    }
                )
            }
    }

    private fun List<Product>.notifyChanged(newData: Product): List<Product> {
        return List<Product>(this.size + 1) {
            if (it < this.size) this[it]
            else newData
        }
    }
}

// UiState for Search Screen.
data class SearchUiState(
    val searchKeyword: TextFieldValue = TextFieldValue(),
    val searchResult: List<Product> = emptyList(),
    val isProductDetailRequested: Boolean = false,
    val currentSelectedProduct: Product? = null
)


/*
// Search Result (Use function that returns entire list at once. Should be revised later.
    val searchResultState = snapshotFlow {
        _uiState.value.searchKeyword.text
    }.transformLatest {
        if (it.isNotBlank()) {
            delay(500)
            emit(searchProductByKeywordContinuous())
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Lazily,
        initialValue = emptyList<Product>()
    )
 */