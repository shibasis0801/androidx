/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:JvmName("UnbundledSQLiteStatementKt")

package androidx.sqliteMultiplatform.unbundled

import androidx.sqliteMultiplatform.SQLiteStatement

actual class UnbundledSQLiteStatement(
    private val connectionPointer: Long,
    private val statementPointer: Long
) : SQLiteStatement {
    override fun bindBlob(index: Int, value: ByteArray) {
        nativeBindBlob(statementPointer, index, value)
    }

    override fun bindDouble(index: Int, value: Double) {
        nativeBindDouble(statementPointer, index, value)
    }

    override fun bindLong(index: Int, value: Long) {
        nativeBindLong(statementPointer, index, value)
    }

    override fun bindText(index: Int, value: String) {
        nativeBindText(statementPointer, index, value)
    }

    override fun bindNull(index: Int) {
        nativeBindNull(statementPointer, index)
    }

    override fun getBlob(index: Int): ByteArray {
        return nativeGetBlob(statementPointer, index)
    }

    override fun getDouble(index: Int): Double {
        return nativeGetDouble(statementPointer, index)
    }

    override fun getLong(index: Int): Long {
        return nativeGetLong(statementPointer, index)
    }

    override fun getText(index: Int): String {
        return nativeGetText(statementPointer, index)
    }

    override fun isNull(index: Int): Boolean {
        return nativeGetColumnType(statementPointer, index) == COLUMN_TYPE_NULL
    }

    override fun getColumnCount(): Int {
        return nativeGetColumnCount(statementPointer)
    }

    override fun getColumnName(index: Int): String {
        return nativeGetColumnName(statementPointer, index)
    }

    override fun step(): Boolean {
        return nativeStep(statementPointer)
    }

    override fun reset() {
        nativeReset(statementPointer)
    }

    override fun close() {
        nativeClose(statementPointer)
    }

    companion object {
        private const val COLUMN_TYPE_INTEGER = 1
        private const val COLUMN_TYPE_FLOAT = 2
        private const val COLUMN_TYPE_TEXT = 3
        private const val COLUMN_TYPE_BLOB = 4
        private const val COLUMN_TYPE_NULL = 5
    }
}

private external fun nativeBindBlob(pointer: Long, index: Int, value: ByteArray)
private external fun nativeBindDouble(pointer: Long, index: Int, value: Double)
private external fun nativeBindLong(pointer: Long, index: Int, value: Long)
private external fun nativeBindText(pointer: Long, index: Int, value: String)
private external fun nativeBindNull(pointer: Long, index: Int)
private external fun nativeStep(pointer: Long): Boolean
private external fun nativeGetBlob(pointer: Long, index: Int): ByteArray
private external fun nativeGetDouble(pointer: Long, index: Int): Double
private external fun nativeGetLong(pointer: Long, index: Int): Long
private external fun nativeGetText(pointer: Long, index: Int): String
private external fun nativeGetColumnType(pointer: Long, index: Int): Int
private external fun nativeGetColumnCount(pointer: Long): Int
private external fun nativeGetColumnName(pointer: Long, index: Int): String
private external fun nativeReset(pointer: Long)
private external fun nativeClose(pointer: Long)
