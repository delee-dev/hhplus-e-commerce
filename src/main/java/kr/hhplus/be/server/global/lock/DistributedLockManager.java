package kr.hhplus.be.server.global.lock;

import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;


@Component
@RequiredArgsConstructor
public class DistributedLockManager {
    @Value("${lock.wait-time:5000}")
    private Long waitTime;
    @Value("${lock.lease-time:10000}")
    private Long leaseTime;
    @Value("${lock.prefix:lock:}")
    private String lockPrefix;

    private final TransactionTemplate transactionTemplate;
    private final RedissonClient redissonClient;

    public<T> T withLock(String key, Supplier<T> supplier) {
        String lockKey = lockPrefix.concat(key);
        RLock lock = redissonClient.getLock(lockKey);
        return withLock(lock, supplier);
    }

    public<T> T withLock(List<String> keys, Supplier<T> supplier) {
        RLock[] locks = keys.stream()
                .sorted()
                .map(key -> lockPrefix.concat(key))
                .map(redissonClient::getLock)
                .toArray(RLock[]::new);
        RLock multiLock = redissonClient.getMultiLock(locks);
        return withLock(multiLock, supplier);
    }

    private<T> T withLock(RLock lock, Supplier<T> supplier) {
        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);
            if (!isLocked) {
                throw new IllegalStateException("락 획득에 실패했습니다.");
            }

            return transactionTemplate.execute(status -> {
               return supplier.get();
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("락 획득 중 인터럽트 발생", e);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }
}
