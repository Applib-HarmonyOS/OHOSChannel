package kr.pe.burt.ohos.lib.ohoschannel;

import ohos.eventhandler.EventHandler;
import ohos.eventhandler.EventRunner;
import ohos.eventhandler.InnerEvent;

/**
 * Created by burt on 15. 9. 23..
 */
public class OhosChannel {

    /**
     * UiCallback interface for ui messages.
     */
    public interface UiCallback {
        boolean handleUiMessage(InnerEvent msg);
    }

    /**
     * WorkerCallback interface for worker messages.
     */
    public interface WorkerCallback {
        boolean handleWorkerMessage(InnerEvent msg);
    }

    EventRunner workerThread = null;
    EventHandler mainThreadHandler = null;
    EventHandler workerThreadHandler = null;
    UiCallback uiCallback = null;
    WorkerCallback workerCallback = null;
    boolean isChannelOpened = false;

    /**
     * Create channel and open channel.
     *
     * @param uiCallback        handler callback for ui messages
     * @param workerCallback    handler callback for worker messages
     *
     */
    public OhosChannel(final UiCallback uiCallback, final WorkerCallback workerCallback) {
        this.uiCallback = uiCallback;
        this.workerCallback = workerCallback;
        open();
    }

    /**
     * To send message to ui thread, You should get mainThreadHandler by using toUI() method.
     *
     * @return main thread handler
     *
     */
    public EventHandler toUi() {
        return mainThreadHandler;
    }

    /**
     * To send message to worker thread, You should get workerThreadHandler by using toWorker() method.
     *
     * @return worker thread handler
     *
     */
    public EventHandler toWorker() {
        return workerThreadHandler;
    }

    /**
     * Open channel.
     *
     * @return true if success to open channel, or return false
     *
     */
    public boolean open() {
        if (isChannelOpened) {
            return true;
        }
        if (uiCallback == null || workerCallback == null) {
            return false;
        }
        mainThreadHandler = new EventHandler(EventRunner.getMainEventRunner()) {
            @Override
            protected void processEvent(InnerEvent event) {
                uiCallback.handleUiMessage(event);
            }
        };
        workerThread = EventRunner.create("ohos-channel-worker-thread");
        workerThreadHandler = new EventHandler(workerThread) {
            @Override
            protected void processEvent(InnerEvent event) {
                workerCallback.handleWorkerMessage(event);
            }
        };
        isChannelOpened = true;
        return true;
    }

    /**
     * Close channel.
     */
    public void close() {
        if (isChannelOpened) {
            return;
        }
        mainThreadHandler.removeAllEvent();
        workerThreadHandler.removeAllEvent();
        workerThread.stop();
        workerThread = null;
        workerThreadHandler = null;
        mainThreadHandler = null;
        isChannelOpened = false;
    }
}
