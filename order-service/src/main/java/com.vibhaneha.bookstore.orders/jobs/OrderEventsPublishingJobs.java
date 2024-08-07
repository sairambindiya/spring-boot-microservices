package com.vibhaneha.bookstore.orders.jobs;

import com.vibhaneha.bookstore.orders.domain.OrderEventService;
import java.time.Instant;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OrderEventsPublishingJobs {

    private static final Logger log = LoggerFactory.getLogger(OrderEventsPublishingJobs.class);

    private final OrderEventService orderEventService;

    public OrderEventsPublishingJobs(OrderEventService orderEventService) {
        this.orderEventService = orderEventService;
    }

    @Scheduled(cron = "${orders.publish-order-events-job-cron}")
    @SchedulerLock(name = "publishOrderEvents")
    public void publishOrderEvents() {
        LockAssert.assertLocked();
        log.info("Publish Order Events at {}" + Instant.now());
        orderEventService.publishOrderEvents();
    }
}
