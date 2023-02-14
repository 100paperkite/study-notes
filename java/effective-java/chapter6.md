# 6장 - 열거 타입과 애너테이션

열거 타입과 애너테이션을 올바르게 사용하는 방법을 알아보자.

## [34] `int` 상수를 나열하는 대신 열거 타입을 사용하라

**필요한 원소를 컴파일 타임에 다 알 수 있는 상수 집합이라면 항상 열거 타입을 사용하자**

자바의 열거타입은 다른 언어(단순한 정숫값)와 달리 완전한 형태의 클래스이다.

상수 하나당 자신의 인스턴스를 하나씩 만들어 `public static final` 필드로 공개하므로 열거 타입 선언으로 만들어진 **인스턴스는 딱 하나만 존재함**이 보장된다.

**vs 상수**

- 열거 타입은 상수와 달리 컴파일 타입 안정성을 제공한다. (열거 타입끼리만 비교가 가능하다)
- 열거 타입에 새로운 상수를 추가하거나 순서를 바꿔도 다시 컴파일하지 않아도 된다. (공개되는 것이 오직 필드 이름뿐이다)
- `toString()` 메서드가 출력에 적합한 문자열을 생성한다.
- 열거 타입에는 임의의 메서드나 필드를 추가하거나 인터페이스를 구현하게 할 수도 있다.

### 상수마다 동작이 달라져야 하는 상황에서는 `switch` 가 아닌 상수별 메서드 구현을 이용하라

```java
// BAD. 좋지 않은 코드. 깨지기 쉽다.
public enum Operation {
    PLUS, MINUS, TIMES, DIVIDE;

    public double apply(double x, double y){
        switch (this) {
            case PLUS:  return x+y;
            case MINUS: return x-y;
            case TIMES: return x*y;
            case DIVIDE:return x/y;
        }
        throw new AssertionError("알 수 없는 연산: "+ this);
    }
}
```

```java
// GOOD. 추상 메서드를 선언하고 각 상수별 클래스 몸체에서 자신에 맞게 재정의한다.
public enum Operation {
    PLUS("+") {public double apply(double x, double y) {return x+y;}},
    MINUS("-") {public double apply(double x, double y) {return x-y;}},
    TIMES("*") {public double apply(double x, double y) {return x*y;}},
    DIVIDE("/") {public double apply(double x, double y) {return x/y;}};

		private final String symbol;

		Operation(String symbol) {this.symbol = symbol;}

		@Override public String toString() { return symbol; }

    public abstract double apply(double x, double y);

}
```

switch문은 열거 타입의 상수별 동작을 구현하는 데 적합하지 않지만, **기존 열거 타입에 상수별 동작을 혼합해 넣을 때는 `switch` 문이 좋은 선택이 될 수 있다**

```java
// 서드파티에서 가져온 `Operation`열거 타입의 연산들의 반대 연산들을 반환해야 할 때.
public static Operation inverse(Operation op) {
	switch(op) {
		case PLUS: return Operation.MINUS;
		case MINUS: return Operation.PLUS;
		case TIMES: return Operation.DIVIDE;
		case DIVIDE: return Operation.TIMES:

		default: throw new AssertionError("알 수 없는 연산: " + op);
}
```

## [35] `ordinal` 메서드 대신 인스턴스 필드를 사용하라

**열거 타입 상수에 연결된 값은 `ordinal` 메서드로 얻지 말고, 인스턴스 필드에 저장하자.**

Enum.ordinal 은 웬만해선 사용하면 안된다(거의 쓸 일이 없다)

```java
// 동작은 하지만 유지보수하기 힘든 코드이다.
// 상수 선언 순서를 바꾸는 순간 `numberOfMusicians`는 오동작한다.
// 심지어 값을 중간에 비워둘 수도 없다.
public enum Ensemble {
	SOLO, DUET, TRIO, QUARTET, QUINTET,
	SEXTET, SEPTET, OCTET, NONET, DECTET;

	public int numberOfMusicians() { return ordinal() + 1; }
}

// 이렇게 인스턴스 필드로 정의하라.
public enum Ensemble {
	SOLO(1), DUET(2), TRIO(3), QUARTET(4), QUINTET(5),
	SEXTET(6), SEPTET(7), OCTET(8), NONET(9), DECTET(10);

	private final int numberOfMusicians;
	Ensemble(int size) { this.numberOfMusicians = size; }
	public int numberOfMusicians() { return numberOfMusicians; }
}
```

## [36] 비트 필드 대신 `EnumSet` 을 사용하라

```java
// 비트 필드 열거 상수. text.applyStyles(STYLE_BOLD | STYLE_ITALIC); 처럼 쓴다.
public class public class Text {
    public static final int STYLE_BOLD          = 1 << 0; // 1
    public static final int STYLE_ITALIC        = 1 << 1; // 2
    public static final int STYLE_UNDERLINE     = 1 << 2; // 4
    public static final int STYLE_STRIKETHROUGH = 1 << 3; // 8

    public void applyStyles(int styles) {...}
}
```

