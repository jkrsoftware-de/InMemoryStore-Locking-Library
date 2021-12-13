package one.jkr.de.jkrsoftware.entity.locking.libraries.inmemorystore.locking.library.locking.system.adapters.in;

import lombok.SneakyThrows;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.port.in.get.current.entity.lock.IsAlreadyLockedCommand;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.port.in.lock.LockCommand;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.port.in.unlock.ForceUnlockCommand;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.application.port.in.unlock.UnlockCommand;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.LockGroup;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.LockIdentifier;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.LockSubject;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.entity.lock.EntityLock;
import one.jkr.de.jkrsoftware.entity.locking.libraries.generic.locking.library.locking.system.domain.lock.timeout.strategy.LockTimeoutStrategy;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

class InMemoryStoreLockingSystemAdapterIntegrationTest {

    private final Clock clock = Clock.systemUTC();

    private final InMemoryStoreLockingSystemAdapter uut = new InMemoryStoreLockingSystemAdapter(clock, 500L);

    @Test
    void getCurrentEntityLock() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> firstLock = uut.lock(lockCommand);
        Optional<EntityLock> secondLock = uut.lock(lockCommand);

        // then.
        Assertions.assertThat(firstLock).isNotNull();
        Assertions.assertThat(firstLock).isPresent();
        Assertions.assertThat(firstLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(secondLock).isNotNull();
        Assertions.assertThat(secondLock).isEmpty();
    }

