package com.kalyani.fetch

import com.kalyani.fetch.network.ApiService
import com.kalyani.fetch.network.model.Item
import javax.inject.Inject

class ItemRepository @Inject constructor(
    private val apiService: ApiService
) {
    suspend fun fetchItems(): List<Item> {
        return try {
            apiService.getItems()
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }
}