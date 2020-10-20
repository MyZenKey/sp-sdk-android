# Changelog
All notable changes to this project will be documented in this file.

All releases before 1.0.0 may contain breaking changes.
After 1.0.0 this project will adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Versions
[1.1.2 - 2020-09-28](#112---2020-09-28)

## Updating
When the Unreleased section becomes a new version, duplicate the Template to create a new Unreleased section.

## [Template]
### Added
### Changed
### Removed
### Deprecated
### Fixed
### Security

## [Unreleased]
### Added
### Changed
### Removed
### Deprecated
### Fixed
### Security

## [1.1.2] - 2020-09-28
### Fixed
- Fixed crash on startup when a browser has no versionName
- Bypass checking visibility of ZenKey app of SDK 30

## [1.1.1] - 2020-09-23
### Changed
- Force Android Security Provider Update.
### Fixed
- Lack of browser visibility on Android 11

## [1.1.0] - 2020-09-21
### Changed
- Migrate support library to androidx
- Update compileSdk/targetSdk version to 30

## [1.0.0] - 2020-09-21
### Added
- Example for user carrier migration
### Changed
- Update support library to 28.0.0
- Update README.md with minor edits
- Update the name of the Developer Portal throughout the docs

## [0.14.2] - 2020-07-10
### Changed
- Update licence declaration.

## [0.14.1] - 2020-07-10
### Changed
- Change CONTRIBUTING.md

## [0.14.0] - 2020-06-23
### Added
- Implement UserInfo v2 in sample app.

## [0.13.0] - 2020-06-23
### Changed
- Rename Example app folder
- Improve Example App Readme
- Improve Example App comments

## [0.12.3] - 2020-06-23
### Changed
- Map TimeoutException/SocketTimeoutException to SERVER_ERROR instead of NETWORK_FAILURE 

## [0.12.2] - 2020-05-05
### Added
- Add Internal Deployment of Example App.

## [0.12.1] - 2020-04-20
### Changed
- Allow Nullable Theme

## [0.12.0] - 2020-04-14
### Added
- Add Example Application

## [0.11.19] - 2020-04-14
### Added
- Add new scopes.

## [0.11.18] - 2020-03-25
### Added
- Add Logged-In screen.

## [0.11.17] - 2020-03-25
### Fixed
- Fix optional branding in OpenId configuration.

## [0.11.16] - 2020-03-23
### Added
- Add Rest API clients.

## [0.11.15] - 2020-03-23
### Added
- Add missing error description, for non standard errors.

## [0.11.14] - 2020-03-18
### Added
- Add clientId used for request in AuthorizationResponse.

## [0.11.13] - 2020-03-18
### Changed
- Improve drawable nullability management.

## [0.11.12] - 2020-03-18
### Changed
- Make feature branch prefix bump version minor digit.

## [0.11.11] - 2020-03-18
### Added
- Add ability for SP theme override.

## [0.11.10] - 2020-03-13
### Fixed
- Fixed usage authorization Uri with empty parameters in versions [0.11.7, 0.11.8 , 0.11.9] .

## [0.11.9] - 2020-03-10
### Added
- Add Loading State for sample Application.

## [0.11.8] - 2020-03-09
### Added
- Add Error messages for Authorization response in Sample Application.

## [0.11.7] - 2020-03-09
## Changed
- Improve package model.

## [0.11.6] - 2020-03-09
## Changed
- Improve metadata parsing.

## [0.11.5] - 2020-03-09
## Fixed
- Return INVALID_REQUEST when receiving INVALID_REQUEST from discovery instead of INVALID_CONFIGURATION error.

## [0.11.4] - 2020-03-09
### Changed
- Clean-Up/Add Unit Tests

## [0.11.3] - 2020-03-06
### Added
- Add Login screen for Sample Application.

## [0.11.2] - 2020-03-05
### Changed
- Allow ZenKeyButton to have click listener.
- Use Koltin @JvmOverloads to merge multiple constructors in one.

## [0.11.1] - 2020-03-04
### Changed
- Improve Usage of MessageDigest
- Add Unit tests for CodeVerifier/PKCE creation.

## [0.11.0] - 2020-03-02
### Added
- Add sample application.

## [0.10.18] - 2020-03-02
### Added
- Add contribution guide.

## [0.10.17] - 2020-02-11
### Changed
- Change versioning to allow major bump.

## [0.10.16] - 2020-02-11
### Changed
- Expose Nonce, ACR, Context and CorrelationId in authorization response.

## [0.10.15] - 2020-02-06
### Changed
- Improve usage of TelephonyManager using kotlin language features.

## [0.10.14] - 2020-02-06
### Changed
- Expose Interface instead of implementation. Improve internal code base.

## [0.10.13] - 2020-02-03
### Fixed
- Fix race condition on Android 9.

## [0.10.12] - 2020-01-28
### Added
- Catch SSLException and return meaningful error message.

## [0.10.11] - 2020-01-27
### Fixed
- Remove PKCE plain Base64 encoding.

## [0.10.10] - 2020-01-23
### Fixed
- Fix PKCE code_challenge double hashing.

## [0.10.9] - 2020-01-23
### Changed
- Improve internal code base (native intent creation).

## [0.10.8] - 2020-01-23
### Fixed
- Fix crash when no Browser available on the device.

## [0.10.7] - 2020-01-23
### Changed
- Update Changelog missing entries.

## [0.10.6] - 2020-01-22
### Removed
- Disable Carrier endorsement view until full feature is complete.

## [0.10.5] - 2020-01-22
### Removed
- Remove internal usage of transitive dependency from customTabs dependency 
(annotations support library).

## [0.10.4] - 2019-12-31
### Changed
- Update Copyright.

## [0.10.3] - 2019-12-10
### Changed
- Change ZenKeyButton parameters names.

## [0.10.2] - 2019-11-21
### Changed
- Remove Base64 encoding for context parameter.

## [0.10.1] - 2019-11-21
### Added
- Add Changelog.

## [0.10.0] - 2019-11-20
### Added
- Add carrier endorsement view and stylable option to show it into the ZenKeyButton.

## [0.9.0] - 2019-11-07
### Added
- Remove gradle plugin used to deploy internal demo application. 

## [0.8.9] - 2019-11-04
### Added
- PKCE: auto-generated codeChallenge and method included in auth code request.
- PKCE: codeVerifier returned in authenticatedResponse for use in token requests.

## [0.8.8] - 2019-11-01
### Changed
- Include the error name in the authorization error string representation.

## [0.8.7] - 2019-11-01
### Changed
- Improve authorization error string representation.

## [0.8.6] - 2019-11-01
### Fixed
- Fix a potential crash when the context of the ZenKeyButton is not an Activity but a ContextWrapper.

## [0.8.5] - 2019-10-29
### Changed
- Use Base54 encoding for the state parameter when specified by the developer (Default state parameter was already Base64 encoded).

## [0.8.4] - 2019-10-29
### Changed
- ACR values passed to API are now a1, a2, a3 instead of aal1, aal2, aal3. API and ZenKey app update required.

## [0.8.3] - 2019-10-29
### Removed
- [breaking] Depreciated scopes: authorize, register, secondFactor, authenticate.

## [0.8.2] - 2019-10-29
### Added
- Expose SDK version parameter in authorization response. Send SDK version parameter in OIDC discovery network call.

## [0.8.1] - 2019-10-29
### Added
- Base64 encoding for the context parameter.

## [0.8.0] - 2019-10-24
### Fixed
- Clean-Up SDK dependencies.

## [0.7.9] - 2019-10-21
### Added
- First public release