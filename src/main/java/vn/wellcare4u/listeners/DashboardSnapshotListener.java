package vn.wellcare4u.listeners;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vn.wellcare4u.events.DashboardChangedEvent;
import vn.wellcare4u.services.PatientDashboardSnapshotService;

@Component
@RequiredArgsConstructor
public class DashboardSnapshotListener {

    private final PatientDashboardSnapshotService snapshotService;

    @Async
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT
    )
    public void handle(DashboardChangedEvent event) {
        snapshotService.rebuildSnapshot(event.patientId());
    }
}