package one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.in;

import lombok.NonNull;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.adapter.in.LockingSystemAdapter;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.service.LockingService;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.out.InMemoryStoreLockPersistor;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.out.InMemoryStoreLockRequestPersistor;

import java.time.Clock;

public class InMemoryStoreLockingSystemAdapter extends LockingSystemAdapter {

    public InMemoryStoreLockingSystemAdapter(@NonNull Clock clock, long pollingRateOnLockRequestQueue) {
        super(
                new LockingService(
                        new InMemoryStoreLockPersistor(clock),
                        new InMemoryStoreLockRequestPersistor(pollingRateOnLockRequestQueue, clock)
                )
        );
    }

}
