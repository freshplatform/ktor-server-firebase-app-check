# Firebase App Check for Ktor server

A Ktor server plugin for configuring [Firebase App Check](https://firebase.google.com/products/app-check) easily and with simplicity.
It is **not affiliated** with Firebase or Google and may not be suitable for production use **yet**.

[//]: # (Note: this repository name might be changed to [ktor-server-guardian]&#40;https://github.com/freshplatform/ktor-server-guardian&#41;)

feel free to share your opinion in the discussions

[//]: # ([![Build Status]&#40;https://travis-ci.org/freshplatform/ktor-server-firebase-app-check.svg?branch=main&#41;]&#40;https://travis-ci.org/freshplatform/ktor-server-firebase-app-check&#41;)
[![](https://jitpack.io/v/freshplatform/ktor-server-firebase-app-check.svg)](https://jitpack.io/#freshplatform/ktor-server-firebase-app-check)
[![CI](https://github.com/freshplatform/ktor-server-firebase-app-check/actions/workflows/main.yml/badge.svg)](https://github.com/freshplatform/ktor-server-firebase-app-check/actions/workflows/main.yml)

## Table of Contents

- [About](#about)
- [Installation](#installation)
- [Usage](#usage)
- [Status](#status)
- [Features](#features)
- [Contributing](#contributing)
- [Acknowledgments](#acknowledgments)

## About

Protection for your app’s data and users
App Check is an additional layer of security that helps protect
access to your services by attesting that incoming traffic is
coming from your app, and blocking traffic that 
doesn't have valid credentials. It helps protect your backend from abuse,
such as billing fraud, phishing, app impersonation, and data poisoning.

Broad platform support that you can tailor to your needs
App Check supports Android, iOS, and Web out of the box. For customers who want to support more platforms, 
you can integrate your own attestation provider with App Check's custom capabilities.

Firebase support integrates with a custom backend, the supported SDKs are Node.js, Python, and Go

This is a ktor plugin that saves you some time to integrate it with your ktor backend

[![Firebase app check video](https://i.imgur.com/asvY9tu.png)](https://youtu.be/LFz8qdF7xg4?si=V8SJRrkrHdCDZBKU)

## Installation

Use this section to describe how to install your project. For example:

1. [Create a new ktor project](https://start.ktor.io/) or use existing one if you already have.
2. Add jitpack repository to your `build.gradle.kts`:
   ```groovy
   repositories {
    mavenCentral()
    maven {
        name = "jitpack"
        setUrl("https://jitpack.io")
    }
    }
   ```
3. Add the dependency:
    ```groovy
    dependencies {
        implementation("com.github.freshtechtips:ktor-server-firebase-app-check:<latest-version-here>") // use the latest version above
    }
    
    ```
4. Configure and install the plugin in the application module, 
Pass the following environment variables,
   go to your Firebase project settings, in the general tab

   * `FIREBASE_PROJECT_NUMBER` from the Project ID
   * `FIREBASE_PROJECT_ID` from the Project number

```kotlin
    install(FirebaseAppCheckPlugin) {
    firebaseProjectNumber = System.getenv("FIREBASE_PROJECT_NUMBER")
    firebaseProjectId = System.getenv("FIREBASE_PROJECT_ID")
    isShouldVerifyToken = true
    secureStrategy = FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes
    pluginMessagesBuilder = { configuration ->
        // Example of override a response message
        FirebaseAppCheckMessages(
            configuration,
            appCheckIsNotDefinedResponse = mapOf(
                "error" to "${configuration.firebaseAppCheckHeaderName} is required"
            ),
        )
    }
}
```

By default, the plugin runs the app check only when the development is false.
You can override this bypass `isShouldVerifyToken = true` in the configuration

## Usage

You might want to read the [Firebase App Check documentation](https://firebase.google.com/docs/app-check)

Here's how to use the library:

* First make sure to use the desired secure strategy in the plugin configuration when you install it, if you want to secure the whole api and the app,
or just specific routes by surrounding them with `protectedRouteWithAppCheck {  }`


* Secure your routes (optional) if you are 

`secureStrategy = FirebaseAppCheckSecureStrategy.ProtectSpecificRoutes,`
```kotlin
routing {
        get("/") {
            call.respondText("Hello World! this route is not using app firebase app check")
        }
        protectRouteWithAppCheck {
            route("/products") {
                get("/1") {
                    call.respondText { "Product 1, Firebase app check" }
                }
                get("/2") {
                    call.respondText { "Product 2, Firebase app check" }
                }
            }
        }
        get("/test") {
            call.respondText { "Tis get test doesn't use firebase app check!" }
        }
        protectRouteWithAppCheck {
            post("/test") {
                call.respondText { "Tis post test is protected!" }
            }
        }
    }
```
* Send the request and set the header `X-Firebase-AppCheck` with the app check token (jwt).
  you can get the token from the Firebase app by checking SDK in the client apps (Android, iOS, macOS, Web)
  Or if you just want to test real quick [try this expired token](https://pastebin.com/za2wW8cP).
  (please notice to succeed in the test, you must generate the app token from the client app that uses the same project)

Token for testing purposes:
```
eyJraWQiOiJ2Yy1sVEEiLCJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxOjgwMjA4OTE0MjU1OTphbmRyb2lkOjI2ZDhjMDA3ZGVkMDNmODQyYTg4MmEiLCJhdWQiOlsicHJvamVjdHNcLzgwMjA4OTE0MjU1OSIsInByb2plY3RzXC9teW5vdGVzLWViNzE3Il0sInByb3ZpZGVyIjoiZGVidWciLCJpc3MiOiJodHRwczpcL1wvZmlyZWJhc2VhcHBjaGVjay5nb29nbGVhcGlzLmNvbVwvODAyMDg5MTQyNTU5IiwiZXhwIjoxNjk3MTM0NDg3LCJpYXQiOjE2OTcxMzA4ODcsImp0aSI6InZLZERfNTRhQ2tzVmpHV0xBN3d1TjZmWlFUQWRYZzRBWGJhYVBzRUZDV0EifQ.H_LGsCe5I-Z2uAgYU1isDmxQ-6PecdmjEqvkrZp9AWthNhsiMdlVYjUe2DaSmt3lhIlwCJyCh2YooOLvSlFAvdx5n__kB5O5C9Fw-Vw-zjSTOAi6lNB0hi8OEkIJhNgw2b_UipeVFd1I6ICkCdV93Ewr-clv-eDeMIg_b8vr3w6HtypZDVu3hAl6BjfxY9r7cm5eBmHGnOxwb1-flSKRJdBmrh4Bm0_imaDPSHw_rUwCUXHOAM-QfdQ-D4C15L_IJH4X6kT7nm8GMj47rQjr1d6CQZbW3xoIsTJvnpreOR1xyiHZiLydj1cwPt6r2DfmjRL6-tFs2u8c72CcoqQ4hhsJE9ZSk1BHXpnGw6t5PLPWmk-K7wCrn49U20SYsbOGzyMmwPs-nRyYL3QeV00brlaQWFN7pnjquYHtgJZgkVZlIe1Hh_8mBzTSLygc3-0Xw3FKf1X6p_jOyyN7Qi3Wf5GHvBdp_sYyuBtXMYVwhKQ56lYBX3waLP0KHSiDiDUW
```

## Status

Is this library ready for production use?? 
The short answer is yes, the long one is no, because we do have
plans to change too many things that are not related to how the logic
of Firebase App Check works, the logic will likely still be the same

It will only update when the official one updates.
What could be changes then?
only cosmetic changes to change the way to use the library,
for example, we might change the library name to `KotlinGuardian` or `KtorGuardian`
and add support for different providers to use instead of firebase alone, add more features
and things, so we expect to do too many breaking changes but other than that

The library should work just fine as long it follows the official docs of firebase

## Features
List the key features of the library

```markdown
## Features

— Easy to use and customizable
- Different secure strategies
- Caching and rate limiting for the public key of the Firebase app check
- Handle different errors
```

## Contributing

We welcome contributions!

Please follow these guidelines when contributing to this project.
See [CONTRIBUTING.md](./CONTRIBUTING.md) for more details.

## Acknowledgments

- Thanks to [Firebase](https://firebase.google.com/)
for updating the [documentation](https://firebase.google.com/docs/app-check/custom-resource-backend#other)
  and showing us how to contact their APIs
- Thanks to JetBrains for Kotlin, IntelliJ IDEA Community Edition, and Ktor for server
- Thanks to the open-source community
- Thanks for [Auth0](https://developer.auth0.com/) and [Jwt.io](https://jwt.io/) for the JWT libraries
and the debugger
