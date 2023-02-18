## [78] 공유 중인 가변 데이터는 동기화해 사용하라

`synchronized` 키워드는 해당 메서드나 블록을 한 번에 한 스레드씩 수행하도록 보장한다.

언어 명세상 `long` 과 `double` 외의 변수를 읽고 쓰는 동작은 원자적이다.

언어 명세는 스레드가 필드를 읽을 때 항상 ‘수정이 완전히 반영된 값’을 얻는다고 보장하지만, 한 스레드가 저장한 값이 다른 스레드에 ‘보이는가’는 보장하지 않는다. **동기화는 배타적 실행뿐 아니라 스레드 사이의 안정적인 통신에 꼭 필요하다. 동기화하지 않으면 메인 스레드가 수정한 값을 백그라운드 스레드가 언제 보게 될 지 보증할 수 없다. 쓰기와 읽기 모두가 동기화되지 않으면 동작을 보장하지 않는다.**

`volatile` 한정자는 항상 가장 최근에 기록된 값을 읽헤 함을 보장하므로 `volatile` 을 사용해도 되지만, 주의해서 사용해야 한다.

```java
private static volatile int nextSerialNumber = 0;
public static int generateSerialNumber() {
	// '++' 연산자는 실제로 nextSerialNumber 필드에 두 번(값을 읽고, 새로운 값 저장) 접근하므로
	// 동기화가 필요하다
	return nextSerialNumber++;
}

// `volatile`을 빼고 `synchronized` 한정자를 붙였다. 이제 동기화 문제가 발생하지 않는다.
private static int nextSerialNumber = 0;
public static synchronized int generateSerialNumber() {
	return nextSerialNumber++;
}

// `lock-free`인 `AtomicLong`을 사용하면 `synchronized`보다 성능이 우수하며 `volatile`이 제공하지 않는 원자성까지 확보할 수 있다.
private static final AtomicLong nextSerialNumber = new AtomicLong();
public static long generateSerialNumber() {
	return nextSerialNumber.getAndIncrement();
}
```

## [79] 과도한 동기화는 피하라

과도한 동기화는 성능을 떨어뜨리고, 교착상태에 빠뜨리고, 심지어 예측할 수 없는 동작을 낳는다.

**동기화 메서드나 동기화 블록 안에서는 제어를 절대로 클라이언트에게 양도하면 안된다**(동기화 영역 안에서는 재정의 할 수 있는 메서드를 호출해선 안된다)

외부 메서드는 얼마나 오래 실행될 지 알 수 없기 때문에, 동기화 블록 안에서 호출된다면 그 동안 다른 스레드는 동기화 자원을 사용하지 못하고 대기해야만 한다.

### 해결 방법

- 외부 메서드 호출을 동기화 블록 바깥으로 옮기고 - ‘**open call**’ 이라 한다, 복사본을 만들어 외부 메서드를 수행한다.
- 동시성 컬렉션 라이브러리인 `CopyOnWriteArrayList` 을 사용한다.
  - `ArrayList` 를 구현한 클래스로, 내부 변경 작업 시엔 복사본을 만들어 수행한다.
  - 수정할 일은 드물고 순회만 빈번히 일어날 때 쓰기 좋다.

**동기화 영역에서는 가능한 한 일을 적게 하라.** 과도한 동기화는 race condition으로 인해 낭비하는 시간(병렬로 실행할 기회를 잃고, 모든 코어가 메모리를 일관되게 보기위해 지연되는 시간)을 초래한다.

가변 클래스를 작성하고자 한다면 다음 두 선택지 중 하나를 따르라

- 동기화를 전혀 하지 말고, 해당 클래스를 동시에 사용해야 하는 클래스가 외부에서 알아서 동기화되게 하라
  - `Vector` 와 `Hashtable` 을 제외한 `java.util`이 이 방식을 취했다.
