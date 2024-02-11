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

package io.github.breninsul.futurestarter.service

import io.github.breninsul.futurestarter.service.FutureService.Companion.logger
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit
import java.util.logging.Level
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

/**
 * The default implementation of [FutureService].
 *
 * @property timeoutClassMap A mapping of class representations of [KClass] to their corresponding execution [Duration].
 * @property defaultTimeout A [Duration] that would be returned when class has no specific timeout.
 * @property loggingLevel The level of logging for this service.
 */
open class DefaultFutureService(
    protected val timeoutClassMap: Map<KClass<*>, Duration>,
    protected val defaultTimeout: Duration,
    override val loggingLevel: Level = Level.INFO,
) : FutureService {
    /**
     * The Map holding the Futures mapped by [FutureService.TaskId].
     */
    protected val resultMap: MutableMap<FutureService.TaskId, FutureService.Result<*>> = mutableMapOf()

    /**
     * {@inheritDoc}
     */
    override fun <T : Any> registerTask(
        id: Any,
        resultClass: KClass<T>,
    ): CompletableFuture<T> {
        return registerTask(id, resultClass, getTimeout(resultClass))
    }

    /**
     * {@inheritDoc}
     */
    override fun <T : Any> registerTask(
        id: Any,
        resultClass: KClass<T>,
        timeout: Duration,
    ): CompletableFuture<T> {
        val taskId = FutureService.TaskId(id, resultClass)
        val future =
            CompletableFuture<T>()
                .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                .whenComplete { _, exception ->
                    if (exception != null) {
                        logger.log(Level.SEVERE, "Error while processing Future $taskId", exception)
                    } else {
                        logger.log(loggingLevel, "Callback save image completed $taskId")
                    }
                    resultMap.remove(taskId)
                }
        resultMap[taskId] = FutureService.Result(future, LocalDateTime.now().plus(timeout))
        return future
    }

    /**
     * {@inheritDoc}
     */
    override fun <T : Any> complete(
        id: Any,
        resultClass: KClass<T>,
        result: T,
    ) {
        val taskId = FutureService.TaskId(id, resultClass)
        return try {
            val futureTask = resultMap[taskId]
            checkNotNull(futureTask) { "Task is not exist. May validTill is exceeded?" }
            val completableFuture = futureTask.result as CompletableFuture<T>
            if (!completableFuture.isDone) {
                completableFuture.complete(result)
            }
            resultMap.remove(taskId)
            logger.log(
                loggingLevel,
                "Future $taskId completed took ${System.currentTimeMillis() - futureTask.createTimestamp}ms",
            )
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error while complete Future $taskId", t)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun <T : Any> completeExceptionally(
        id: Any,
        resultClass: KClass<T>,
        result: Throwable,
    ) {
        val taskId = FutureService.TaskId(id, resultClass)
        return try {
            val futureTask = resultMap[taskId] ?: return
            val completableFuture = futureTask.result as CompletableFuture<T>
            if (!completableFuture.isDone) {
                futureTask.result.completeExceptionally(result)
            }
            resultMap.remove(taskId)
            logger.log(
                loggingLevel,
                "Future $taskId completeExceptionally with ${result.javaClass}:${result.message} took ${System.currentTimeMillis() - futureTask.createTimestamp}ms",
            )
        } catch (t: Throwable) {
            logger.log(Level.SEVERE, "Error while completeExceptionally Future $taskId", t)
        }
    }

    /**
     * {@inheritDoc}
     */
    override fun getExpired(): List<FutureService.TaskId> {
        val now = LocalDateTime.now()
        return resultMap.filter { it.value.expireTime.isAfter(now) }.map { it.key }
    }

    /**
     * Protected helper function to get timeout for a specific class.
     */
    protected open fun getTimeout(clazz: KClass<*>) =
        timeoutClassMap.filterKeys { clazz == it }.map { it.value }.firstOrNull()
            ?: timeoutClassMap.filterKeys { clazz.isSubclassOf(it) }.map { it.value }.firstOrNull()
            ?: defaultTimeout
}
