# Google Play Data Safety Responses

**App:** Let's Stretch (`letshangllc.stretchingroutines`)  
**Version:** 3.0.0 (versionCode 100)  
**Last Updated:** November 2026  
**Target SDK:** 36

This document helps fill out the Google Play Console Data Safety form. The previous listing was removed for a missing/invalid Data safety form while AdMob sent undisclosed data.

---

## Overview

Let's Stretch loads stretch routines and images from Firebase and shows AdMob banners. It does not require user accounts. Google User Messaging Platform (UMP) collects consent before personalized ads are requested.

---

## Data Collection Summary

| Data Type | Collected | Shared | Purpose |
|-----------|-----------|--------|---------|
| App interactions | Yes | Yes (AdMob) | Advertising, Analytics |
| Advertising ID / Device or other IDs | Yes | Yes (AdMob) | Advertising |
| Diagnostics / crash-adjacent device info | Possible (via Google SDKs) | Yes (Google) | Advertising, Analytics |
| Personal info (name, email, etc.) | No | No | — |
| Location | No (approximate may be inferred by ads SDK) | Possibly via AdMob | Advertising |
| Photos / media | No (local only if user picks a photo in unused create-routine UI) | No | — |

---

## App Permissions

| Permission | Why |
|------------|-----|
| `INTERNET` | Firebase content + AdMob over HTTPS |
| `ACCESS_NETWORK_STATE` | AdMob / network checks |
| `ACCESS_WIFI_STATE` | Legacy ads-related; kept for SDK compatibility |

**Play Console:** Declare these permissions and that the app uses ads. No `POST_NOTIFICATIONS`, no precise location, no SMS/call permissions.

---

## Third-Party SDKs

### Firebase Realtime Database & Storage
- **Data:** Stretch routine names, instructions, image download URLs
- **Purpose:** App content
- **Privacy:** https://firebase.google.com/support/privacy

### Firebase Auth (dependency present; no sign-in UI in current screens)
- **Data:** None unless auth APIs are used later
- **Privacy:** https://firebase.google.com/support/privacy

### Google Mobile Ads (AdMob)
- **Data:** Advertising ID, app interactions, device/network info
- **Purpose:** Banner ads (HTTPS only; Amazon Mobile Ads removed)
- **Shared:** Yes, with Google advertising partners
- **Control:** UMP consent (GDPR/EEA)
- **Privacy:** https://policies.google.com/privacy

### Google User Messaging Platform (UMP)
- **Purpose:** Consent before requesting ads
- **Privacy:** https://policies.google.com/privacy

---

## Data Security

- **In transit:** Yes — Firebase and AdMob use TLS/HTTPS
- **Deletion:** No user accounts. Contact the Play listing developer email for deletion requests related to ads/device identifiers (users can also reset advertising ID in device settings)

---

## Children's Privacy

Not directed at children under 13 (COPPA) or under 16 (GDPR).  
`setTagForUnderAgeOfConsent(false)` is set in UMP. Do **not** enroll in Designed for Families unless ads/policy are reviewed separately.

---

## Play Console Form — Suggested Answers

1. **Does your app collect or share any of the required user data types?** Yes  
2. **Is all user data collected by your app encrypted in transit?** Yes  
3. **Do you provide a way for users to request that their data is deleted?** Yes (via developer contact / advertising ID reset)  
4. **Declare Device or other IDs** — Collected: Yes; Shared: Yes (AdMob); Purpose: Advertising  
5. **Declare App interactions** — Collected: Yes; Shared: Yes (AdMob); Purpose: Advertising  
6. **Ads:** App contains ads — Yes  

---

## AdMob IDs (production)

| Resource | AdMob name | Value |
|----------|------------|-------|
| `admob_app_id` | Let's Stretch (Android) | `ca-app-pub-8223005482588566~7547587931` |
| `admob_id_routines` | Banner ad Home | `ca-app-pub-8223005482588566/9024321135` |
| `admob_id_stretches` | Banner Ad Stretches | `ca-app-pub-8223005482588566/1501054335` |

Debug builds continue to use Google's official test IDs.

## Owner Follow-ups

1. Complete Data safety in Play Console using this doc  
2. Confirm AdMob app Data safety / privacy disclosures match UMP + AdMob collection  
3. Review whether Firebase Auth dependency can stay unused or should be removed later  