- 동기화를 내부에서 수행해 스레드 안전한 클래스로 만들라 - [item 82]
  - 클라이언트가 외부에서 전체 객체에 락을 거는 것 보다 월등히 개선할 수 있을 때만 사용하라
  - `java.util.concurrent` 가 이 방식을 취했다.
  - lock splitting, lock striping, nonblocking concurrency control 등을 이용해 동시성을 높여줄 수 있다.

> **_lock splitting_**: 기능마다 lock을 따로 사용한다(ex. read-lock, write-lock을 따로 두기)
> **_lock striping_**: 자료구조의 특정 부분마다 lock을 따로 사용한다(ex. Map을 SubMap으로 분리하고 각각 Lock을 사용)

## [80] 스레드보다는 실행자, 태스크, 스트림을 애용하라

작업 큐나 스레드를 직접 다루는 것은 일반적으로 삼가야 한다.

스레드를 직접 다루면 `Thread` 가 작업 단위와 수행 매커니즘 역할을 모두 수행하게 되지만 `Executor` 프레임워크에서는 작업 단위와 실행 매커니즘이 분리된다. (실행자 서비스를 ThreadPool이라 생각하면 된다)

**코드 예시**

```java
ExecutorService exec = Executors.newSingleThreadExecutor(); // 작업 큐 생성
exec.execute(runnable); // 실행 태스크 넘기기
exec.shutdown(); // 실행자를 우아하게 종료
```

이 외에도

- 특정 태스크가 완료되길 기다리기
- 태스크 모음 중 하나 or 모든 태스크가 완료되길 기다리기
- 결과를 차례대로 받기
- 태스크를 주기적으로 실행하기
- 실행자 서비스가 종료하길 기다리기

등등 많은 기능들이 존재한다.

## [81] `wait` 와 `notify` 보다는 동시성 유틸리티를 애용하라

**`wait` 와 `notify` 는 올바르게 사용하기가 아주 까다로우니 고수준 동시성 유틸리티를 사용하자.**

### **고수준 유틸리티 종류**

- **실행자 프레임워크** (Executor) → [item 80]
- **동시성 컬렉션** (concurrent collection): 동기화를 각자의 내부에서 수행하는 컬렉션
  동시성 컬렉션은 이전의 동기화한 컬렉션보다 훨씬 좋다. 이제 **`Collections.synchronizedMap` 보다는 `ConcurrentHashMap` 을 사용하는 게 훨씬 좋다.**
- **동기화 장치 (synchronized)**
  동기화 장치는 스레드가 다른 스레드를 기다릴 수 있게 한다.
  자주 쓰이는 것은 `CountDownLatch` 와 `Semaphore` 다. 그리고 가장 강력한 동기화 장치는 `Phaser` 이다.

만약 `wait` 메서드를 사용해야 한다면 다음과 같은 표준 방식을 따르라.

```java
synchronized (obj) {
	while (<조건이 충족되지 않았다>){
		obj.wait(); // 락을 놓고, 깨어나면 다시 잡는다.)
		... // 조건이 충족됐을 때의 동작을 수행한다.
}
```

**`wait` 메서드를 사용할 때는 반드시 대기 반복문(`wait loop`) 관용구를 사용하라. 반복문 밖에서는 절대로 호출하지 말라.** **또한 일반적으로 `notify` 보단 `notifyAll` 을 사용하라(모든 스레드가 깨어남을 보장하므로 항상 정확한 결과를 얻는다)**.

## [82] 스레드 안전성 수준을 문서화하라

### 스레드 안전성 수준

- **불변**: 마치 상수와 같아서 외부 동기화가 필요없다. ex. `String`, `Long`, `BigInteger`
- **무조건적 스레드 안전**: 인스턴스는 수정될 수 있지만, 내부에서 충실히 동기화하여 별도의 외부 동기화 없이 동시에 사용해도 안전하다. ex. `AtomicLong`, `ConcurrentHashMap`
- **조건부 스레드 안전**: ‘무조건적 스레드 안전’과 같지만 일부 메서드는 동시에 사용하려면 외부 동기화가 필요하다.
- **스레드 안전하지 않음**: 동시에 사용하려면 클라이언트가 동기화해야 한다. ex. `ArrayList`, `HashMap` 같은 기본 컬렉션
- **스레드 적대적**: 모든 메서드 호출을 외부에서 동기화하더라도 안전하지 않다.

