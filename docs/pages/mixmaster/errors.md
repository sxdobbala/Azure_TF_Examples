---
layout: main
title: Mix-Master Errors
permalink: /mm/errors
---

## Error Listing

| Endpoint | Error Body | Error Code | Notes |
|:---------|:-----------|:----------:|:------|
| /orchestrate | WrappedManifestResponse object with message field: "varies based on validation failure" | 400 | One of your fields was not valid (see Full Manifest for validation rules) |
|| Full authentication is required to access this resource | 401 | You didn't pass in correct Authentication to Mix-Master or you didn't pass in any Authentication to Mix-Master |
|| WrappedManifestResponse object with message field: "There is an issue authenticating to the downstream automation target" | 401 or 403 | Whatever you passed in as Authentication is not working downstream |
|| WrappedManifestResponse object with message field: "Feature type not found" | 500 | There was a feature type not found, if you passed in a full Manifest then one of the features is not correct. If you passed in a BomLite then there is an internal issue with what the Codegen service has given back to Mix-Master |
|| WrappedManifestResponse object with message field: "There was a downstream error when calling the " + OPERATION_NAME +" operation for the " + FEATURE +" feature. Downstream return code: " + downstream.value() | 503 | Any downstream failure (which means either DevOps Toolchain failed or one of the Automation Targets failed) will return this error. |

### WrappedManifestResponse

Some of the above errors may result in the following WrappedManifestResponse being displayed, it containing the full manifest being generated, along with the error message and code.

```{
  "manifest": {
    "version": "v1",
    "application": { ... },
    "billingInformation": { ... },
    "features": [ ... ]
  },
  "message": "exception message set in Mix-Master",
  "code": 500
}
```
