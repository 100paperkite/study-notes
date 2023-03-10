# 2장 - 객체의 생성과 파괴

## [1] 생성자 대신 정적 팩터리 메서드를 고려하라

```java
public static Boolean valueOf(boolean b){
	return b ? Boolean.TRUE : Boolean.FALSE;
```

### 생성자 보다 좋은 점

- 이름을 가질 수 있어서 반환될 객체의 특성을 쉽게 묘사할 수 있다.
- 호출될 때 마다 인스턴스를 새로 생성하지는 않아도 된다.
  - 인스턴스를 미리 만들어두거나 인스턴스를 캐싱하여 쓸 수 있게 된다.
- 반환 타입의 하위 타입의 객체를 반환할 수 있는 능력이 있다.
  - 클래스를 자유롭게 선택할 수 있는 유연성을 제공한다.
  - Java 8 부터는 인터페이스에 정적 메서드를 선언할 수 있다. (public)
- 입력 매개변수에 따라 매번 다른 클래스의 객체를 반환할 수 있다.
- 정적 팩터리 메서드를 작성하는 시점에는 반환할 객체의 클래스가 존재하지 않아도 된다.

### 단점

- 상속을 하려면 `public` 이나 `protected` 생성자가 필요하므로 정적 팩터리 메서드만 제공하면 하위 클래스를 만들 수 없다.
- 프로그래머가 찾기 어렵기 때문에 주로 사용하는 명명 방식을 사용하자
  - `from` : 형변환 → `Date.from(date)`
  - `of` : 여러 매개변수를 받아 적합한 타입의 인스턴스를 반환하는 집계 메서드 → `EnumSet.of(JACK, QUEEN, KING)`
  - `valueOf` : `from` 과 `of` 의 자세한 버전
  - `instance` or `getInstance`
  - `create` or `newInstance` : 매번 새로운 인스턴스를 생성함을 보장
  - `getType` : = getInstance. 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의하는 경우
  - `newType` : = newInstance. 생성할 클래스가 아닌 다른 클래스에 팩터리 메서드를 정의하는 경우
  - `type` : = *getType*과 *newType*의 간결한 버전.

## [2] 생성자에 매개변수가 많다면 빌더를 고려하라

**생성자나 정적 팩터리가 처리해야 할 매개변수가 많다면 빌더 패턴을 선택하는 게 더 낫다.**

매개변수가 많은 경우, 점층적으로 생성자를 구성할 수도 있지만, 쓰지 않는 매개변수에 모두 기본 값을 지정해주어야 하고 매개변수 개수가 많아지면 클라이언트 코드를 작성하기 어렵다. 게다가 타입이 같은 매개변수가 여러개 나열되어있으면 컴파일러가 알아채지 못해 런타임 오류가 날 수도 있다.

### 빌더 패턴

필수 매개변수만으로 생성자(혹은 정적 팩터리 메서드)를 호출해 빌더 객체를 얻는다. 그 후 빌더 객체가 제공하는 세터 메서드들로 원하는 매개변수들을 설정한 후, 매개변수가 없는 `build()` 메서드를 호출해 원하는 객체를 얻는다.

빌더의 세터 메서드들은 빌더 자신을 반환하기 때문에 연쇄적으로 호출할 수 있다 - **메서드 연쇄**

```java
NutritionFacts cocaCola = new NutritionFacts.Builder(240,8) // 필수 매개변수
  .calories(100)
  .sodium(35)
  .build()
```

빌더 패턴은 계층적으로 설계된 클래스와 함께 쓰기에 좋다.

**참고) Recursive parameter, and raw type**

Raw type은 쓰지 말 것. 런타임 에러 남. 애초에 호환성을 위해 존재한 것이다.

