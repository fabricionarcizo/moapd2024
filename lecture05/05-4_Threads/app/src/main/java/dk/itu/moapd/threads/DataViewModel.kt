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
package dk.itu.moapd.threads

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel

/**
 * A view model sensitive to changes in the `Fragments` UI components.
 */
class DataViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    /**
     * A set of private constants used in this class.
     */
    companion object {
        private const val CONT_KEY = "CONT_KEY"
        private const val STATUS_KEY = "STATUS_KEY"
    }

    /**
     * The current value showing in the progress bar.
     */
    private val _cont: MutableLiveData<Int> by lazy {
        savedStateHandle.getLiveData(CONT_KEY, 100)
    }

    /**
     * A flag to control the start/stop button in the application.
     */
    var status: Boolean
        get() = savedStateHandle.get<Boolean>(STATUS_KEY) ?: false
        set(value) = savedStateHandle.set(STATUS_KEY, value)

    /**
     * A LiveData which publicly exposes any update in the cont variable.
     */
    val cont: LiveData<Int>
        get() = _cont

    /**
     * This method resets `cont` variable.
     */
    fun resetCont() {
        _cont.value = 0
    }

    /**
     * This method increments the current `cont` variable in one and reset it when its value is more
     * than 100.
     */
    fun increaseCont() {
        _cont.value = (_cont.value?.plus(1))?.mod(101)
    }

}
