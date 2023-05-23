# Uber_Clone

This is an Uber clone app that provides extra security by using email and password for sign-in, as opposed to Uber's phone number verification. 
The app utilizes Firebase Realtime Database and Authentication for data storage and user management. 
It automatically captures the user's location upon sign-in and allows them to choose their destination by entering it into an auto-suggestion TextView. 
The app then displays the route and prices for the selected destination. Users can also choose their desired type of car and make payments using the PayFast API.

## Getting Started

To get started with the Uber clone app, follow the instructions below:

### Prerequisites

- Android Studio
- Firebase Account
- PayFast API credentials
- Google Maps API Key

### Installation

1. Clone the repository to your local machine.

2. Open the project in Android Studio.

3. Configure Firebase:
   - Create a Firebase project in the Firebase Console.
   - Enable Firebase Authentication and Realtime Database for your project.
   - Download the `google-services.json` file and place it in the app module directory.

4. Configure PayFast API:
   - Obtain your PayFast API credentials (merchant key and merchant ID).
   - Replace `[yourmerchantkey]` and `[yourmerchantid]` with your own credentials in the `PaymentActivity` class.

5. Configure Google Maps API:
   - Obtain a Google Maps API key.
   - Replace `[YourApiKey]` with your API key in the `RouteActivity` class (line 382), `PlaceAPI` class (Models package, line 25), and the `AndroidManifest.xml` file (line 41).

6. Build and run the app on an Android device or emulator.

## Usage

1. Launch the app and sign in using your email and password.

2. Grant location permission when prompted to allow the app to capture your location.

3. Enter your destination into the auto-suggestion TextView.

4. The app will display the route and prices for the selected destination.

5. Choose your desired type of car.

6. Proceed to payment using the PayFast payment gateway.
