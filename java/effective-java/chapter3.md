# 3장 - 모든 객체의 공통 메서드

final이 아닌 Object 메서드들을 언제 어떻게 재정의해야 하는가.

## [10] `equals` 는 일반 규약을 지켜 재정의하라

다음과 같은 경우들이 아니면 재정의하지 않는 것이 최선이다.

- 각 인스턴스가 본질적으로 고유하다. ex. Thread
- 인스턴스의 논리적 동치성을 검사할 일이 없다.
- 상의 클래스에서 재정의한 equals가 하위 클래스에서도 딱 들어맞는다.
- 클래스가 private이거나 package-privated이고 equals 메서드를 호출할 일이 없다.
  - 실수로라도 호출되는 것을 막고 싶다면 다음과 같이 정의해두어 호출을 회피하자
    ```java
    @Override public boolean equals(Object o){
    	throw new AssertionError(); // 호출 금지!
    }
    ```

equals를 재정의 해야 하는 때는, 객체가 물리적으로 같은지(identity)가 아니라 논리적으로 동일한지(값이 같은지)를 확인해야 하는데, 상위 클래스의 equals가 논리적 동치성을 비교하도록 재정의 되지 않았을 때다. 주로 값 클래스(`Integer` , `String`)들이 해당한다.

하지만 값 클래스라해도 같은 인스턴스가 둘 이상 만들어지지 않음을 보장한다면 재정의하지 않아도 된다.

### equals 메서드의 일반 규약

- 반사성: null이 아닌 모든 참조 값 x에 대해 x.equals(x)는 참이다
- 대칭성: null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)가 참이면 반대도 참이다
- 추이성: null이 아닌 모든 참조 값 x,y,z에 대해 x.equals(y)가 참이면 y.equals(z)도 참이다
- 일관성: null이 아닌 모든 참조 값 x,y에 대해 x.equals(y)를 반복해서 호출하면 항상 같은 값을 반환한다.
- null-아님: null이 아닌 모든 참조 값 x에 대해 x.equals(null)은 거짓이다.

## [11] `equals` 를 재정의하려거든 `hashCode` 도 재정의하라

equals(Object)가 두 객체를 같다고 판단했다면, 두 객체의 hashCode는 똑같은 값을 반환해야 한다.

그렇지 않으면 인스턴스를 `HashMap` 이나 `HashSet` 같은 컬렉션의 원소로 사용할 때 문제를 일으킬 것이다.

## [12] `toString` 을 항상 재정의하라

Object의 기본 toString 메서드가 사용자 정의 클래스에 적합한 문자열을 반환하는 경우는 거의 없다.

대부분 단순히 **클래스이름@16진수로*표현한*해시코드**를 반환할 뿐이다.

toString메서드는 객체를 println, printf, 문자열 연결 연산자(+), assert 구문에 넘길 때 자동으로 불린다.

toString은 그 객체가 가진 주요 정보 모두를 반환하는 게 좋다.

## [13] `clone` 재정의는 주의해서 진행하라

새로운 인터페이스를 만들 때는 절대 Cloneable을 확장해서는 안 되며, 새로운 클래스도 이를 구현해서는 안된다.

기본적인 원칙은 ‘복제 기능은 생성자와 팩터리를 이용하는 것’이 최고이다.

```java
public Person(Person persom) { ... }; // 복사 생성자
public static Person newPerson(Person person) { ... }; // 복사 팩터리
```

단 배열은 `clone` 메서드 방식이 가장 깔끔한, 이 규칙의 예외이다.

## [14] `Comparable` 을 구현할 지 고려하라

해당 메서드의 성격은 Object의 equals와 같다.

다른 점은 compareTo는 단순 동치성 비교에 더해 순서까지 비교할 수 있다는 점이다.

Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 ‘자연적인 순서’가 있음을 뜻한다.

그래서 `Comparable` 을 구현한 객체들의 배열은 `Arrays.sort(a)` 처럼 손쉽게 정렬할 수 있다.

compareTo 메서드에서 정수 기본 타입 필드를 비교할 때는 박싱된 기본 타입 클래스들의 정적 메서드 `compare` 을 이용하거나 Comparator 인터페이스가 제공하는 비교자 생성 메서드를 이용하라
