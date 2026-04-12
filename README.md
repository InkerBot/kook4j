# kook4j
Kook4j 是 Java 语言编写的 Kook API 封装库。

## 快速开始

### 安装

#### Gradle

```groovy
repositories {
    maven {
        url 'https://ghr.inkerbot.bot/kook4j'
    }
}

dependencies {
    implementation 'bot.inker.kook4j:kook4j:0.1.0'
}
```

#### Gradle Kotlin

```kotlin
repositories {
    maven('https://ghr.inkerbot.bot/kook4j')
}

dependencies {
    implementation("bot.inker.kook4j:kook4j:0.1.0")
}
```

#### Maven

```xml
<repositories>
    <repository>
        <id>inkerbot-ghr</id>
        <url>https://ghr.inkerbot.bot/kook4j</url>
    </repository>
</repositories>

<dependency>
    <groupId>bot.inker.kook4j</groupId>
    <artifactId>kook4j</artifactId>
    <version>0.1.0</version>
</dependency>
```

## License
Copyright (c) 2026 InkerBot. All rights reserved.