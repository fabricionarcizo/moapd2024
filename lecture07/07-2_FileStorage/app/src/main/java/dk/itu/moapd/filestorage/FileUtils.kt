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
package dk.itu.moapd.filestorage

import android.content.Context
import android.util.Log
import java.io.*

/**
 * An utility class to manage and persist application data in a text file.
 */
class FileUtils(private val context: Context) {

    /**
     * A set of static attributes used in this class.
     */
    companion object {
        private val TAG = FileUtils::class.qualifiedName
        private const val filename = "data.txt"
    }

    /**
     * Writes the given data to a file. If the data is empty, no action is taken. The data is
     * appended to an existing file or a new file is created if it doesn't exist.
     *
     * @param data The data to be written to the file.
     */
    fun writeDataToFile(data: String) {
        if (data.isEmpty()) return

        try {
            // Open a private file associated with this Context's application package for writing.
            context.openFileOutput(filename, Context.MODE_APPEND)?.use { fileOutputStream ->

                // Convert the input data into bytes.
                val dataInBytes = data.toByteArray()
                val lineSeparator = System.getProperty("line.separator")

                // Write the input data in the text file.
                fileOutputStream.write(dataInBytes)
                fileOutputStream.write(lineSeparator?.toByteArray() ?: byteArrayOf())

                // IMPORTANT: No need to flush/close as we're using Kotlin's use function, which
                // automatically handles closing the resource.
            }
        } catch (ex: IOException) {
            Log.d(TAG, ex.message, ex)
        }
    }

    /**
     * Reads data from a file and returns it as a list of strings. If an error occurs during
     * reading, an empty list is returned.
     *
     * @return List of strings read from the file.
     */
    fun readDataFromFile(): List<String> {
        return try {
            context.openFileInput(filename)?.use { fileInputStream ->
                InputStreamReader(fileInputStream).use { inputStreamReader ->
                    BufferedReader(inputStreamReader).use { bufferedReader ->
                        bufferedReader.readLines()
                    }
                }
            } ?: emptyList()
        } catch (ex: IOException) {
            Log.d(TAG, ex.message, ex)
            emptyList()
        }
    }

    /**
     * Removes data from the file at the specified position.
     *
     * @param data The list of data to remove from the file.
     * @param position The position of the data to remove.
     */
    fun removeDataFromFile(data: List<String>, position: Int) {
        try {
            context.openFileOutput(filename, Context.MODE_PRIVATE)?.use { fileOutputStream ->
                val lineSeparator = System.getProperty("line.separator")
                data.filterIndexed { index, _ -> index != position }
                    .forEach { line ->
                        fileOutputStream.write(line.toByteArray())
                        fileOutputStream.write(lineSeparator?.toByteArray()!!)
                    }
            }
        } catch (ex: IOException) {
            Log.d(TAG, ex.message, ex)
        }
    }

}