비트 필드를 사용하면 비트별 연산을 사용해 합집합과 같은 집합 연산을 효율적으로 수행할 수 있지만, 비트 필드는 정수 열거 상수의 단점을 그대로 지니면서 추가적으로 다음과 같은 문제를 지닌다.

- 비트 필드 값은 정수 열거 상수를 출력할 때보다 해석하기 힘들다.
- 비트 필드를 보고 어떤 원소가 있는지 순회하기 까다롭다.
- 최대 몇 비트가 필요한지 미리 예측해서 선택해야 한다.

```java
public class Text {
		public enum Style { BOLD, ITALIC, UNDERLINE, STRIKETHROUGH }

		// 어떤 Set을 넘겨도 상관없지만, EnumSet이 가장 좋다.
		public void applyStyles(Set<Style> styles) { ... }
}
```

`text.applyStyles(**********EnumSet.of(Style.Bold, Style.ITALIC))`\*\* 처럼 쓰면 된다.

## [37] `ordinal` 인덱싱 대신 `EnumMap` 을 사용하라

Enum을 Key로 묶을 일이 있을 때, 배열을 선언 한 후 Enum의 ordinal을 인덱스로 사용하지 말고 `EnumMap` 을 사용하라. 다차원 관계는 `EnumMap<..., EnumMap<...>>` 으로 표현하라.

```java
// EnumMap의 생성자가 받는 키 타입의 Class 객체는 한정적 타입 토큰으로, 런타임 제네릭 정보를 제공해야 한다.
Map<Plant.LifeCycle, Set<Plant>> plantsByLifeCycle = new EnumMap<>(Plant.LifeCycle.class);

// Stream을 사용한 코드 1 - EnumMap을 사용하지 않는다.
Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle));

// Stream을 사용한 코드 2 - EnumMap을 사용한다. (공간/성능 상 이점 존재)
Arrays.stream(garden).collect(groupingBy(p -> p.lifeCycle, () -> new EnumMap<>(LifeCycle.class), toSet())));
```

## [38] 확장할 수 있는 열거 타입이 필요하면 인터페이스를 사용하라

대부분 상황에서 열거 타입을 확장하는 건 좋지 않은 생각이다.

1. 확장한 타입의 원소는 기반 타입의 원소로 취급되지만 반대는 성립하지 않아서 이상하기도 하고, 2) 기반 타입과 확장 타입 원소 모두를 순회할 방법도 마땅치 않으며, 3) 확장성을 높이려면 고려해야 할 요소가 늘어나서 설계와 구현이 복잡해진다.

하지만 연산 코드(Operation code)의 경우에는 이런 쓰임이 어울리는데(이따금 기본 연산 외에 사용자 확장 연산을 추가할 수 있도록 열어줘야 하는 경우), 이 경우엔 연산 코드용 인터페이스를 정의하고 열거 타입이 이 인터페이스를 구현하게 하면 된다.

```java
public interface Operation {
		double apply(double x, double y);
}
```

## [39] 명명 패턴보다 애너테이션을 사용하라

**명명 패턴**: 시그니처로 구분하는 패턴. (ex. 테스트 메서드는 무조건 `test` 로 시작)

**애너테이션으로 할 수 있는 일을 명명 패턴으로 처리할 이유는 없다. 애너테이션으로 메서드를 구분하라.**

(컨테이너 애너테이션 타입에는 적절한 보존 정책(`@Retention` 과 `@Target` 을 명시해야 컴파일 가능하다)

## [40] `@Override` 애너테이션을 일관되게 사용하라

재정의한 모든 메서드에 `@Override` 애너테이션을 의식적으로 달면 실수했을 때 컴파일러가 바로 알려줄 것이다. (ex. 실수로 오버라이딩이 아니라 오버로딩 한 경우). 단 구체 클래스에서 상위 클래스의 추상메서드를 재정의한 경우엔 굳이 달지 않아도 된다.

## [41] 정의하려는 것이 타입이라면 마커 인터페이스를 사용하라

**마커 인터페이스**; 아무 메서드도 담고 있지 않고 자신을 구현하는 클래스가 특정 속성을 가짐을 표시해주는 인터페이스.

**마커 애너테이션 vs 마커 인터페이스**

- 마커 인터페이스는 엄연히 타입이므로, 이를 구현한 클래스의 인스턴스를 구분하는 타입으로 쓸 수 있다.
- 마커 인터페이스는 적용 대상을 더 정밀하게 지정할 수 있다. (애너테이션은 부착할 수 있는 타입을 세밀하게 조정하지 못한다)
- 마커 애너테이션은 거대한 애너테이션 시스템의 지원을 받는다는 점에서 낫다.
- 마커를 인터페이스나 클래스에 적용해야 한다면, **‘마킹이 된 객체를 매개변수로 받는 메서드를 작성할 일이 있다’**면 마커 인터페이스를 써야 한다.

**적용 대상이 `Element.TYPE` 인 마커 애너테이션을 작성하고 있다면, 마커 인터페이스가 낫지는 않을지 곰곰이 생각해보자**
