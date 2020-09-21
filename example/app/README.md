![ZenKey](../../image/ZenKey_rgb.png "ZenKey")

# ZenKey SDK Example Application

This ZenKey SDK Example app uses a simple programmatic UI to demonstrate how to integrate the ZenKey SDK for sign-in. Because of the secure nature of the ZenKey service, you will need to create real project credentials and use those to build the app and test with a real phone.

This basic integration of the ZenKey SDK matches the steps described in the [Android Quick Start Guide](https://developer.myzenkey.com/android-quickstart/).

## Set up

There are three steps to building and running the ZenKey SDK Example app to test a complete auth flow:

1. Configure a `client_id`.
2. Create a sample backend server.
3. Set the location of your sample backend server.

## 1. Configure a `client_id`

Create an account and project in the [ZenKey Developer Portal](https://portal.myzenkey.com).

When creating a project, the portal provides a default `redirect-uri`. To simplify ZenKey integration, keep this default value; the SDK uses the default for quick-start integration. You can always add or edit the URI at a later time. See the [Android Integration Guide](https://developer.myzenkey.com/android/) for details about using a custom `redirect-uri`.

Once your project receives approval, copy your `client_id` and secret from the ZenKey Developer Portal dashboard. You can start using the `client_id` after it is provisioned by the carriers. Using a non-provisioned `client_id` will cause errors in API responses.

Next, add your `client_id` to the ZenKey SDK Example app:
1. Open [build.gradle](/build.gradle).
2. In the Android defaultConfig section, replace the `manifestPlaceholders` value for the `zenKeyClientId` key (`clientId`) with your `client_id`.
   
## 2. Create a sample backend server

The ZenKey SDK Example app can only start the ZenKey authorization flow. For security, the final token request must be made from a secure server. To test the complete authorization flow, use the provided Python sample code to set up a server instance. In the instance, you set the ZenKey secret you got from the portal. The server sample code and instructions are here:
[API Backend Sample Repo](https://github.com/MyZenKey/sp-sdk-provider-integration-web/Examples/APIBackend).

Note: Never store the ZenKey secret in a public binary.

## 3. Set the location of the sample backend server

The ZenKey SDK Example app needs to know how to call the APIs in the server sample code. 
1. Open [build.gradle](/build.gradle), 
2. In the Android defaultConfig section, replace the `BuildConfigField` value for the `SAMPLE_API_ENDPOINT` key (`baseUrl`) with the location of the sample backend server instance you created.

## Run the example app

You can run the ZenKey SDK Example app on a real phone with a real SIM card to authorize a sign-in request; or you can use an Android emulator to test a secondary-device flow, where you pair the device with a primary phone.

To test a carrier authorization request:
1. Launch the ZenKey SDK Example app and tap "Sign in with ZenKey".
2. The SDK Determines if it is running on a primary or secondary device and helps you install the ZenKey app for your carrier, if needed.
3. The SDK launches the ZenKey app which asks you to authorize your app.
4. The ZenKey app redirects you back to the ZenKey SDK Example app with an `AuthorizationResponse` from the user's carrier.
5. The ZenKey SDK Example app uses the `AuthorizationResponse` to request a sign-in from the sample server.
6. The sample server makes the token request and userInfo request to complete sign-in.

## Send us your feedback

Please report bugs or issues to our [support team](mailto:techsupport@myzenkey.com).

## View SDK version and history information

View history of SDK versions and changes in the [Changelog](../../CHANGELOG.md).

## License

NOTICE: © 2019-2020 ZENKEY, LLC. ZENKEY IS A TRADEMARK OF ZENKEY, LLC. ALL RIGHTS RESERVED. THE INFORMATION CONTAINED HEREIN IS NOT AN OFFER, COMMITMENT, REPRESENTATION OR WARRANTY AND IS SUBJECT TO CHANGE.
