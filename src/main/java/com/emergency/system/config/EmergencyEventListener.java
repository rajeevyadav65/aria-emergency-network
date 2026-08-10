package com.emergency.system.config;

import com.emergency.system.model.Emergency;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Application event system for decoupled emergency lifecycle processing.
 *
 * Publishes: EmergencyCreatedEvent, EmergencyResolvedEvent, HighRiskEmergencyEvent
 *
 * This pattern lets you add new side-effects (e.g. SMS, email, analytics)
 * without modifying EmergencyService — just add a new @EventListener.
 */
@Component
@Slf4j
public class EmergencyEventListener {

    // ── Event definitions ─────────────────────────────────────────────────────

    public static class EmergencyCreatedEvent extends ApplicationEvent {
        private final Emergency emergency;
        public EmergencyCreatedEvent(Object source, Emergency emergency) {
            super(source);
            this.emergency = emergency;
        }
        public Emergency getEmergency() { return emergency; }
    }

    public static class EmergencyResolvedEvent extends ApplicationEvent {
        private final Emergency emergency;
        public EmergencyResolvedEvent(Object source, Emergency emergency) {
            super(source);
            this.emergency = emergency;
        }
        public Emergency getEmergency() { return emergency; }
    }

    public static class HighRiskEmergencyEvent extends ApplicationEvent {
        private final Emergency emergency;
        private final int alertedCount;
        public HighRiskEmergencyEvent(Object source, Emergency emergency, int alertedCount) {
            super(source);
            this.emergency = emergency;
            this.alertedCount = alertedCount;
        }
        public Emergency getEmergency() { return emergency; }
        public int getAlertedCount() { return alertedCount; }
    }

    // ── Listeners ─────────────────────────────────────────────────────────────

    /**
     * Logs every new emergency. Add SMS/email integrations here.
     */
    @Async
    @EventListener
    public void onEmergencyCreated(EmergencyCreatedEvent event) {
        Emergency e = event.getEmergency();
        log.info("[EVENT] Emergency created: #{} | {} | {} | device={}",
                e.getId(), e.getRiskLevel(), e.getStatus(), e.getReportedByDeviceId());
        // Future: send SMS via Twilio, email via SendGrid
    }

    /**
     * Logs when an emergency is resolved. Add follow-up logic here.
     */
    @Async
    @EventListener
    public void onEmergencyResolved(EmergencyResolvedEvent event) {
        Emergency e = event.getEmergency();
        log.info("[EVENT] Emergency resolved: #{} at {}", e.getId(), LocalDateTime.now());
        // Future: send resolution confirmation, update analytics
    }

    /**
     * Extra handling for high-risk situations.
     */
    @Async
    @EventListener
    public void onHighRiskEmergency(HighRiskEmergencyEvent event) {
        Emergency e = event.getEmergency();
        log.warn("[EVENT] HIGH RISK #{} — {} nearby users alerted | location ({},{})",
                e.getId(), event.getAlertedCount(), e.getLatitude(), e.getLongitude());
        // Future: escalate to emergency services API, trigger sirens, etc.
    }

    // ── Publisher helper (inject into EmergencyService) ───────────────────────

    @Component
    public static class EmergencyEventPublisher {
        private final ApplicationEventPublisher publisher;
        public EmergencyEventPublisher(ApplicationEventPublisher publisher) {
            this.publisher = publisher;
        }
        public void publishCreated(Object source, Emergency e) {
            publisher.publishEvent(new EmergencyCreatedEvent(source, e));
        }
        public void publishResolved(Object source, Emergency e) {
            publisher.publishEvent(new EmergencyResolvedEvent(source, e));
        }
        public void publishHighRisk(Object source, Emergency e, int count) {
            publisher.publishEvent(new HighRiskEmergencyEvent(source, e, count));
        }
    }
}
