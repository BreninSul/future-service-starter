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

package com.github.breninsul.futurestarter.service

import java.time.Duration
import java.util.concurrent.CompletableFuture

/**
 * Registers a task with a specified id.
 * @param T The type of the result returned by the task
 * @param id The identification of the task
 * @return A future representing the result of the task
 */
inline fun <reified T : Any> FutureService.registerTask(id: Any): CompletableFuture<T> = registerTask(id, T::class)

/**
 * Registers a task with a specified id and timeout.
 * @param T The type of the result returned by the task
 * @param id The identification of the task
 * @param timeout The maximum time to wait for the task to complete
 * @return A future representing the result of the task
 */
inline fun <reified T : Any> FutureService.registerTask(id: Any, timeout: Duration): CompletableFuture<T> = registerTask(id, T::class, timeout)

/**
 * Waits for a task with a specified id to complete and returns its result.
 * @param T The type of the result returned by the task
 * @param id The identification of the task
 * @param timeout The maximum time to wait for the task to complete
 * @return The result of the completed task
 */
inline fun <reified T : Any> FutureService.waitResult(id: Any, timeout: Duration): T = waitResult(id, T::class, timeout)

/**
 * Waits for a task with a specified id to complete and returns its result.
 * @param T The type of the result returned by the task
 * @param id The identification of the task
 * @return The result of the completed task
 */
inline fun <reified T : Any> FutureService.waitResult(id: Any): T = waitResult(id, T::class)

/**
 * Completes a task with the specified id and result.
 * @param T The type of the result returned by the task
 * @param id The identification of the task
 * @param result The result of the completed task
 */
inline fun <reified T : Any> FutureService.complete(id: Any,result:T)= complete(id, T::class,result)

/**
 * Completes a task exceptionally with the specified id and result.
 * @param T The type of the result returned by the task
 * @param id The identification of the task
 * @param result The exception thrown by the task
 */
inline fun <reified T : Any> FutureService.completeExceptionally(id: Any,result:Throwable)= completeExceptionally(id, T::class,result)