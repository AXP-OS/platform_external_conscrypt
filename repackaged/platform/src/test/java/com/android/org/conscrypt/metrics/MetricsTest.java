/* GENERATED SOURCE. DO NOT MODIFY. */
/*
 * Copyright (C) 2020 The Android Open Source Project
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

package com.android.org.conscrypt.metrics;

import static org.junit.Assert.assertEquals;

import android.util.StatsEvent;
import com.android.org.conscrypt.TestUtils;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @hide This class is not part of the Android public SDK API
 */
@RunWith(JUnit4.class)
public class MetricsTest {
    public static final int TLS_HANDSHAKE_REPORTED = 317;

    // Tests that ReflexiveEvent produces the same event as framework's.
    @Test
    @Ignore // Ignore on CTS 12 only: b/259508875
    public void test_reflexiveEvent() throws Exception {
        TestUtils.assumeStatsLogAvailable();

        StatsEvent frameworkStatsEvent = StatsEvent.newBuilder()
                                                 .setAtomId(TLS_HANDSHAKE_REPORTED)
                                                 .writeBoolean(false)
                                                 .writeInt(1) // protocol
                                                 .writeInt(2) // cipher suite
                                                 .writeInt(100) // duration
                                                 .usePooledBuffer()
                                                 .build();

        ReflexiveStatsEvent reflexiveStatsEvent =
                ReflexiveStatsEvent.buildEvent(TLS_HANDSHAKE_REPORTED, false, 1, 2, 100);
        StatsEvent constructedEvent = (StatsEvent) reflexiveStatsEvent.getStatsEvent();

        // TODO(nikitai): Figure out how to use hidden (@hide) getters from StatsEvent
        // to eliminate the use of reflection
        int fid = (Integer) frameworkStatsEvent.getClass()
                          .getMethod("getAtomId")
                          .invoke(frameworkStatsEvent);
        int cid = (Integer) constructedEvent.getClass()
                          .getMethod("getAtomId")
                          .invoke(constructedEvent);
        assertEquals(fid, cid);

        int fnb = (Integer) frameworkStatsEvent.getClass()
                          .getMethod("getNumBytes")
                          .invoke(frameworkStatsEvent);
        int cnb = (Integer) constructedEvent.getClass()
                          .getMethod("getNumBytes")
                          .invoke(constructedEvent);
        assertEquals(fnb, cnb);

        byte[] fbytes = (byte[]) frameworkStatsEvent.getClass()
                                .getMethod("getBytes")
                                .invoke(frameworkStatsEvent);
        byte[] cbytes =
                (byte[]) constructedEvent.getClass().getMethod("getBytes").invoke(constructedEvent);
        for (int i = 0; i < fnb; i++) {
            // skip encoded timestamp (bytes 1-8)
            if (i < 1 || i > 8) {
                assertEquals(fbytes[i], cbytes[i]);
            }
        }
    }
}
