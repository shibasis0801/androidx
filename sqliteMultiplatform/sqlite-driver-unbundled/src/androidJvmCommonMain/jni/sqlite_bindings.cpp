#include <jni.h>
#include "sqlite3.h"
#include <sstream>
#include <stdlib.h>

int throwException(JNIEnv *env, sqlite3* db, int errorCode) {
    jclass exceptionClass = env->FindClass("java/lang/IllegalStateException");
    std::stringstream errorMessage;
    errorMessage << "Error code: " << errorCode;
    if (db != nullptr) {
        errorMessage << ", message: " <<  sqlite3_errmsg(db);
    }
    int throwResult = env->ThrowNew(exceptionClass, errorMessage.str().c_str());
    return throwResult;
}

extern "C" JNIEXPORT jlong JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteDriverKt_nativeOpen(
        JNIEnv* env,
        jclass clazz,
        jstring name) {
    const char *path = env->GetStringUTFChars(name, nullptr);
    sqlite3 *db;
    int openFlags = SQLITE_OPEN_READWRITE | SQLITE_OPEN_CREATE;
    int rc = sqlite3_open_v2(path, &db, openFlags, nullptr);
    env->ReleaseStringUTFChars(name, path);
    if (rc != SQLITE_OK) {
        throwException(env, NULL, rc);
        return 0;
    }
    return reinterpret_cast<jlong>(db);
}

extern "C" JNIEXPORT jlong JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteConnectionKt_nativePrepare(
        JNIEnv* env,
        jclass clazz,
        jlong dbPointer,
        jstring sqlString) {
    sqlite3* db = reinterpret_cast<sqlite3*>(dbPointer);
    sqlite3_stmt* stmt;
    jsize sqlLength = env->GetStringLength(sqlString);
    // Java / jstring represents a string in UTF-16 encoding.
    const jchar* sql = env->GetStringCritical(sqlString, nullptr);
    int rc = sqlite3_prepare16_v2(db, sql, sqlLength * sizeof(jchar), &stmt, nullptr);
    env->ReleaseStringCritical(sqlString, sql);
    if (rc != SQLITE_OK) {
        throwException(env, db, rc);
        return 0;
    }
    return reinterpret_cast<jlong>(stmt);
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteConnectionKt_nativeClose(
        JNIEnv* env,
        jclass clazz,
        jlong dbPointer) {
    sqlite3 *db = reinterpret_cast<sqlite3*>(dbPointer);
    sqlite3_close_v2(db);
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeBindBlob(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer,
        jint index,
        jbyteArray value) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    jsize valueLength = env->GetArrayLength(value);
    jbyte* blob = static_cast<jbyte*>(env->GetPrimitiveArrayCritical(value, nullptr));
    int rc = sqlite3_bind_blob(stmt, index, blob, valueLength, SQLITE_TRANSIENT);
    env->ReleasePrimitiveArrayCritical(value, blob, JNI_ABORT);
    if (rc != SQLITE_OK) {
        throwException(env, sqlite3_db_handle(stmt), rc);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeBindDouble(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer,
        jint index,
        jdouble value) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    int rc = sqlite3_bind_double(stmt, index, value);
    if (rc != SQLITE_OK) {
        throwException(env, sqlite3_db_handle(stmt), rc);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeBindLong(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer,
        jint index,
        jlong value) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    int rc = sqlite3_bind_int64(stmt, index, value);
    if (rc != SQLITE_OK) {
        throwException(env, sqlite3_db_handle(stmt), rc);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeBindText(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer,
        jint index,
        jstring value) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    jsize valueLength = env->GetStringLength(value);
    const jchar* text = env->GetStringCritical(value, NULL);
    int rc = sqlite3_bind_text16(stmt, index, text, valueLength * sizeof(jchar), SQLITE_TRANSIENT);
    env->ReleaseStringCritical(value, text);
    if (rc != SQLITE_OK) {
        throwException(env, sqlite3_db_handle(stmt), rc);
    }
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeBindNull(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    int rc = sqlite3_bind_null(stmt, index);
    if (rc != SQLITE_OK) {
        throwException(env, sqlite3_db_handle(stmt), rc);
    }
}

extern "C" JNIEXPORT jboolean JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeStep(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    int rc = sqlite3_step(stmt);
    if (rc == SQLITE_ROW) {
        return JNI_TRUE;
    }
    if (rc == SQLITE_DONE) {
        return JNI_FALSE;
    }
    throwException(env, sqlite3_db_handle(stmt), rc);
    return JNI_FALSE;
}

extern "C" JNIEXPORT jbyteArray JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetBlob(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    const void *blob = sqlite3_column_blob(stmt, index);
    int size = sqlite3_column_bytes(stmt, index);
    // TODO(b/304297717): Use sqlite3_errcode() to check for out-of-memory
    jbyteArray byteArray = env->NewByteArray(size);
    if (size > 0) {
        env->SetByteArrayRegion(byteArray, 0, size, static_cast<const jbyte*>(blob));
    }
    return byteArray;
}

extern "C" JNIEXPORT jdouble JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetDouble(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    return sqlite3_column_double(stmt, index);
}

extern "C" JNIEXPORT jlong JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetLong(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    return sqlite3_column_int64(stmt, index);
}

extern "C" JNIEXPORT jstring JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetText(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    // Java / jstring represents a string in UTF-16 encoding.
    const jchar *text = static_cast<const jchar*>(sqlite3_column_text16(stmt, index));
    if (text) {
        int length = sqlite3_column_bytes16(stmt, index) / sizeof(jchar);
        return env->NewString(text, length);
    }
    // TODO: Use sqlite3_errcode() to check for out-of-memory
    return nullptr;
}

extern "C" JNIEXPORT jint JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetColumnCount(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    return sqlite3_column_count(stmt);
}

extern "C" JNIEXPORT jstring JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetColumnName(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    const char *name = sqlite3_column_name(stmt, index);
    if (name == NULL) {
        // TODO: throw out-of-memory exception
    }
    return env->NewStringUTF(name);
}

extern "C" JNIEXPORT jint JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeGetColumnType(
        JNIEnv *env,
        jclass clazz,
        jlong stmtPointer,
        jint index) {
    sqlite3_stmt *stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    return sqlite3_column_type(stmt, index);
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeReset(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    sqlite3_reset(stmt);
}

extern "C" JNIEXPORT void JNICALL
Java_androidx_sqliteMultiplatform_unbundled_UnbundledSQLiteStatementKt_nativeClose(
        JNIEnv* env,
        jclass clazz,
        jlong stmtPointer) {
    sqlite3_stmt* stmt = reinterpret_cast<sqlite3_stmt*>(stmtPointer);
    sqlite3_finalize(stmt);
}
