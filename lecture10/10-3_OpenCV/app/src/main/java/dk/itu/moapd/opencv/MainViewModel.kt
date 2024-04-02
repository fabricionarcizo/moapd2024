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
package dk.itu.moapd.opencv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

/**
 * A view model sensitive to changes in the `MainActivity` UI components.
 */
class MainViewModel : ViewModel() {

    /**
     * The current selected camera characteristics.
     */
    private var _characteristics = MutableLiveData<Int>()

    /**
     * A `LiveData` which publicly exposes any update in the camera characteristics.
     */
    val characteristics: LiveData<Int>
        get() = _characteristics

    /**
     * This method will be executed when the user interacts with the camera selector component. It
     * sets the selector into the LiveData instance.
     *
     * @param characteristics A set of requirements and priorities used to select a camera.
     */
    fun onCameraCharacteristicsChanged(characteristics: Int) {
        this._characteristics.value = characteristics
    }

    /**
     * The current selected method.
     */
    private var _methodId = MutableLiveData(0)

    /**
     * A `LiveData` which publicly exposes any update in the selected method.
     */
    val methodId: LiveData<Int>
        get() = _methodId

    /**
     * This method will be executed when the user interacts with the method selector component. It
     * sets the method ID into the LiveData instance.
     *
     * @param methodId The selected image analysis method ID.
     */
    fun onMethodChanged(methodId: Int) {
        this._methodId.value = methodId
    }

}
