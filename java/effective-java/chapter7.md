# 7장 - 람다와 스트림

## [42] 익명 클래스보다는 람다를 사용하라

자바 8부터 추상 메서드가 하나인 인터페이스들의 인스턴스는 ‘**람다식**’을 사용해 만들 수 있다. ‘람다’는 함수나 익명 클래스와 개념은 비슷하지만 코드는 훨씬 간결하다.

```java
// 낡은 기법
Collections.sort(word, new Comparator<String>() {
    public int compare(String s1, String s2) {
        return Integer.compare(s1.length(), s2.length());
    }
}

// 람다
Collections.sort(word, (s1, s2) -> Integer.compare(s1.length(), s2.length());

// 람다 (더 간결. 비교자 생성 메서드)
Collections.sort(word, (s1, s2) -> comparingInt(String::length));

// `List` 인터페이스의 `sort` 사용
word.sort(comparingInt(String::length));
```

람다, 매개변수, 반환값의 타입은 컴파일러가 추론해준다. **타입을 명시해야 코드가 명확할 때만 제외하고는, 람다의 모든 매개변수 타입은 생략하자.** 그리고 컴파일러는 타입을 추론하는데 필요한 타입 정보 대부분을 **제네릭**으로 부터 얻으므로 제네릭을 잘 쓰는 것이 중요하다.

하지만 메서드나 클래스와 달리, **람다는 이름이 없고 문서화도 못한다. 따라서 코드 자체로 동작이 명확히 설명되지 않거나 코드 줄 수가 많아지면 람다를 쓰지 말아야 한다**(한 줄이 가장 좋고, 길어야 3줄 이내로 끝내는 것이 좋다)

### 익명클래스를 사용해야 하는 경우

- 추상 클래스의 인스턴스를 만드는 경우
- 추상 메서드가 여러개인 인터페이스
- 함수 객체가 자신을 참조해야 하는 경우 (람다식 내의 `this` 는 바깥 인스턴스를 가리킨다)
-

## [43] 람다보다는 메서드 참조를 사용하라

람다가 익명 클래스보다 나은 점 중에서 가장 큰 특징은 간결함이다. 그런데 람다보다도 함수 객체를 더 간결하게 만드는 방법은 바로 **메서드 참조**이다. (람다로 할 수 없는 일이라면 메서드 참조로도 할 수 없다)

```java
// 키가 맵 안에 없다면 키와 숫자 1을 매핑하고, 이미 있다면 기존 매핑 값을 증가시킨다.
map.merge(key, 1, (count, incr) -> count + incr);

// 자바 8부터 Integer 클래스는 위 람다와 기능이 같은 `sum`메서드를 제공하므로,
// 메서드 참조를 전달하면 똑같은 결과를 더 보기 좋게 얻을 수 있다.
map.merge(key, 1, Integer::sum);
```

### 메서드 참조의 유형

| 메서드 참조 유형    | 예                     | 같은 기능을 하는 람다         |
| ------------------- | ---------------------- | ----------------------------- |
| 정적                | Integer::parseInt      | str → Integer.parseInt(str)   |
| 한정적(인스턴스)    | Instant.now()::isAfter | Instant then = Instant.now(); |
| t →then.isAfter(t); |
| 비한정적(인스턴스)  | String::toLowerCase    | str → str.toLowerCase()       |
| 클래스 생성자       | TreeMap<K,V>::new      | () → new TreeMap<K,V>();      |
| 배열 생성자         | int[]::new             | len → new int[len]            |

**메서드 참조 쪽이 짧고 명확하다면 메서드 참조를 쓰고, 그렇지 않을 때만 람다를 사용하라**

## [44] 표준 함수형 인터페이스를 사용하라

**함수형 인터페이스를 직접 구현하지말고, 필요한 용도에 맞는 게 있다면 `java.util.function` 패키지의 표준 함수형 인터페이스를 활용하라**

### 기본 함수형 인터페이스

| 인터페이스        | 함수 시그니처       | 예                  | 비고                                            |
| ----------------- | ------------------- | ------------------- | ----------------------------------------------- |
| UnaryOperator<T>  | T apply(T t)        | String::toLowerCase | 인수가 1개인 반환값과 인수의 타입이 같은 함수   |
| BinaryOperator<T> | T apply(T t1, T t2) | BigInteger::add     | 인수가 2개인 반환값과 인수의 타입이 같은 함수   |
| Predicate<T>      | boolean test(T t)   | Collection::isEmpty | 인수 하나를 받아 boolean을 반환하는 함수        |
| Function<T,R>     | R apply(T t)        | Arrays::asList      | 인수와 반환 타입이 다른 함수                    |
| Supplier<T>       | T get()             | Instant::now        | 인수를 받지 않고 반환값을 제공하는 함수         |
| Consumer<T>       | void accept(T t)    | System.out::println | 인수를 받고 반환값은 없는(인수를 소비하는) 함수 |

