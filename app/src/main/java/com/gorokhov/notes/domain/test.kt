package com.gorokhov.notes.domain

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMap
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun searchInBackend(query: String): Flow<String> = flow {
    println("Search for: '$query' started")
    delay(2000) // Simulating a long request
    println("Search for: '$query' completed")
    emit("Results for '$query'")
}

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking {
    val queryFlow = flow {
        emit("A")
        delay(500)
        emit("AB")
        delay(500)
        emit("ABC")
        delay(2000) // Pause to let the last request complete
    }

    queryFlow
        .flatMapLatest { query -> searchInBackend(query) }
        .collect { result ->
            println("Received: $result")
        }
}