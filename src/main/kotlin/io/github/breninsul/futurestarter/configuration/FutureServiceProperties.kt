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

package io.github.breninsul.futurestarter.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import java.time.Duration

/**
 * This class holds configuration properties related to the Future Service. These properties will be mapped from
 * the Spring application properties using a prefix of "future.service".
 *
 * @property enabled Indicates if the Future Service is enabled or not. This can be used to turn on/off processing
 * within the service.
 * @property clearDelay Specifies the delay time for clearing out future sourced data.
 * @property defaultTimeout Default time duration before a given future operation times out.
 * @property loggingLevel The log level setting for the Future Service.
 * @property defaultClassTimeout Default time durations for specific classes before the Future Service operations
 * times out on them. The keys of this map represent the class names while the values are the respective timeout durations.
 */
@ConfigurationProperties(prefix = "future.service")
data class FutureServiceProperties(
    var enabled: Boolean = true,
    var clearDelay: Duration = Duration.ofSeconds(10),
    var defaultTimeout: Duration = Duration.ofMinutes(2),
    var loggingLevel: String = "INFO",
    var defaultClassTimeout: Map<String, Duration> = mapOf(),
)