기본 함수형 인터페이스는 기본 타입인 `int`, `long`, `double` 용으로 각 3개씩 변형이 존재하고, 인수를 2개씩 받는 변형 등 여러 변형이 존재한다. → 총 43개

웬만하면 이미 구현된 표준 함수형 인터페이스들 중 하나를 사용하는 것이 낫지만, 다음 중 하나 이상을 만족한다면 전용 함수형 인터페이스를 따로 구현해야 하는 건 아닌지 고민해보자

- 자주 쓰이며, 이름 자체가 용도를 설명해준다
- 반드시 따라야 하는 규약이 있다
- 유용한 디폴트 메서드를 제공할 수 있다.

그리고 **직접 만든 함수형 인터페이스에는 항상 `@FunctionalInteface` 애너테이션을 사용하라.** 해당 인터페이스가 람다용으로 설계된 것임을 알려주며, 누군가 실수로 메서드를 추가하지 못하게 막아준다(추상 메서드가 하나뿐이어야 하므로)

## [45] 스트림은 주의해서 사용하라

스트림 API는 다량의 데이터 처리 작업(순차 or 병렬)을 돕고자 자바 8부터 추가되었다.

**Stream**

steam은 데이터 원소의 유한 혹은 무한 시퀀스를 뜻하고, stream pipeline은 이 원소들로 수행하는 연산 단계를 표현하는 개념이다. 스트림 안의 데이터 원소들은 객체 참조나 기본 타입 값(`int`, `long`, `double`이다. **`char`은 없다 -** `char` 값을 처리할 때는 스트림을 삼가는 편이 낫다.)

**Stream pipeline**

- 스트림 파이프라인은 소스 스트림에서 시작해 마지막 연산(terminal operation)으로 끝나며 하나 이상의 중간 연산이 있을 수 있고, 중간 연산은 스트림을 변환한다.
- 스트림 파이프라인은 지연 평가(lazy evaluation)된다. 평가는 마지막 연산이 호출될 때 이뤄지며, 마지막 연산에 쓰이지 않는 데이터 원소는 계산에 쓰이지 않는다. (마지막 연산이 없는 스트림 파이프라인은 아무 일도 하지 않으므로 빼먹으면 안된다.)

하지만 **스트림을 과용하면 프로그램이 읽거나 유지보수하기 어려워질 수 있다.**

### 스트림이 안성맞춤인 경우

- 원소들의 시퀀스를 일관되게 변환한다
- 원소들의 시퀀스를 필터링한다
- 원소들의 시퀀스를 하나의 연산을 사용해 결합한다
- 원소들의 시퀀스를 컬렉션에 모은다 (공통된 속성을 기준으로 묶는다)
- 원소들의 시퀀스에서 특정 조건에 만족하는 원소를 찾는다.

## [46] 스트림에서는 부작용 없는 함수를 사용하라

스트림 패러다임의 핵심은 계산을 일련의 변환으로 재구성하는 것이다. 그리고 각 변환단계는 가능한 한 이전 단계의 결과를 받아 처리하는 **순수 함수**(입력만이 결과에 영향을 주는 함수. 다른 가변 상태를 참조하지 않고, 함수 스스로도 다른 상태를 변경하지 않는다)여야 한다.

이렇게 하려면 스트림 연산에 건네는 함수 객체는 모두 **사이드 이펙트**가 없어야 한다.

```java
// BAD
Map<String, Long> freq = new HashMap<>();
try (var words = new Scanner(file).tokens()){
    words.forEach(word -> freq.merge(word.toLowerCase(), 1L, Long::sum));
}
```

위 코드는 스트림 코드를 가장한 반복적 코드다. 이는 스트림 API의 이점을 살리지 못하여 같은 기능의 반복적 코드보다 읽기 어렵고 유지보수에 좋지 않다. `forEach` 가 그저 스트림이 수행한 연산 결과를 보여주는 일 이상(람다가 상태를 수정한다)을 하기 때문이다. **`forEach` 연산은 terminal 연산 중 가장 기능이 적고 제일 ‘덜’ 스트립답다. `forEach` 연산은 스트림 계산 결과를 보고할 때만 사용하고, 계산할 때는 사용하지 말자.**

```java
// GOOD
Map<String, Long> freq;
try (var words = new Scanner(file).tokens()){
    freq = words.collect(groupingBy(String::toLowerCase, counting()));
}
```

### Collector

위 코드는 수집기(collector)를 사용하는데, 수집기를 사용하면 스트림의 원소를 손쉽게 컬렉션으로 모을 수 있다. `java.util.stream.Collectors` 클래스는 메서드를 43개나 가지고 있는데, 자주 쓰이는 메서드들을 알아보자.

**toList**

```java
// 빈도표에서 가장 흔한 단어 10개를 뽑아내는 파이프라인
// `toList`는 `Collectors`의 메서드다. 이렇게 정적 임포트해서 쓰면 가독성이 좋아진다.
List<String> topTen = freq.keySet().stream()
    .sorted(comparing(freq::get).reversed())
    .limit(10)
    .collect(toList());
```

