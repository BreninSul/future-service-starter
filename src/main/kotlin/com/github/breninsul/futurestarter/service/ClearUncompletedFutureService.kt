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
import java.util.concurrent.TimeoutException
import java.util.logging.Level

/**
 * Class that provides a mechanism to clear uncompleted futures.
 * It does this by scheduling a task to be run at specified delay intervals
 *
 * @param   futureService the FutureService instance used for future operations
 * @param   schedulerDelay the delay at which the scheduler will run
 */
open class ClearUncompletedFutureService(
    protected val futureService: FutureService,
    protected val schedulerDelay: Duration
) : FutureService by object : ScheduledFutureServiceWrapper(futureService, schedulerDelay, "ClearUncompleted"){

    /**
     * Function to run the scheduled task.
     * It gets the expired futures and tries to complete them exceptionally.
     * If an error occurs, it logs the error
     */
    override fun runScheduled() {
        val outdated = getExpired()
        outdated.forEach {
            try {
                completeExceptionally(
                    it.id,
                    it.resultClass,
                    TimeoutException("Maximum timeout reached for this Future")
                )
                logger.log(loggingLevel, "ClearUncompletedFutureService completed on timeout $it")
            } catch (t: Throwable) {
                logger.log(Level.SEVERE, "Error on ClearUncompletedFutureService complete $it", t)
            }
        }
    }
}