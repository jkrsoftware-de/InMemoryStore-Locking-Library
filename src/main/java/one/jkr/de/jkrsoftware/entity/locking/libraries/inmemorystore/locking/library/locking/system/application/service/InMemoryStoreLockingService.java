package one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.application.service;

import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.service.LockingService;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.out.InMemoryStoreLockPersistor;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.out.InMemoryStoreLockRequestPersistor;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.application.ports.in.get.current.lock.CurrentLockUseCaseForInMemoryStore;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.application.ports.in.lock.LockUseCaseForInMemoryStore;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.application.ports.in.unlock.UnlockUseCaseForInMemoryStore;

public class InMemoryStoreLockingService
        extends LockingService
        implements CurrentLockUseCaseForInMemoryStore, LockUseCaseForInMemoryStore, UnlockUseCaseForInMemoryStore {

    public InMemoryStoreLockingService(long pollingRateOnLockRequestQueue) {
        super(
                new InMemoryStoreLockPersistor(),
                new InMemoryStoreLockRequestPersistor(pollingRateOnLockRequestQueue)
        );
    }

}