‘조건부 스레드 안전’은 주의해서 문서화해야한다. 어떤 순서로 호출할 때 외부 동기화가 필요한지, 그리고 그 순서로 호출하려면 어떤 lock들을 얻어야 하는지 알려줘야 한다.

‘무조건적 스레드 안전’ 클래스를 작성할 때는 `synchronized` 메서드가 아닌 **비공개 락 객체**를 사용하여 클라이언트가 락에 접근할 수 없게 하라( `synchronized` 메서드는 공개된 락과 마찬가지이다).

```java
// 비공개 락 객체
private final Object lock = new Object(); // lock 필드는 항상 final로 선언하라.

public void foo() {
	synchronized(lock) {
		...
	}
}
```

## [83] 지연 초기화는 신중히 사용하라

지연 초기화(lazy initialization); 필드의 초기화 시점을 그 값이 처음 필요할 때까지 늦추는 기법).

지연 초기화는 **필요할 때까지는 하지 말라**. 인스턴스 생성 시 초기화 비용을 줄지만 초기화 필드에 접근하는 비용은 커진다. 멀티 스레드 환경이라면 초기화 하는 필드를 둘 이상의 스레드가 공유하는 경우 반드시 동기화해야 한다. **대부분의 상황에서 일반적인 초기화가 지연 초기화보다 낫다**

### 지연 초기화를 사용해야 한다면 올바른 방법으로 사용하라

- 인스턴스필드에 ‘이중 검사 관용구’를 사용하라 - 필드가 이미 초기화된 상황이라면 딱 한 번만 읽도록 보장하여 초기화된 필드에 접근할 때의 동기화 비용을 없애준다.

  ```java
  private volatile FieldType field;

  private FieldType getField() {
  	FieldType result = field;
  	if (result != null) { // 첫 번째 검사 (락 사용 안 함)
  		return result;
  	}

  	synchronized(this) {
  		if (field == null) { // 두 번째 검사 (락 사용)
  			field = computeFieldValue();
  		}
  		return field;
  	}
  }
  ```

- 정적 필드에는 지연 초기화 홀더 클래스 관용구를 사용하라

  ```java
  // getField()가 처음 호출되는 순간 FieldHolder.field가 처음 읽히면서 클래스 초기화를 촉발한다.
  private static class FieldHolder {
  	static final FieldType field = computeFieldValue();
  }

  private static FieldType getField() { return FieldHolder.field; }
  ```

## [84] 프로그램의 동작을 스레드 스케줄러에 기대지 말라

운영체제는 스레드의 스케줄링을 담당하며, 스케줄링 정책은 운영체제마다 다를 수 있다. **정확성이나 성능이 스레드 스케줄러에 따라 달라지는 프로그램이라면 다른 플랫폼에 이식하기 어렵다**

**실행 가능한 스레드의 평균적인 수를 프로세서 수보다 지나치게 많아지지 않도록** 한다면 스레드 스케줄링 정책이 아주 상이한 시스템에서도 동작이 크게 달라지지 않는다.

**실행 가능한 스레드의 수를 적게 유지하는 기법**

- 작업을 완료한 이후에는 대기하라. **스레드는 당장 처리해야 할 작업이 없다면 실행되어선 안된다**
  - Executor 프레임워크를 예로 들면, 스레드 풀 크기를 적절히 설정하고 작업은 짧게 유지하면 된다(단 너무 짧으면 컨텍스트 스위칭 비용이 크기 때문에 오히려 성능을 떨어뜨릴 수 있다).
- 스레드는 **busy waiting** 상태가 되면 안된다.
-

특정 스레드가 CPU 시간을 충분히 얻지 못한다고 해서 **Thread.yield를 써서 문제를 해결하려고 하면 안된다**. 테스트 할 수단도 없으며, 다른 환경에선 오히려 느려질 수도 있다.
