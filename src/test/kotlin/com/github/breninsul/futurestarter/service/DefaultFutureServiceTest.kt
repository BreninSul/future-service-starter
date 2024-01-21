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

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.Duration
import java.util.concurrent.ExecutionException
import java.util.concurrent.TimeoutException
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.concurrent.thread

@ExtendWith(SpringExtension::class)
class DefaultFutureServiceTest {

    @Test
    fun `test async task completion`() {
        val futureService = DefaultFutureService(mapOf(), Duration.ofMinutes(1))
        val taskId="test async task completion"
        val okResult = "OK"
        val jobThread=thread(start = true) {
            val time=System.currentTimeMillis()
            val future = futureService.registerTask<String>(taskId)
            val result=future.get()
            logger.log(Level.INFO,"$taskId took ${System.currentTimeMillis()-time} ms")
            assertEquals(okResult, result)
        }
        thread(start = true) {
            Thread.sleep(100)
            futureService.complete<String>(taskId,okResult)
        }
        jobThread.join()
    }
    @Test
    fun `test async task timeout`() {
        val futureService = DefaultFutureService(mapOf(), Duration.ofMinutes(1))
        val taskId="test async task timeout"
        val jobThread=thread(start = true) {
            val time=System.currentTimeMillis()
            val future = futureService.registerTask<String>(taskId,Duration.ofMillis(100))
            try {
                future.thenApply { "Mod$it" } .get()
            } catch (t:ExecutionException) {
                val original=t.cause
                logger.log(Level.INFO, "$taskId took ${System.currentTimeMillis() - time} ms")
                assertInstanceOf(TimeoutException::class.java,original)
            }
        }
        jobThread.join()
    }


    @Test
    fun `test async task completion error`() {
        val futureService = DefaultFutureService(mapOf(), Duration.ofMinutes(1))
        val taskId="test async task completion error"
        val exception = IllegalStateException("Test")
        val jobThread=thread(start = true) {
            val time=System.currentTimeMillis()
            val future = futureService.registerTask<String>(taskId)
            try {
               future.thenApply { "Mod$it" } .get()
            } catch (t:ExecutionException) {
                val original=t.cause
                logger.log(Level.INFO, "$taskId took ${System.currentTimeMillis() - time} ms")
                assertEquals(exception,original)
            }
        }
        thread(start = true) {
            Thread.sleep(100)
            futureService.completeExceptionally<String>(taskId,exception)
        }
        jobThread.join()
    }
    companion object {
        internal val logger = Logger.getLogger(this::class.java.name)
    }
}