# 5장 - 제네릭

자바 5부터 사용가능한 제네릭 덕분에 컴파일 시점에 타입 에러를 감지할 수 있게 되었다.

## [26] Raw 타입은 사용하지 말라

클래스와 인터페이스 선언에 타입 매개변수가 쓰이면 이를 **제네릭 클래스** 혹은 **제네릭 인터페이스**라 하며 이를 통틀어 **제네릭 타입** 이라 한다.

제네릭 타입을 하나 정의하면 Raw type(제네릭 타입에서 타입 매개변수를 사용하지 않은 것)도 함께 정의된다. Raw type은 제네릭 타입 정보가 전부 지워진 것 처럼 동작하는데 이는 **제네릭이 없던 때의 코드와 호환성을 유지하기 위해** 존재한다.

**Raw type을 쓰면 제네릭이 안겨주는 안정성(컴파일러 단에서 에러를 먼저 감지)과 표현력을 모두 잃게 되므로 절대 써서는 안된다**

제네릭 타입을 쓰고 싶지만 실제 타입 매개변수가 무엇인지 신경쓰고 싶지 않다면 Wildcard(`?`)를 사용하자.

```java
static int numElementsInCommon(Set<?> s1, Set<?> s2) { ... } // ok
static int numElementsInCommon(Set s1, Set s2) { ... } // !!! Raw 타입을 써서는 안된다.
```

### 예외

- `class literal` 에는 Raw type을 써야 한다. ex. `List.class`, `String[].class`, `int.class`
- `instance of` 연산자: 런타임에는 제네릭 타입 정보가 지워지므로 불필요한 제네릭 표시는 코드만 지저분하게 만든다.
  ```java
  if (o instanceof Set) {  // raw type
  	Set<?> s = (Set<?>) o; // Set 타입임을 확인한 후엔 와일드카드 타입으로 형변환 해야 한다.
  }
  ```

## [27] 비검사 경고를 제거하라

제네릭을 사용하기 시작하면 수많은 컴파일러 경고를 보게 될 것이다. 비검사 경고는 런타임에 `ClassCastException` 을 일으킬 수 있는 잠재적 가능성을 뜻하므로 **할 수 있는 한 모든 비검사 경고를 제거하라. 경고를 제거할 수는 없지만 타입 안전하다고 확신할 수 있다면 선언에 `@SuppressWarnings("unchecked")` 에너테이션을 달아 경고를 숨기자**

## [28] 배열보다는 리스트를 사용하라

배열은 **’공변(covariant)’ → `Sub` 가 `Super` 의 하위 타입이라면 배열 `Sub[]`는 배열 `Super[]`의 하위 타입.’**이다.

반면 제네릭은 **불공변(invariant)**이다. → `**List<T>**` 는 `**List<E>**` 와 어떤 관계도 가지지 않는다.

```java
// 런타임 실패
Object[] objArray = new Long[1];
objArray[0] = "타입이 달라 넣을 수 없다"; // ArrayStoreException을 던진다.

// 컴파일 실패
List<Object> ol = new ArrayList<Long>;
ol.add("타입이 달라 넣을 수 없다");
```

배열에서는 실수를 런타임에야 알 수 있게 되지만 리스트를 사용하면 컴파일 시점에서 바로 알 수 있다.

이런 차이로 인해 배열과 제네릭은 잘 어우러지지 못하며 배열은 **제네릭 타입, 매개변수화 타입, 타입 매개변수**로 사용할 수 없다. `new List<E>[]`, `new List<String>[]`, `new E[]` 는 컴파일되지 않는다.

제네릭 컬렉션에서는 자신의 원소 타입을 담은 배열을 반환하는게 보통은 불가능하다. 배열로 형변환할 때 제네릭 배열 생성 오류나 비검사 형변환 경고가 뜨는경우는 배열인 `E[]` 대신 `List<E>` 를 사용하면 해결된다.

```java
public class Chosser<T> {
	private final T[] choiceArray;

	public Chooser(Collection<T> choices) {
		choiceArray = (T[]) choices.toArray(); // 동작은 하지만 해당 형변환이 런타임에 안전한지 보장할 수 없기 때문에 컴파일 경고가 난다.
}
```

## [29] 이왕이면 제네릭 타입으로 만들라

Object 기반 스택을 살펴보자. 해당 클래스는 제네릭 타입이어야 마땅하므로 제네릭 타입으로 바꿔보자.

```java
public class Stack {
    private Object[] elements;
    private int size;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
        elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object e){
        elements[size++] = e;
    }

    private void ensureCapacity(){
        if (elements.length == size){
            elements = Arrays.copyOf(elements, 2* size+1);
        }
    }
		// ... 생략
}
```

하지만 `E` 는 실체화 불가 타입이므로 `new E[]` 에서 오류가 난다.

```java
private E[] elements;
...
public Stack() {
    elements = new E[DEFAULT_INITIAL_CAPACITY]; // !!! error
}
```

### 배열을 사용한 코드를 제네릭으로 만드는 방법 1

```java
// push 메서드를 통해 배열에 저장되는 원소의 타입은 E이므로 비검사 형변환은 안전하다.
@SuppressWarning("unchecked")
public Stack() {
	elements = (E[]) new Object[DEFAULT_INITIAL_CAPACITY];
}
```

### 배열을 사용한 코드를 제네릭으로 만드는 방법 2

