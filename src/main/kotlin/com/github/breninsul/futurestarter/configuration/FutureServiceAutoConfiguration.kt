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

package com.github.breninsul.futurestarter.configuration

import com.github.breninsul.futurestarter.service.ClearUncompletedFutureService
import com.github.breninsul.futurestarter.service.DefaultFutureService
import com.github.breninsul.futurestarter.service.FutureService
import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.logging.Level
import kotlin.reflect.KClass

/**
 * Auto configuration for Future Service.
 * This class provides the framework for future service configuration
 * based on the properties defined.
 */
@AutoConfiguration
@ConditionalOnClass(CompletableFuture::class)
@ConditionalOnProperty(
    prefix = "future.service",
    name = ["enabled"],
    matchIfMissing = true,
    havingValue = "true",
)
@EnableConfigurationProperties(FutureServiceProperties::class)
class FutureServiceAutoConfiguration {
    /**
     * Defines the configuration for future service.
     *
     * @param futureServiceProperties - Properties needed for the future services.
     * @return a FutureService configured based on the given properties.
     */
    @Bean
    fun futureService(futureServiceProperties: FutureServiceProperties): FutureService {
        val classTimeoutMap =
            futureServiceProperties.defaultClassTimeout.mapKeys { Class.forName(it.key).kotlin } as MutableMap<KClass<*>, Duration>
        val loggingLevel = Level.parse(futureServiceProperties.loggingLevel)
        val default = DefaultFutureService(classTimeoutMap, futureServiceProperties.defaultTimeout, loggingLevel)
        return if (futureServiceProperties.clearDelay.toNanos() > 0) {
            ClearUncompletedFutureService(default, futureServiceProperties.clearDelay)
        } else {
            default
        }
    }
}