    @Test
    void isAlreadyLocked_whenThereIsNoLockBefore() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-firstLock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );

        // when.
        boolean alreadyLocked = uut.isAlreadyLocked(new IsAlreadyLockedCommand(lockIdentifier));

        // then.
        Assertions.assertThat(alreadyLocked).isFalse();
    }

    @Test
    void isAlreadyLocked_whenThereAlreadyLocked() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("someLockGroup"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        uut.waitForLock(lockCommand, LockTimeoutStrategy.timebasedLockTimeOutStrategy(Duration.ofSeconds(5)));
        boolean alreadyLocked = uut.isAlreadyLocked(new IsAlreadyLockedCommand(lockIdentifier));

        // then.
        Assertions.assertThat(alreadyLocked).isTrue();
    }

    @Test
    void waitForLock_whenThereIsNoLockBefore() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-firstLock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> entityLock = uut.waitForLock(lockCommand,
                LockTimeoutStrategy.timebasedLockTimeOutStrategy(Duration.ofSeconds(5)));

        // then.
        Assertions.assertThat(entityLock).isNotNull();
        Assertions.assertThat(entityLock).isPresent();
        Assertions.assertThat(entityLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
    }

    @Test
    void waitForLock_whenThereIsAlreadyALock_withTimebasedLockTimeoutStrategy_withoutUnlock() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-firstLock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> firstLock = uut.lock(lockCommand);
        Optional<EntityLock> secondLock = uut.waitForLock(lockCommand,
                LockTimeoutStrategy.timebasedLockTimeOutStrategy(Duration.of(5, ChronoUnit.SECONDS)));

        // then.
        Assertions.assertThat(firstLock).isNotNull();
        Assertions.assertThat(firstLock).isPresent();
        Assertions.assertThat(secondLock).isNotNull();
        Assertions.assertThat(secondLock).isEmpty();
    }

    @Test
    void waitForLock_whenThereIsAlreadyALock_withTimebasedLockTimeoutStrategy_withUnlock() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-firstLock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> firstLock = uut.lock(lockCommand);
        unlockAfterDelay(firstLock.get(), Duration.ofSeconds(2));
        Optional<EntityLock> secondLock = uut.waitForLock(lockCommand, LockTimeoutStrategy.timebasedLockTimeOutStrategy(
                Duration.of(10, ChronoUnit.SECONDS)));

        // then.
        Assertions.assertThat(firstLock).isNotNull();
        Assertions.assertThat(firstLock).isPresent();
        Assertions.assertThat(firstLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(secondLock).isNotNull();
        Assertions.assertThat(secondLock).isPresent();
        Assertions.assertThat(secondLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
    }

    @Test
    void waitForLock_whenThereIsAlreadyALock_withoutLockTimeoutStrategy() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-firstLock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> firstLock = uut.lock(lockCommand);
        unlockAfterDelay(firstLock.get(), Duration.ofSeconds(2));
        Optional<EntityLock> secondLock = uut.waitForLock(lockCommand, LockTimeoutStrategy.withoutLockTimeoutStrategy());

        // then.
        Assertions.assertThat(firstLock).isNotNull();
        Assertions.assertThat(firstLock).isPresent();
        Assertions.assertThat(firstLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(secondLock).isNotNull();
        Assertions.assertThat(secondLock).isPresent();
        Assertions.assertThat(secondLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
    }

    @Test
    void lock_noLockBefore_should_success() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> lock = uut.lock(lockCommand);

        // then.
        Assertions.assertThat(lock).isNotNull();
        Assertions.assertThat(lock).isPresent();
        Assertions.assertThat(lock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
    }

    @Test
    void lock_whenAlreadyLocked_shouldNotLocked() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> firstLock = uut.lock(lockCommand);
        Optional<EntityLock> secondLock = uut.lock(lockCommand);

        // then.
        Assertions.assertThat(firstLock).isNotNull();
        Assertions.assertThat(firstLock).isPresent();
        Assertions.assertThat(firstLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(secondLock).isNotNull();
        Assertions.assertThat(secondLock).isEmpty();
    }

    @Test
    void unlock_unsuccessful_unlockTwiceAfterSuccessfulUnlock() {

        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> entityLock = uut.lock(lockCommand);

        boolean becameUnlocked = uut.unlock(new UnlockCommand(entityLock.get()));
        boolean becameUnlockedTwice = uut.unlock(new UnlockCommand(entityLock.get()));

        // then.
        Assertions.assertThat(entityLock).isNotNull();
        Assertions.assertThat(entityLock).isPresent();
        Assertions.assertThat(entityLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(becameUnlocked).isTrue();
        Assertions.assertThat(becameUnlockedTwice).isFalse();
    }

    @Test
    void unlock_successful_whenLocked() {

        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> entityLock = uut.lock(lockCommand);
        boolean becameUnlocked = uut.unlock(new UnlockCommand(entityLock.get()));

        // then.
        Assertions.assertThat(entityLock).isNotNull();
        Assertions.assertThat(entityLock).isPresent();
        Assertions.assertThat(entityLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(becameUnlocked).isTrue();
    }

    @Test
    void unlock_force_noLockBefore() {

        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        ForceUnlockCommand forceUnlockCommand = new ForceUnlockCommand(lockIdentifier);

        // when.
        boolean becameUnlocked = uut.unlock(forceUnlockCommand);

        // then.
        Assertions.assertThat(becameUnlocked).isTrue();
    }

    @Test
    void unlock_force_whenLocked() {
        // given.
        LockIdentifier lockIdentifier = new LockIdentifier(
                new LockGroup("some-lock-group"),
                new LockSubject("id-of-some-lockable-entity-for-testing")
        );
        LockCommand lockCommand = new LockCommand(lockIdentifier);
        ForceUnlockCommand forceUnlockCommand = new ForceUnlockCommand(lockIdentifier);

        // when.
        Optional<EntityLock> entityLock = uut.lock(lockCommand);
        boolean becameUnlocked = uut.unlock(forceUnlockCommand);

        // then.
        Assertions.assertThat(entityLock).isNotNull();
        Assertions.assertThat(entityLock).isPresent();
        Assertions.assertThat(entityLock.get().getLockIdentifier()).isEqualTo(lockIdentifier);
        Assertions.assertThat(becameUnlocked).isTrue();
    }

    @SneakyThrows
    private void unlockAfterDelay(EntityLock lockToUnlock, Duration duration) {
        Runnable unlockSequence = () -> {
            try {
                Thread.sleep(duration.toMillis());
                uut.unlock(new UnlockCommand(lockToUnlock));
            } catch (InterruptedException e) {
                // just nothing. :)
            }
        };
        CompletableFuture.runAsync(unlockSequence);
    }

}