/*
 * MIT License
 *
 * Copyright (c) 2024 Fabricio Batista Narcizo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package dk.itu.moapd.roomstorage

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

/**
 * A view model sensitive to changes in the `Dummy` dataset.
 */
class DummyViewModel(private val repository: DummyRepository) : ViewModel() {

    /**
     * The `LiveData` used to monitor the elements available in the `Dummy` table.
     */
    val names: LiveData<List<Dummy>> = repository.names.asLiveData()

    /**
     * This method inserts a new `Dummy` instance in the dataset. It uses Kotlin Coroutine KTX to
     * execute the database query in the `ViewModel` scope.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    fun insert(dummy: Dummy) = viewModelScope.launch {
        repository.insert(dummy)
    }

    /**
     * This method updates the parameters of a specific `Dummy` instance in the dataset. It uses
     * Kotlin Coroutine KTX to execute the database query in the `ViewModel` scope.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    fun update(dummy: Dummy) = viewModelScope.launch {
        repository.update(dummy)
    }

    /**
     * This method deletes a specific `Dummy` instance from the dataset. It uses Kotlin Coroutine
     * KTX to execute the database query in the `ViewModel` scope.
     *
     * @param dummy An instance of the `Dummy` class.
     */
    fun delete(dummy: Dummy) = viewModelScope.launch {
        repository.delete(dummy)
    }

}

/**
 * This class gets as a parameter the dependencies needed to create `DummyViewModel` instance, i.e.,
 * the `DummyRepository`. The framework will take care of the lifecycle of the `ViewModel`.
 */
class DummyViewModelFactory(private val repository: DummyRepository) : ViewModelProvider.Factory {

    /**
     * Creates a new instance of the `DummyViewModel` class.
     *
     * Default implementation throws [UnsupportedOperationException].
     *
     * @param modelClass A `Class` whose instance is requested.
     *
     * @return A newly created `ViewModel`.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DummyViewModel::class.java))
            @Suppress("UNCHECKED_CAST")
            return DummyViewModel(repository) as T
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
