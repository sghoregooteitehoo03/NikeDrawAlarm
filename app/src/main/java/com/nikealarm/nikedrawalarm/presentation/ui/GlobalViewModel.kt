package com.nikealarm.nikedrawalarm.presentation.ui

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nikealarm.nikedrawalarm.data.model.entity.NotificationEntity
import com.nikealarm.nikedrawalarm.domain.model.ProductCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GlobalViewModel @Inject constructor() : ViewModel() {
    // State
    private val _selectedCategory: MutableState<ProductCategory> =
        mutableStateOf(ProductCategory.All)
    private var _notificationEntity: MutableState<NotificationEntity?> = mutableStateOf(null)
    private val _dialogScreen: MutableState<DialogScreen> =
        mutableStateOf(DialogScreen.DialogDismiss)
    private val _event = MutableSharedFlow<ActionEvent>()

    val selectedCategory: State<ProductCategory> = _selectedCategory
    val dialogScreen: State<DialogScreen> = _dialogScreen
    val notificationEntity: State<NotificationEntity?> = _notificationEntity
    val event = _event.asSharedFlow()

    fun setSelectedCategory(category: ProductCategory) {
        _selectedCategory.value = category
        setActionEvent(ActionEvent.ActionSelectCategory(category))
    }

    fun dialogOpen(dialog: DialogScreen) {
        _dialogScreen.value = dialog
    }

    fun setNotificationEntity(data: NotificationEntity?) {
        _notificationEntity.value = data
    }

    fun setActionEvent(actionEvent: ActionEvent) = viewModelScope.launch {
        _event.emit(actionEvent)
    }
}

sealed interface ActionEvent {
    data object ActionNavigationUp : ActionEvent
    data class ActionSelectCategory(val category: ProductCategory) : ActionEvent
    data class ActionShowMessage(val message: String) : ActionEvent
    data object ActionSettingIcon : ActionEvent
    data object ActionNotificationIcon : ActionEvent
}