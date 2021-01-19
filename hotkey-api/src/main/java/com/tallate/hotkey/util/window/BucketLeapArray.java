/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.tallate.hotkey.util.window;

import java.util.concurrent.atomic.LongAdder;

/**
 * The fundamental data structure for metric statistics in a time span.
 */
public class BucketLeapArray extends LeapArray<LongAdder> {

    public BucketLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }

    @Override
    public LongAdder newEmptyBucket(long time) {
        return new LongAdder();
    }

    @Override
    protected WindowWrap<LongAdder> resetWindowTo(WindowWrap<LongAdder> w, long startTime) {
        // Update the start time and reset value.
        w.resetTo(startTime);
        w.value().reset();
        return w;
    }
}
