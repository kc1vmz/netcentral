package netcentral.transceiver.aprsis.config;

/*
    Net Central
    Copyright (c) 2025, 2026 John Rokicki KC1VMZ

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
    
    http://www.kc1vmz.com
*/

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
