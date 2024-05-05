IDEA Plugin for generating boilerplate test code with usage of [Mockk](https://github.com/mockk/mockk) library.
---

Usage of this plugin will take next file as input:

```kotlin
package com.test.project

class Foo(
    val t1: Any,
    val t2: (Any) -> Unit,
    val t3: kotlin.Lazy<Any>,
    val t4: dagger.Lazy<Any>,
)
```

And generate test class with some boilerplate code as output:

```kotlin
package com.test.project

import io.mockk.impl.annotations.MockK

class FooTest {

    @MockK
    lateinit var t1: Any

    @MockK
    lateinit var t2: (Any) -> Unit

    @MockK
    lateinit var t3: Any

    @MockK
    lateinit var t4: Any

    private fun prepareFoo(
        t1: Any = this.t1,
        t2: (Any) -> Unit = this.t2,
        t3: Any = this.t3,
        t4: Any = this.t4,
    ): Foo {
        return Foo(
            t1 = t1,
            t2 = t2,
            t3 = lazyOf(t3),
            t4 = { t4 },
        )
    }

}

```

### Usage

Plugin is a part of Generate group - open it with next shortcuts and select **Generate MockK Test**:

MacOS: <kbd>Cmd</kbd> + <kbd>N</kbd>

Windows: <kbd>Alt</kbd> + <kbd>Insert</kbd>

![mockk-generate.png](images/mockk-generate.png)


