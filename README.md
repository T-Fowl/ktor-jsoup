# Ktor: Jsoup

![Build](https://github.com/T-Fowl/ktor-jsoup/workflows/Build/badge.svg)
![Maven Central](https://img.shields.io/maven-central/v/com.tfowl.ktor/ktor-jsoup)
![GitHub](https://img.shields.io/github/license/T-Fowl/ktor-jsoup)

The Jsoup feature allows you to handle xml and html content in your ktor application easily using the [Jsoup](https://jsoup.org/) library.

This feature provides a [HttpClientFeature](https://ktor.io/clients/http-client/features.html) similar to that of the built-in [JsonFeature](https://ktor.io/clients/http-client/features/json-feature.html).

## Usage

Install the feature in your `HttpClient`

```kotlin
install(JsoupFeature)
```

You can optionally configure more content types for Jsoup to parse

```kotlin
install(JsoupFeature) {
    parsers[ContentType.Application.Rss] = Parser.xmlParser()
}
``` 

Once the Jsoup feature is installed you use it like you would the Json feature, by using `call.receive<Document>()` (and variants). 

```kotlin
HttpClient(engine) {
    install(JsoupFeature) {
        parsers[ContentType.Application.Rss] = Parser.xmlParser()
    }
}.use { client ->
    val feed = client.get<Document>(url)
}
```

## Download

Add a gradle dependency to your project:

Groovy
```groovy
repositories {
    mavenCentral()
}
implementation "com.tfowl.ktor:ktor-jsoup:$ktorJsoupVersion"
```

Kotlin DSL
```kotlin
repositories {
    mavenCentral()
}
implementation("com.tfowl.ktor:ktor-jsoup:$ktorJsoupVersion")
```

Add a maven dependency to your project:
```xml
<dependency>
  <groupId>com.tfowl.ktor</groupId>
  <artifactId>ktor-jsoup</artifactId>
  <version>${ktorJsoupVersion}</version>
</dependency>
```

## License

```
MIT License

Copyright (c) 2020 Thomas Fowler

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