[how generic type with a recursive type parameter along with the abstract self method allows method chaining to work properly?](https://stackoverflow.com/questions/67711974/how-generic-type-with-a-recursive-type-parameter-along-with-the-abstract-self-me)

## [3] Private 생성자나 열거 타입으로 싱글턴임을 보증하라.

싱글턴이란 인스턴스를 오직 하나만 생성할 수 있는 클래스를 말함.

하지만 클래스를 싱글턴으로 만들면 테스트하기가 어렵다 (인터페이스로 정의된게 아니라면 해당 클래스를 테스트용으로 대체할 수 없기 때문)

싱글턴을 만드는 방법은 보통 둘 중에 하나

- 생성자 private으로 두고 필드에서 public static final로 정의.
- 생성자 private으로 두고 pubilc static method으로 인스턴스에 접근.

→ 리플렉션으로 private 생성자에 접근할 수도 있으므로 예외 처리를 해야 한다.

→ 직렬화도 어렵다.(역직렬화할 때 마다 새로운 인스턴스가 만들어지게 됨)

세번 째 방법은 원소가 하나인 열거 타입을 선언하는 것이다 (추가적인 노력없이 직렬화 가능하다 - enum이니까)

단, 필드 값이 있는 경우 필드 값은 직렬화되지 않는다.

```java
public enum SingletonEnum {
	INSTANCE;

	int value;

	public void setValue(int value) {...}
}
```

## [4] 인스턴스화를 막으려거든 private 생성자를 사용하라

실수로 생성자를 호출하지 않도록 해준다.

상속도 불가능해진다 (하위 클래스에서 상위 클래스의 생성자에 접근할 수 없으므로)

## [5] 자원을 직접 명시하지 말고 의존 객체 주입을 사용하라

사용하는 자원에 따라 동작이 달라지는 클래스에는 정적 유틸리티 클래스나 싱글턴 방식이 적합하지 않다.

**인스턴스를 생성할 때 생성자에 필요한 자원을 넘겨주는 방식**을 사용하라.

클래스가 내부적으로 하나 이상의 자원에 의존하고, 그 자원이 클래스 동작에 영향을 준다면 싱글턴과 정적 유틸리티 클래스는 사용하지 않는 것이 좋으며 이 자원들을 클래스가 직접 만들게 해서도 안된다.

대신 필요한 자원 or 해당 자원을 만드는 팩터리를 생성자에 넘겨주자.

## [6] 불필요한 객체 생성을 피하라

### String

```java
String s = new String("test") // 실행될 때 마다 매번 객체가 생성됨. for 문 안에 있다면 큰일.
String s = "test" // JVM안에서 이와 똑같은 문자열 리터럴을 사용하는 모든 코드가 같은 객체를 재사용함이 보장된다.
```

### 정규 표현식

정규 표현식에서도 성능적으로 `s.matches` 메서드를 사용하기 보단 `Pattern` 인스턴스를 캐싱해두고 재사용하자.

### Auto boxing

primitive type과 박싱된 primitive type을 섞어 쓸 때 자동으로 변환해주는 기술이다. 하지만 자칫하면 박싱된 타입으로 바꾸느라 불필요한 인스턴스가 생길 수 있다.

```java
public static long sum() {
	Long sum = 0L; // 여기가 문제
	for (long i = 0; i <= Integer.MAX_VALUE; i++) {
		sum += i; // 매번 오토박싱이 일어난다.
	}
	return sum;
}
```

**박싱된 기본 타입보다는 기본 타입을 사용하고, 의도치 않은 오토박싱이 숨어들지 않도록 주의하자**

“하지만 객체 생성은 비싸니 피해야 한다”는 아니다. 요즘 JVM에서는 유효 기간이 짧은 작은 객체를 생성하고 회수하는 일은 큰 부담이 아니다.

## [7] 다 쓴 객체 참조를 해제하라

GC언어에서는 객체 참조 하나를 살려두면 해당 객체 뿐만 아니라 해당 객체가 참조하는 모든 객체를 회수할 수 없다. **자기 메모리를 직접 관리하는 클래스라면 프로그래머는 항시 메모리 누수에 주의해야 한다**

**캐시 역시 메모리 누수를 일으키는 주범이다**

## [8] finalizer와 cleaner 사용을 피하라

finalizer와 cleaner는 C++의 destructor와는 다른 개념이다(Java는 JVM의 GC가 메모리 관리를 해주기 때문에) 이 둘은 파일이나 스레드 등 종료해야 할 자원을 담고 있는 클래스에서 사용한다.

finalizer와 cleaner는 수행 시점 뿐만 아니라 수행 여부조차 보장하지 않으므로 웬만하면 사용하면 안된다.

대신에 **AutoCloseable** 를 구현해주고 인스턴스를 다 쓰고나면 `close` 메서드를 호출하자. (일반적으로는 `try-with-resource` 를 사용해야 한다)

cleaner(자바 8까지는 finalizer)는 자원 소유자가 `close` 메서드를 호출하지 않는 경우를 대비한 안전망 역할이나 중요하지 않은 네이티브 자원 회수용으로만 사용하자. 이 경우에도 불확실성과 성능 저하에 주의해야 한다.

## [9] try-finally보다는 try-with-resources를 사용하라

코드도 더 짧고 분명해지고, 만들어지는 예외 정보도 훨씬 유용하다.

게다가 try-finally를 사용하면 try에서 난 예외가 finally 예외에 의해 가려진다.

try-with-resources를 사용하려면 해당 자원이 `AutoCloseable` 인터페이스를 구현해야 한다.

try-with-resources는 호출 양 쪽에서 예외가 발생하면 첫번째 예외가 기록되고, 나머지 예외들도 버려지지는 않고 (suppressed) 꼬리표를 달고 출력된다.
