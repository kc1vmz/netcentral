package netcentral.transceiver.agw.config;

import io.micronaut.context.annotation.Value;
import jakarta.inject.Singleton;

@Singleton
public class QueueConfiguration {
    @Value("${netcentral.queue.sendobjecthandler.size}")
    private Integer queueSendObjectHandlerSize;
    @Value("${netcentral.queue.sendreporthandler.size}")
    private Integer queueSendReportHandlerSize;
    @Value("${netcentral.queue.sendmessagehandler.size}")
    private Integer queueSendMessageHandlerSize;

    public Integer getQueueSendObjectHandlerSize() {
        return queueSendObjectHandlerSize;
    }
    public void setQueueSendObjectHandlerSize(Integer queueSendObjectHandlerSize) {
        this.queueSendObjectHandlerSize = queueSendObjectHandlerSize;
    }
    public Integer getQueueSendReportHandlerSize() {
        return queueSendReportHandlerSize;
    }
    public void setQueueSendReportHandlerSize(Integer queueSendReportHandlerSize) {
        this.queueSendReportHandlerSize = queueSendReportHandlerSize;
    }
    public Integer getQueueSendMessageHandlerSize() {
        return queueSendMessageHandlerSize;
    }
    public void setQueueSendMessageHandlerSize(Integer queueSendMessageHandlerSize) {
        this.queueSendMessageHandlerSize = queueSendMessageHandlerSize;
    }
}
