# 낙관적 락 재시도 처리

## Point
```java
public class Point extends BaseEntity {
    private Long id;
    private long balance;
    private Long userId;
    private Long version;
}
```
`Point` 충전 및 사용의 경우에는 락이 필요한 기능은 맞지만, 유저 별로 관리되기 때문에 같은 행에 접근할 가능성이 상대적으로 적습니다.
(동일한 사용자가 포인트의 충전을 동시에 진행할 가능성, 사용을 동시에 진행할 가능성, 충전 및 사용을 동시에 진행할 가능성이 떨어집니다.)

따라서, 데이터베이스에 락을 걸지 않고 동일한 효과를 얻을 수 있는 낙관적 락을 사용하는 것이 맞다는 판단을 했습니다. 

## 재시도 처리
낙관적 락을 사용할 때, 주의해야 하는 점은 낙관적 락은 대기하지 않는다는 것입니다.

비관적 락을 사용하는 경우 락을 얻지 못한 스레드는 락이 해제될 때까지 대기 후, 다른 스레드에서 락이 해제되면 락을 획득합니다.
하지만, 낙관적 락을 사용하는 경우에는 충돌 즉시 `OptimisticLockException`을 발생시켜 대기하지 않고 실패처리 됩니다.

따라서, 비관적 락에 비해 더 잦은 예외를 발생시키게 되고 이는 사용자에게 좋지 않은 경험으로 다가갈 수 있습니다.
이를 해결할 수 있는 방법으로 충돌 시 재시도 처리를 고려할 수 있습니다.

### RetryUtil
```java

public class RetryUtil {
    private static final int MAX_RETRY = 3;
    private static final long RETRY_DELAY_MS = 100;

    public static <T> T execute(Supplier<T> operation) {
        int retryCount = 0;

        while (true) {
            try {
                return operation.get();

            } catch (OptimisticLockingFailureException e) {
                retryCount++;

                if (retryCount >= MAX_RETRY) {
                    throw e;
                }

                try {
                    Thread.sleep(RETRY_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("낙관적 락 재시도 중 인터럽트가 발생했습니다. (재시도 횟수: " + retryCount + ")", ie);
                }
            }
        }
    }

    public static void execute(Runnable operation) {
        execute(() -> {
            operation.run();
            return null;
        });
    }
}
```
시스템 내에서 낙관적 락을 사용하는 곳은 `Point` 외 추가로 생길 수 있다는 생각으로 유틸 클래스를 만들었습니다.

실행할 함수를 파라미터로 받아, `OptimisticLockingFailureException` 예외가 발생하면 재시도 처리를 합니다.

### 낙관적 락 예외 발생 시점에 따른 버그
```java
public class PointService {
    @Transactional
    public Point chargeWithLock(Long userId, Long amount) {
        return RetryUtil.execute(() -> {
            Point point = pointRepository.findByUserIdWithLock(userId)
                    .orElseThrow(() -> new DomainException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
            point.charge(amount);
            pointRepository.save(point);
    
            PointHistory pointHistory = new PointHistory(point, amount, TransactionType.CHARGE);
            pointHistoryRepository.save(pointHistory);
    
            return point;
        });
    }
}
```
처음에는 단순하게 충전 로직 전체를 `execute` 함수로 감싸주면 되겠지 생각을 했었습니다.

하지만, 이는 낙관적 락 예외 발생 시점을 고려하지 않은 처리였습니다.
낙관적 락 예외는 조회 시점이 아닌 트랜잭션 커밋 시점에 발생하기 때문에, 위와 같은 처리로는 예외 발생을 감지할 수 없었고 재시도 처리를 정상적으로 할 수 없습니다.

따라서, `PointService.chargeWithLock` 함수를 호출하는 곳에서 해당 함수를 `execute`로 감싸주어야 했습니다.

### 트랜잭션 전파 속성
```java
public class PointFacade {
    private final PointService pointService;
    
    @Transactional
    public PointResult charge(Long userId, Long amount) {
        User user = userService.getUser(userId);
        Point point = RetryUtil.execute(() -> pointService.chargeWithLock(userId, amount));
    
        return new PointResult(
                user.getId(),
                user.getName(),
                point.getId(),
                point.getBalance()
        );
    }
}
```
이렇게 `PointService.chargeWithLock` 함수를 호출하는 `PointFacade`로 처리를 옮겼지만 원하는 데로 재시도 처리가 동작하지는 않았습니다.
`PointFacade`, `PointService` 두 곳 모두 `@Transactional`의 기본 전파 속성인 `REQUIRED`를 사용하고 있었기 때문입니다.