**toMap(keyMapper, valueMapper)**

```java
// toMap 수집기를 사용하여 문자열을 열거 타입 상수에 매핑한다.
Map<String, Operation> stringToEnum =
		Stream.of(Operation.values()).collect(toMap(Object::toString, e->e));

// 마지막에 쓴 값을 취하는 수집기
toMap(keyMapper, valueMapper, (oldVal, newVal) -> newVal);
```

**groupingBy**

입력으로 분류 함수(classifier)를 받고 출력으로는 원소들을 카테고리별로 모아 놓은 맵을 담은 수집기를 반환한다.

```java
// 알파벳화한 단어를 알파벳화 결과가 같은 단어들의 리스트로 매핑하는 맵
words.collect(groupingBy(word -> alphabetize(word));

// 리스트 외의 값을 갖는 맵을 생성하게 하려면 `downstream` 수집기도 명시해야 한다.
words.collect(groupingBy(word -> alphabetize(word), toSet()); // 집합을 값으로 갖는다
words.collect(groupingBy(word -> alphabetize(word), toCollection(collectionFactory)); // 원하는 컬렉션 타입을 값으로 갖는 맵을 생성한다.
```

**joining**

문자열 등의 `CharSequence` 인스턴스의 스트림에만 적용할 수 있다. 구분문자(delimeter)를 받아 해당 원소들을 연결하는 수집기를 반환한다.

## [47] 반환 타입으로는 스트림보다 컬렉션이 낫다

스트림은 반복을 지원하지 않으므로 스트림과 반복을 알맞게 조합해야 좋은 코드가 나온다.

API를 스트림만 반환하도록 짜두면 반환된 스트림을 for-each로 반복하길 원하는 사용자가 불편해 할 것이다.

객체 시퀀스를 반환하는 메서드를 작성하는데 이 메서드가 오직 스트림 파이프라인에서만 쓰인다면 스트림을 반환하자

```java
public static <E> Stream<E> streamOf(Iterable<E> iterable) {
    return StreamSupport.stream(iterable.spliterator(), false);
}
```

반대로 반환된 객체들이 반복문에서만 쓰일 걸 안다면 `Iterable` 을 반환하자.

```java
// Stream<E>를 Iterable<E>로 중개해주는 어댑터
public static <E> Iterable<E> iterableOf(Stream<E> stream) {
    return stream::iterator;
}
```

하지만 공개 API를 작성할 때는 두 방식 다 제공해야 한다. Collection 인터페이스는 반복과 스트림을 동시에 지원하므로 **원소 시퀀스를 반환하는 공개 API의 반환 타입에는 `Collection` 이나 그 하위 타입을 쓰는 게 일반적으로 최선이다**.(Arrays역시 Arrays.asList와 Stream.of 메서드로 손쉽게 반복과 스트림을 지원할 수 있다)

반환하는 시퀀스의 크기가 메모리에 올려도 될 만큼 작다면 `ArrayList` 나 `HashSet` 같은 표준 컬렉션 구현체를 반환하는 것이 최선일 수 있다.

## [48] 스트림 병렬화는 주의해서 적용하라

**데이터 소스가 `Stream.iterate` 거나 중간 연산으로 `limit` 을 쓰면 파이프라인 병렬화로는 성능 개선을 기대할 수 없다** (병렬화는 `limit` 을 다룰 때 CPU코어가 남는다면 원소를 몇 개 더 처리한 후 초과된 결과를 버릴 수 있기 때문에 불필요한 시간이 더 소요될 수 있다)

**스트림의 소스가 `ArrayList`, `HashMap`, `HashSet`, `ConcurrentHashMap` 의 인스턴스거나 배열, `int` 범위, `long` 범위일 때 병렬화의 효과가 가장 좋다**

- 데이터를 원하는 크기로 정확하고 손쉽게 나눌 수 있어서 일을 다수의 스레드에 분배하기에 좋다
- 또, 참조 지역성이 높아(메모리에 연속적으로 저장되어있어) 다량의 데이터를 처리하는 연산을 병렬화할 때 중요한 요소로 작용한다.

terminal operation 역시 병렬 수행 효율에 영향을 준다. 가장 적합한 연산은 원소를 하나로 합치는 축소(reduction)다 - `min`, `max`, `count`, `sum` 과 같이 완성된 형태로 제공되는 메서드 중 하나를 선택해 수행.

**스트림을 잘못 병렬화하면 (응답 불가를 포함해) 성능이 나빠질 뿐만 아니라 결과 자체가 잘못되거나 예상치 못한 동작이 발생할 수 있다. 스트림 병렬화는 오직 성능 최적화 수단임을 기억해야 하며 반드시 전후 성능을 테스트해서 병렬화 가치가 있는 지 확인해야 한다.**
