package one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.out;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.port.out.LockRequestPort;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.LockIdentifier;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.request.LockRequest;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.request.LockRequestId;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.timeout.strategy.LockTimeoutStrategy;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.timeout.strategy.TimebasedLockTimeoutStrategy;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
@Slf4j
public class InMemoryStoreLockRequestPersistor implements LockRequestPort {

    private static final String LOG_PREFIX = "[In-Memory Lock-Request Persistor]: ";

    private final Map<LockIdentifier, LinkedHashSet<LockRequest>> lockRequestQueueMap = new LinkedHashMap<>();

    private final long pollingRateOnLockRequestQueue;

    @NonNull
    private final Clock clock;

    @Override
    public LockRequest submitLockRequest(@NonNull LockIdentifier lockIdentifier) {
        log.debug(LOG_PREFIX + "Submit Lock-Request for Lock-ID \"{}\".", lockIdentifier);

        LockRequest lockRequest = new LockRequest(new LockRequestId(UUID.randomUUID()), lockIdentifier, OffsetDateTime.now(clock));
        putLockRequestInQueue(lockRequest);
        return lockRequest;
    }

    @Override
    public void waitForFreeSlot(@NonNull LockRequest lockRequest, @NonNull LockTimeoutStrategy lockTimeoutStrategy) {
        log.debug(LOG_PREFIX + "Wait for next Time-Slot to fulfil the Lock-Request \"{}\".", lockRequest);

        AtomicBoolean isNextRequestInQueue = new AtomicBoolean(false);
        ScheduledFuture<?> recheckLockRequestQueueSchedule = Executors.newSingleThreadScheduledExecutor()
                .scheduleWithFixedDelay(
                        () -> isNextRequestInQueue.set(isNextRequestInQueue(lockRequest)),
                        0, pollingRateOnLockRequestQueue, TimeUnit.MILLISECONDS
                );

        while (!isNextRequestInQueue.get()) {
            if (lockTimeoutStrategy instanceof TimebasedLockTimeoutStrategy) {
                boolean limitReached = ((TimebasedLockTimeoutStrategy) lockTimeoutStrategy)
                        .isLimitReached(lockRequest.getRequestedAt(), OffsetDateTime.now(clock));
                if (limitReached) {
                    log.warn("The timebased Timeout-Limit for Lock Request \"{}\" is reached. Used Strategy: \"{}\".",
                            lockRequest, lockTimeoutStrategy);
                    break;
                }
            }
        }
        recheckLockRequestQueueSchedule.cancel(false);
    }

    @Override
    public void removeLockRequest(@NonNull LockRequest lockRequest) {
        log.debug(LOG_PREFIX + "Remove Lock Request \"{}\".", lockRequest);
        getQueue(lockRequest.getLockIdentifier()).remove(lockRequest);
    }

    private boolean isNextRequestInQueue(LockRequest lockRequest) {
        Optional<LockRequest> firstRequestInQueue = getQueue(lockRequest.getLockIdentifier()).stream().findFirst();

        if (firstRequestInQueue.isPresent()) {
            return lockRequest.getLockRequestId().equals(firstRequestInQueue.get().getLockRequestId());
        } else {
            log.error(
                    LOG_PREFIX + "There is no Request present in the In-Memory Queue. Couldn't decide if \"{}\" is the next Request or not.",
                    lockRequest);
            throw new RuntimeException("There is no Request present in the In-Memory Queue. Couldn't decide if \"" +
                    lockRequest + "\" is the next Request or not.");
        }
    }

    private LinkedHashSet<LockRequest> getQueue(LockIdentifier lockIdentifier) {
        if (lockRequestQueueMap.containsKey(lockIdentifier)) {
            return lockRequestQueueMap.get(lockIdentifier);
        } else {
            LinkedHashSet<LockRequest> lockRequestQueue = new LinkedHashSet<>();
            lockRequestQueueMap.put(lockIdentifier, lockRequestQueue);

            return lockRequestQueue;
        }
    }

    private void putLockRequestInQueue(LockRequest lockRequest) {
        getQueue(lockRequest.getLockIdentifier()).add(lockRequest);
    }
}
