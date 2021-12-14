package kr.pe.burt.ohos.lib.ohoschannel;

import ohos.eventhandler.InnerEvent;
import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

/**
 * Created by burt on 15. 9. 23..
 */
public class Timer {
    public static final HiLogLabel HI_LOG_LABEL = new HiLogLabel(0, 0, "Timer");
    private static final int START_TIMER = 0;
    private static final int STOP_TIMER = 1;
    private OhosChannel ohosChannel;
    private int interval = 0;
    private OnTimer onTimer;
    volatile boolean loop = false;

    /**
     * Timer.
     *
     * @param interval interval
     * @param onTimer onTimer
     *
     */
    public Timer(int interval, OnTimer onTimer) {
        this.interval = (interval < 0) ? (interval * -1) : (interval);
        this.onTimer = onTimer;
        ohosChannel = new OhosChannel(new OhosChannel.UiCallback() {

            @Override
            public boolean handleUiMessage(InnerEvent msg) {
                Timer.this.onTimer.onTime(Timer.this);
                return false;
            }
        }, new OhosChannel.WorkerCallback() {

            Thread jobThread = null;

            @Override
            public boolean handleWorkerMessage(InnerEvent msg) {
                if (msg.eventId == START_TIMER) {
                    loop = true;
                }
                if (msg.eventId == STOP_TIMER) {
                    loop = false;
                    jobThread = null;
                }
                if (msg.eventId == START_TIMER && jobThread == null) {
                    jobThread = jobThreadFunction();
                }
                if (jobThread != null && msg.eventId == START_TIMER) {
                    loop = true;
                    jobThread.start();
                }
                if (msg.eventId == STOP_TIMER) {
                    loop = false;
                    jobThread = null;
                }
                return false;
            }
        });
    }

    private Thread jobThreadFunction() {
        return new Thread(() -> {
            while (loop) {
                try {
                    Thread.sleep(Timer.this.interval);
                    InnerEvent msg1 = InnerEvent.get();
                    ohosChannel.toUi().sendEvent(msg1);
                } catch (InterruptedException e) {
                    HiLog.debug(HI_LOG_LABEL, "InterruptedException in handleWorkerMessage " + e);
                    Thread.currentThread().interrupt();
                }
            }
        });
    }

    public void start() {
        ohosChannel.toWorker().sendEvent(START_TIMER);
    }

    public void stop() {
        ohosChannel.toWorker().sendEvent(STOP_TIMER);
    }

    public void resetInterval(int interval) {
        this.interval = interval;
    }

    public int getInterval() {
        return interval;
    }

    public boolean isAlive() {
        return loop;
    }

    /**
     * OnTimer interface.
     */
    public interface OnTimer {

        void onTime(Timer timer);
    }
}
