/*
 * Copyright (C) 2020-21 Application Library Engineering Group
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

package kr.pe.burt.ohos.lib.ohos.channel.app;

import ohos.aafwk.ability.delegation.AbilityDelegatorRegistry;
import org.junit.Test;
import kr.pe.burt.ohos.lib.ohoschannel.Timer;
import static org.junit.Assert.assertEquals;

public class ExampleOhosTest {

    @Test
    public void testBundleName() {
        final String actualBundleName = AbilityDelegatorRegistry.getArguments().getTestBundleName();
        assertEquals("kr.pe.burt.ohos.lib.ohos.channel.app", actualBundleName);
    }

    @Test
    public void testInterval() {
        Timer timer = new Timer(1000, new Timer.OnTimer() {
            int count = 0;
            @Override
            public void onTime(Timer timer) {
                if (count == 0) {
                    timer.resetInterval(2000);
                }
            }
        });
        assertEquals(1000,timer.getInterval());
    }

    @Test
    public void testIsalive() {
        Timer timer = new Timer(1000, new Timer.OnTimer() {
            int count = 0;
            @Override
            public void onTime(Timer timer) {
                if (count == 0) {
                    timer.resetInterval(2000);
                }
            }
        });
        assertEquals(false,timer.isAlive());
    }

}
