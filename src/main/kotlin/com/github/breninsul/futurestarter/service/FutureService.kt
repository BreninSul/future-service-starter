/*
 * MIT License
 *
 * Copyright (c) 2024 BreninSul
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

/**
 * Spring service for handling futures
 */
package com.github.breninsul.futurestarter.service

import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutionException
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.reflect.KClass

interface FutureService {
    /**
     * Represents the current logging level being used
     *
     * @return the current logging level
     */
    val loggingLevel: Level

    /**
     * Represents a task with a specific ID and class
     */
    data class TaskId(val id: Any, val resultClass: KClass<*>) {
        override fun toString(): String {
            return "${resultClass.simpleName}:$id"
        }
    }

    /**
     * Represents a result with a completion future, an expiry time, and a creation timestamp
     */
    data class Result<T>(val result: CompletableFuture<T>, val expireTime: LocalDateTime, val createTimestamp: Long = System.currentTimeMillis())

    /**
     * Registers a task with a specific ID, class, and timeout
     *
     * @param id the task ID
     * @param resultClass the class of the result
     * @param timeout the timeout duration
     * @return a CompletableFuture of type T
     */
    fun <T : Any> registerTask(
        id: Any,
        resultClass: KClass<T>,
        timeout: Duration,
    ): CompletableFuture<T>

    /**
     * Registers a task with a specific ID and class
     *
     * @param id the task ID
     * @param resultClass the class of the result
     * @return a CompletableFuture of type T
     */
    fun <T : Any> registerTask(
        id: Any,
        resultClass: KClass<T>,
    ): CompletableFuture<T>

    /**
     * Method to register a task and wait for the result.
     *
     * @param id the task ID
     * @param resultClass the class of the result
     * @return the return type of the task
     * @throws Throwable if there's any exception during the execution of the task
     */
    fun <T : Any> waitResult(
        id: Any,
        resultClass: KClass<T>,
    ): T {
        try {
            return registerTask(id, resultClass).get()
        } catch (t: ExecutionException) {
            throw t.cause!!
        }
    }

    /**
     * Method to register a task, wait for the result, and specify a timeout duration.
     *
     * @param id the task ID
     * @param resultClass the class of the result
     * @param timeout the timeout duration
     * @return the return type of the task
     * @throws Throwable if there's any exception during the execution of the task
     */
    fun <T : Any> waitResult(
        id: Any,
        resultClass: KClass<T>,
        timeout: Duration,
    ): T {
        try {
            return registerTask(id, resultClass, timeout).get()
        } catch (t: ExecutionException) {
            throw t.cause!!
        }
    }

    /**
     * Completes a task with a specific ID, class
     *
     * @param id the task ID
     * @param resultClass the class of the result
     * @param result the result of the task
     */
    fun <T : Any> complete(
        id: Any,
        resultClass: KClass<T>,
        result: T,
    )

    /**
     * Mark a task as exceptionally completed with a specific ID, class, and exception
     *
     * @param id the task ID
     * @param resultClass the class of the result
     * @param result the exception that was thrown
     */
    fun <T : Any> completeExceptionally(
        id: Any,
        resultClass: KClass<T>,
        result: Throwable,
    )

    /**
     * Get a list of all tasks that have expired
     *
     * @return a list of all expired task IDs
     */
    fun getExpired(): List<TaskId>

    /**
     * A companion object to handle logging
     */
    companion object {
        internal val logger = Logger.getLogger(this::class.java.name)
    }
}
