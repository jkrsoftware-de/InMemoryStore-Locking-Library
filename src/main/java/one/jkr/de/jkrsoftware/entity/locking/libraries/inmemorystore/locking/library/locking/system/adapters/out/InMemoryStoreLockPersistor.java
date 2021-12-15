package one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.out;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.LockIdentifier;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.entity.lock.EntityLock;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.entity.lock.EntityLockId;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.request.LockRequest;
import one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.application.ports.out.LockPortForInMemoryStore;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
public class InMemoryStoreLockPersistor implements LockPortForInMemoryStore {

    private static final String LOG_PREFIX = "[In-Memory Lock Persistor]: ";

    private final Map<LockIdentifier, EntityLock> inMemoryEntityLockMap = new HashMap<>();

    @NonNull
    private final Clock clock;

    public InMemoryStoreLockPersistor() {
        this.clock = Clock.systemUTC();
    }

    @Override
    public Optional<EntityLock> lock(@NonNull LockRequest lockRequest) {
        log.debug(LOG_PREFIX + "Request Lock via Request \"{}\".", lockRequest);

        LockIdentifier lockIdentifier = lockRequest.getLockIdentifier();

        Optional<EntityLock> currentLock = getCurrentLock(lockIdentifier);
        if (currentLock.isPresent()) {
            log.debug(LOG_PREFIX + "There is already an existing Lock, couldn't lock twice. Corresponding Lock-Request: \"{}\".",
                    lockRequest);
            return Optional.empty();
        }

        EntityLock entityLock = new EntityLock(
                new EntityLockId(UUID.randomUUID()),
                lockIdentifier,
                lockRequest,
                OffsetDateTime.now(clock)
        );
        inMemoryEntityLockMap.put(lockIdentifier, entityLock);
        return Optional.of(entityLock);
    }

    @Override
    public Optional<EntityLock> getCurrentLock(@NonNull LockIdentifier lockIdentifier) {

        log.debug(LOG_PREFIX + "Get Current Lock for \"{}\".", lockIdentifier);

        if (inMemoryEntityLockMap.containsKey(lockIdentifier)) {
            return Optional.of(inMemoryEntityLockMap.get(lockIdentifier));
        }

        return Optional.empty();
    }

    @Override
    public void unlock(@NonNull LockIdentifier lockIdentifier) {
        log.debug(LOG_PREFIX + "Unlock \"{}\".", lockIdentifier);
        inMemoryEntityLockMap.remove(lockIdentifier);
    }
}
