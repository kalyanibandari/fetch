package com.kalyani.fetch

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kalyani.fetch.network.UiState
import com.kalyani.fetch.network.model.Item
import com.kalyani.fetch.network.model.ItemGroup
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor (
    private val repository: ItemRepository
) : ViewModel() {
    private val _uiState = MutableLiveData<UiState<List<Item>>>()
    val uiState: LiveData<UiState<List<Item>>> = _uiState

    init {
        fetchItems()
    }

    private fun fetchItems() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            try {
                val items = repository.fetchItems()
                val filteredItems = items.asSequence()
                    .filter { it.name != null && it.name.isNotBlank() }
                    .groupBy { it.listId }
                    .map { (listId, items) ->
                        ItemGroup(
                            listId,
                            items.sortedBy {
                                it.name.substringAfter("Item ").toIntOrNull() ?: Int.MAX_VALUE
                            }
                        )
                    }
                    .sortedBy { it.listId }
                    .flatMap { it.items }
                    .toList()
                _uiState.value = UiState.Success(filteredItems)
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "An unknown error occurred")
            }
        }
    }
}