package com.nikealarm.nikedrawalarm.presentation.favoriteMoreScreen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.nikealarm.nikedrawalarm.domain.model.JoinedProductType
import com.nikealarm.nikedrawalarm.domain.usecase.GetPagingJoinedProductUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class FavoriteMoreViewModel @Inject constructor(
    private val getPagingJoinedProductUseCase: GetPagingJoinedProductUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _uiState = MutableStateFlow(FavoriteMoreUiState())
    val uiState = _uiState.stateIn(
        viewModelScope,
        SharingStarted.Eagerly,
        initialValue = _uiState.value
    )

    init {
        try {
            val typeJson = savedStateHandle.get<String>("type") ?: ""
            val joinedProductType = Json.decodeFromString(JoinedProductType.serializer(), typeJson)

            _uiState.update {
                it.copy(
                    products = getPagingJoinedProductUseCase(joinedProductType).cachedIn(
                        viewModelScope
                    ),
                    sendType = joinedProductType
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}