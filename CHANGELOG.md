# Changelog
All notable changes to this project will be documented in this file.

All releases before 1.0.0 may contain breaking changes.
After 1.0.0 this project will adhere to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## Versions
[0.10.17 - 2020-02-11](#01017---2020-02-11)

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
- Create a dedicated Android TV SDK module.
### Changed
- Improve ZenKey Application validation layer using kotlin language features.
- Split SDK in multiple modules.
### Removed
### Deprecated
### Fixed
- Force Android Security Provider Update.
### Security

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