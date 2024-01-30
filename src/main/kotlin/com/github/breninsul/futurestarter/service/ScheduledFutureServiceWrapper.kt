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

import com.github.breninsul.futurestarter.service.FutureService.Companion.logger
import java.time.Duration
import java.util.*
import java.util.logging.Level

/**
 * Abstract class that wraps a FutureService with scheduling capabilities.
 *
 * @param futureService The future service to be scheduled.
 * @param schedulerDelay The delay duration between scheduling.
 * @param name The name of the scheduled service.
 */
abstract class ScheduledFutureServiceWrapper(
    protected val futureService: FutureService,
    val schedulerDelay: Duration,
    val name: String,
) : FutureService by futureService {
    /**
     * Creates and initializes a timer with a fixed delay schedule.
     *
     * @param name The name of the timer.
     * @param task The task to be scheduled.
     * @param scheduleDelay The delay duration between scheduling.
     * @return Returns an initialized timer.
     */
    protected fun createTimer(
        name: String,
        task: TimerTask,
        scheduleDelay: Duration,
    ): Timer {
        val timer = Timer(name)
        timer.scheduleAtFixedRate(task, scheduleDelay.toMillis(), scheduleDelay.toMillis())
        return timer
    }

    /**
     * The task to be run by the scheduler.
     */
    protected open val task: TimerTask =
        object : TimerTask() {
            /**
             * The method that is executed at every scheduling event.
             */
            override fun run() {
                try {
                    runScheduled()
                } catch (t: Throwable) {
                    logger.log(Level.SEVERE, "Error while executing $name scheduled future task", t)
                }
            }
        }

    /**
     * Abstract method that contains the logic to be executed by the scheduler.
     */
    protected abstract fun runScheduled()

    /**
     * The initialized scheduler.
     */
    @SuppressWarnings("unused")
    protected open val scheduler by lazy { createTimer(name, task, schedulerDelay) }
}