`REQUIRED` 전파 속성을 사용하면, 상위 메서드의 트랜잭션이 존재하면 하위 메서드도 트랜잭션에 참여하여 같은 트랜잭션에서 동작합니다.
따라서, 상위 메서드인 `PointFacade.charge`가 모두 끝나고 트랜잭션이 커밋되는 시점에 낙관적 락 예외 발생 여부를 알 수 있습니다.

```java
public class PointService {
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Point chargeWithLock(Long userId, Long amount) {
        Point point = pointRepository.findByUserIdWithLock(userId)
                .orElseThrow(() -> new DomainException(PointErrorCode.POINT_BALANCE_NOT_FOUND));
        point.charge(amount);
        pointRepository.save(point);

        PointHistory pointHistory = new PointHistory(point, amount, TransactionType.CHARGE);
        pointHistoryRepository.save(pointHistory);

        return point;
    }
}
```
`PointService.chargeWithLock` 메서드를 독립적인 트랜잭션에서 처리하도록 변경했습니다. 
이렇게 변경하면 해당 함수가 끝나는 시점에 트랜잭션을 커밋하고 외부에 있는 `execute`에서 예외를 감지할 수 있습니다.

- `REQUIRES_NEW`: 전파 속성을 항상 새로운 트랜잭션을 생성
- `NESTED`: 기존 트랜잭션이 있으면 중첩 트랜잭션 생성, 없으면 `REQUIRED`처럼 동작

위 전파속성을 통해, 메서드를 독립적인 트랜잭션에서 처리할 수 있었습니다.

### 테스트
```java
@Test
void 동시에_포인트_충전_요청시_모든_요청이_정상적으로_처리된다() throws InterruptedException {
    // given
    long userId = 1L;
    long chargeAmount = 1_000L;

    long initialBalance = pointFacade.getPoint(userId).balance();
    int requestCount = 3;
    long expectedBalance = initialBalance + (requestCount * chargeAmount);

    ExecutorService executor = Executors.newFixedThreadPool(requestCount);
    CountDownLatch latch = new CountDownLatch(requestCount);

    // when
    for (int i = 0; i < requestCount; i++) {
        executor.execute(() -> {
            try {
                pointFacade.charge(userId, chargeAmount);
            } finally {
                latch.countDown();
            }
        });
    }
    latch.await();
    executor.shutdown();

    // then
    long actualBalance = pointFacade.getPoint(userId).balance();
    assertThat(actualBalance).isEqualTo(expectedBalance);
}
```
위 테스틑 통해 재시도 처리가 정상 동작함을 확인했습니다.

## 재시도 처리 삭제
**낙관적 락을 사용하면 재시도 처리는 필수인가요?**

멘토링 시간을 통해 비즈니스적으로 재시도 처리를 해서는 안되는 로직이 있다는 점을 알게되었습니다.

예를 들어, 콘서트 좌석 예매 시스템에서 콘서트 좌석에 낙관적 락을 사용하는 경우 사용자A와 사용자B가 동시에 동일한 좌석 예매를 시도합니다.
사용자A가 락을 획득하고 사용자B는 낙관적 락 예외가 발생했다면 이는 재시도가 필요한 로직일까요?

한 좌석의 예매는 한 사람에게만 허용되어야 하기 때문에 재시도 처리를 해서는 안되는 로직일겁니다.

동일하게, 포인트 충전 및 사용에 대해 생각해 보았을 때 포인트 충전이 동시에 발생하거나 포인트 사용이 동시에 발생하는 경우는 동일한 요청이 오류로 인해 잘못 들어왔을 확률이 높습니다.

이러한 이유로 재시도 로직을 결국 삭제하게 되었습니다.

## 추가로
`RetryUtil`을 작성하며 재시도 정책과 백오프 정책에 대한 고민이 있었습니다.

유틸을 사용하는 곳 모두에서 재시도 횟수, 재시도 간격을 동일하게 설정하는게 맞을까?

일시적인 오류가 발생했을 때 작업을 자동으로 재시도할 수 있게 해주는 기능을 편리하게 제공 받을 수 있는 `spring-retry` 라이브러리에 대해 알게되었습니다.