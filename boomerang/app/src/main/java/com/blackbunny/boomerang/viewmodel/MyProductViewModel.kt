package com.blackbunny.boomerang.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blackbunny.boomerang.data.product.ProductRepository
import com.blackbunny.boomerang.domain.product.FetchProductForCardUseCase
import com.blackbunny.boomerang.domain.product.Product
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyProductViewModel @Inject constructor(
    private val fetchProductUseCase: FetchProductForCardUseCase
) : ViewModel() {

    private val TAG = "MyProductViewModel"

    private val _uiState = MutableStateFlow(MyProductUiState())
    val uiState = _uiState.asStateFlow()

    fun requestProductOwnedBy(userId: String) {
        _uiState.update {
            it.copy(
                productList = emptyList()
            )
        }

        viewModelScope.launch {
            fetchProductUseCase.invoke(userId)
                .flowOn(Dispatchers.IO)
                .map { Result.success(it) }
                .catch { emit(Result.failure(it)) }
                .collectLatest { result ->
                    result.fold(
                        onSuccess = { product ->
                            Log.d(TAG, "Product retrieved.")
                            _uiState.update {
                                it.copy(
                                    productList = it.productList.add(product)
                                )
                            }
                        },
                        onFailure = {
                            Log.d(TAG, "Unable to retrieve products")
                            it.printStackTrace()
                        }
                    )
                }
        }
    }

    private fun <T> List<T>.add(newData: T): List<T> {
        return List<T>(this.size + 1) {
            if (it < this.size) this[it]
            else newData
        }
    }

}

data class MyProductUiState(
    val productList: List<Product> = emptyList()
)