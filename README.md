# SmartShop Mobile

A modern Android application for browsing and purchasing products, built with the latest Android development technologies.

## Project Overview

SmartShop is a simple e-commerce mobile application that allows users to browse a variety of products, add them to a shopping cart, and complete purchases. The app connects to a backend service (in this case, a .NET project) via a RESTful API to fetch and manage data.

## Features

-   **Authentication:** Secure user sign-up and login.
-   **Product Browsing:**
    -   View a list of available products.
    -   Sort and filter products by price, category, brand, and more.
    -   View detailed product information and images.
-   **Shopping Cart:**
    -   Add products to the cart.
    -   Manage cart items (update quantity, remove items).
    -   View a dynamic cart total.
-   **Billing & Checkout:**
    -   Seamless payment integration with gateways like VNPay, ZaloPay, or PayPal.
    -   Enter and save billing and shipping information.
    -   Receive an order confirmation after a successful purchase.
-   **Notifications:** A cart badge on the app icon shows the number of items in the cart.
-   **Store Locator:** An integrated map to find the store's physical location and get directions.
-   **Live Chat:** Real-time chat with store representatives for support.

## Tech Stack

This project is built using a modern Android tech stack:

-   **Programming Language:** [Kotlin](https://kotlinlang.org/)
-   **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) for a declarative and modern UI.
-   **Architecture:** Follows modern Android architecture principles (e.g., MVVM).
-   **Dependency Injection:** [Hilt](https://dagger.dev/hilt/) for managing dependencies.
-   **Networking:** [Retrofit](https://square.github.io/retrofit/) and [OkHttp](https://square.github.io/okhttp/) for making API calls to the backend.
-   **Build Tool:** Gradle with Kotlin DSL and Version Catalogs.
-   **Annotation Processing:** [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html) for faster builds.

## Backend

The mobile app is designed to work with any RESTful API. The backend for this project is a separate .NET application.