```java
private Object[] elements; // 필드 타입을 Object로 바꾸고 원소를 읽을 때 마다 형변환한다.
public E pop() {
	...
	// push에서 E 타입만 허용하므로 이 형변환은 안전하다.
	@SuppressWarnings("unchecked") E result = (E) elements[--size];
	...
}
```

## [30] 이왕이면 제네릭 메서드로 만들라

제네릭 타입과 마찬가지로 클라이언트에서 입력 매개변수와 반환값을 명시적으로 형변환해야 하는 메서드보다 제네릭 메서드가 더 안전하며 사용하기도 쉽다.

```java
public static <E> Set<E> union(Set<E> s1, Set<E> s2){
    Set<E> result = new HashSet<>();
    result.addAll(s2);
    return result;
}
```

## [31] 한정적 와일드카드를 사용해 API 유연성을 높이라

매개변수화 타입은 불공변이므로 `List<Type1>` 은 `List<Type2>`와 같지 않다. 하지만 이를 곰곰히 생각해보면 이는 유연하지 않다. `Stack<Number>` 로 선언한 후 `Integer`(`Number`의 하위 타입)을 넣으려고 하는 것은 논리적으로 잘 동작해야 할 것 같지만 실제로는 동작하지 않는다.

이럴 때 **한정적 와일드 카드 타입**이란 특별한 매개변수화 타입을 이용하라.

**단, 반환 타입에는 한정적 와일드카드 타입을 사용하면 안된다.** 클라이언트 코드에서도 와일드카드 타입을 써야 하기 때문이다

```java
public void pushAll(Iterable<? extends E> src) { // 입력 매개변수의 타입이 E의 하위 타입.
	... // src 원소들을 스택에 push
}

public void popAll(Collection<? super E> dst) { // 입력 매개변수의 타입이 E의 상위 타입.
	... // 스택의 원소들을 모두 pop해서 dst에 push
}
```

### 헷갈린다면 PECS(Producer-extends, consumer-super)를 기억하라

매개변수화 타입 T가 생산자라면 `<? extends T>` 를 사용하고, 소비자라면 `<? super T>` 를 사용하라.

위 스택의 예에서 pushAll의 src는 Stack이 사용하는 E 인스턴스를 생산하므로 `<? extends T>` 가 적절하다.

반면 popAll의 dst 매개변수는 Stack으로부터 E 인스턴스를 소비(pop)하므로 `<? super T>` 가 적당하다.

### 메서드 선언에 타입 매개변수가 한 번만 나오면 와일드카드로 대체하라

```java
public static <E> void swap(List<E> list, int i, int j);
public static void swap(List<?> list, int i, int j); // public api라면 이 방식이 낫다
```

## [32] 제네릭과 가변인수를 함께 쓸 때는 신중하라

가변인수는 메서드에 넘기는 인수의 개수를 클라이언트가 조절할 수 있게 해주는데, 구현 방식에 허점이 있다.

가변 인수 메서드를 호출하면 가변 인수를 담기 위한 만들어진 배열이 클라이언트에 노출되어있으며, 배열과 제네릭의 타입 규칙이 서로 다르기 때문이다.

메서드에 제네릭 매개변수를 사용하고자 한다면, 먼저 해당 메서드가 타입 안전한지(매개변수 배열에 아무것도 저장하지 않거나, 해당 배열을 다른 코드에 노출하지 않는 경우)확인한 다음 `@SafeVarargs` 애너테이션을 달아 사용에 불편함이 없게끔 하자.

```java
// 제네릭과 varargs를 혼용하면 타입 안정성이 깨진다
static void dangerous(List<String>... stringLists) {
	List<Integer> intList = List.of(42);
	Object[] objects = stringLists;
	objects[0] = intList;             // 힙 오염 발생
	String s = stringLists[0].get(0); // ClassCastException
}
```

## [33] 타입 안전 이종 컨테이너를 고려하라

Collection API로 대표되는 일반적인 제네릭 형태에서는 한 컨테이너가 다룰 수 있는 타입 매개변수가 고정되어있다. 하지만 컨테이너 자체가 아닌 키를 타입 매개변수로 바꾸면 이런 제약이 없는 컨테이너를 만들 수 있다. 이 컨테이너는 `Class` 를 키로 쓰며 이런 식으로 쓰이는 `Class` 객체를 타입 토큰이라 한다.

**타입 안전 이종 컨테이너 패턴**

컨테이너 대신 키를 매개변수화한 다음, 컨테이너에 값을 넣거나 뺄 때 매개변수화한 키를 함께 제공하는것.

```java
public class Favorites {
	private Map<Class<?>, Object> favorites = new HashMap<>();

	public <T> void putFavorite(Class<T> type, T instance) {
		favorites.put(Objects.requireNonNull(type), type.cast(instance));
	}
	public <T> T getFavorite(Class<T> type){
		return type.cast(favorites.get(type));
	}
}
```

해당 클래스의 제약은 **실체화 불가 타입**(`List<String>` 같은)에는 사용할 수 없다는 것이다. List<String>용 Class객체를 얻을 수 없기 때문이다 (`List<String>`, `List<Integer>` 둘 다 실제로는 `List.class`).

이 제약은 **슈퍼 타입 토큰**으로 해결할 수 있지만(Spring Framework에서는 `ParameterizedTypeReference` 클래스로 제공) 완벽하지 않으니 주의해야 한다.
