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

import ohos.aafwk.ability.fraction.FractionAbility;
import ohos.aafwk.content.Intent;
import ohos.agp.components.Text;
import ohos.agp.utils.Color;
import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;
import ohos.multimodalinput.event.TouchEvent;
import kr.pe.burt.ohos.lib.ohoschannel.OhosChannel;
import kr.pe.burt.ohos.lib.ohoschannel.Timer;

/**
 * MainAbility Class.
 */
public class MainAbility extends FractionAbility {
    public static final HiLogLabel HI_LOG_LABEL = new HiLogLabel(0, 0, "FractionAbility");
    private static final int PING = 0;
    private static final int PONG = 1;
    Text textView;
    Text textDisplay;
    OhosChannel ohosChannel;
    Timer timer;

    @Override
    public void onStart(Intent intent) {
        super.onStart(intent);
        setUIContent(ResourceTable.Layout_ability_main);
        textView = (Text) findComponentById(ResourceTable.Id_textView);
        textDisplay = (Text) findComponentById(ResourceTable.Id_textdisplay);
        timer = new Timer(1000, new Timer.OnTimer() {
            int count = 0;
            @Override
            public void onTime(Timer timer) {
                count++;
                textView.setText("count : " + count);
                if (count == 10) {
                    timer.resetInterval(2000);
                }
            }
        });
        timer.start();
        ohosChannel = new OhosChannel(new OhosChannel.UiCallback() {
            @Override
            public boolean handleUiMessage(InnerEvent msg) {
                if (msg.eventId == PING) {
                    HiLog.debug(HI_LOG_LABEL, "PING");
                    ohosChannel.toWorker().sendEvent(PONG, 1000);
                }
                return false;
            }
        }, new OhosChannel.WorkerCallback() {
            @Override
            public boolean handleWorkerMessage(InnerEvent msg) {
                if (msg.eventId == PONG) {
                    HiLog.debug(HI_LOG_LABEL, "PONG");
                    ohosChannel.toUi().sendEvent(PING, 1000);
                }
                return false;
            }
        });
    }

    @Override
    protected void onActive() {
        super.onActive();
        ohosChannel.open();
        ohosChannel.toUi().sendEvent(PING);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        ohosChannel.close();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onTouchEvent(TouchEvent event) {
        HiLog.debug(HI_LOG_LABEL, "onTouchEvent " + event.getAction());
        if (event.getAction() == TouchEvent.PRIMARY_POINT_UP) {
            if (timer.isAlive()) {
                textDisplay.setText("Touch event detected, counter stopped.");
                textDisplay.setTextColor(Color.RED);
                HiLog.debug(HI_LOG_LABEL, "onTouchEvent stop ");
                timer.stop();
            } else {
                textDisplay.setText("Touch event detected, counter resumed.");
                textDisplay.setTextColor(Color.GREEN);
                HiLog.debug(HI_LOG_LABEL, "onTouchEvent start ");
                timer.start();
            }
        }
        return super.onTouchEvent(event);
    }

}
