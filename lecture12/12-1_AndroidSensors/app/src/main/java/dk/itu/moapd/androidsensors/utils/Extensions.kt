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
package dk.itu.moapd.androidsensors.utils

import android.hardware.SensorManager
import java.lang.Float.max
import java.lang.Float.min

/**
 * The `Extensions` object has a set of static methods used to improve some software development
 * tasks.
 */
object Extensions {

    /**
     * Convert the current radians value to degrees.
     *
     * @return An integer degree value between -360 and +360.
     */
    fun Float.toDegrees(): Int = (this * 180 / Math.PI).toInt()

    /**
     * Normalizes the gravity value to a percentage within the range [-100, 100].
     *
     * @return The normalized gravity value as a percentage.
     */
    fun Float.normalizeGravity(): Int =
        ((min(max(this, -SensorManager.STANDARD_GRAVITY), SensorManager.STANDARD_GRAVITY)
                + SensorManager.STANDARD_GRAVITY) /
                (2f * SensorManager.STANDARD_GRAVITY) * 100).toInt()

    /**
     * Normalizes the given value to a percentage within the range [0, 100].
     *
     * @return The normalized value as a percentage.
     */
    fun Float.normalizeValue(): Int = (((this + 1) / 2f) * 100).toInt()

}
