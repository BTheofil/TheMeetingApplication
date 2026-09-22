# Privacy Policy

**App:** Meeting
**Developer:** Theofil Bodnar
**Last updated:** 2026.09.22

## Short version

Meeting needs an account so a coach and a client can share a schedule, so some data does leave your device. An account is a username and a password — nothing else. Your coach's open hours, the sessions you book, and which coaches you are connected to are stored on the developer's server so both sides see the same thing.

Meeting never asks for your email address, phone number, real name, or location. There is no analytics, no advertising, and no tracking of any kind.

## What the app collects

**Your account.** When you register you choose a username and a password, and whether you are a coach or a client. The username can be anything you like — it does not have to identify you. Your password is sent to the server only to create or verify your account; the server stores it as a **bcrypt hash**, never as readable text.

**Your scheduling data.** Depending on how you use the app, the server stores:

- if you are a coach: the dates and start/end times of the hours you publish
- if you are a client: which sessions you have booked
- the connections between coaches and clients, and whether a request is pending or accepted

**A notification ID.** So the app can send you notifications, it registers an identifier for your installation (a Firebase installation ID) with the server, together with your account. See *Notifications* below.

## What the app does not collect

- no email address, phone number, real name, or date of birth
- no location, contacts, photos, or files
- no advertising ID or other tracking identifier
- no analytics and no crash reporting — the app contains no SDK for either
- no advertising of any kind

The app requests only two Android permissions: internet access, and permission to show notifications.

## Where your data is stored

Account and scheduling data is stored on a server operated by the developer, and is sent over an encrypted HTTPS connection. The server keeps ordinary request logs, as any web server does.

On your device, the app stores your sign-in details — your username, your password, your account type, and your current session token — so you stay signed in between launches. This is held in app-private storage that is encrypted with a key kept in the Android Keystore, so it is not readable by other apps. It is erased when you log out or delete your profile.

## Notifications

Meeting uses **Google Firebase Cloud Messaging** to deliver notifications, such as telling a coach that someone has asked to connect with them.

For this to work, the app registers an identifier for your app installation (a Firebase installation ID) with the developer's server, linked to your account. It is used only to route notifications to the right device. Google receives this identifier and standard device and app information in order to deliver the message.

The identifier is removed when you log out or delete your profile, and you can turn notifications off at any time in your device's system settings.

- Google's privacy policy: https://policies.google.com/privacy

## Tips and purchases

If you choose to support the developer through an in-app tip, the purchase is processed by **Google Play Billing**, and purchase status is managed using **RevenueCat**, a third-party service that verifies and records purchases.

For that purchase, RevenueCat receives:

- **your username**, which the app uses as the account identifier it sends to RevenueCat, so a purchase stays with your account across reinstalls and devices
- the purchase receipt and transaction details from Google Play
- basic device and store information, such as device model, operating system version, and country

The developer never receives or has access to your payment card details, billing address, or Google account. Payment information is handled entirely by Google Play.

- RevenueCat's privacy policy: https://www.revenuecat.com/privacy
- Google Play's privacy policy: https://policies.google.com/privacy

If you never make a purchase, none of the above applies.

## Why this data is processed

- **your account** — to sign you in and to keep your data separate from other people's
- **scheduling data** — to show a coach's open hours, to let clients book them, and to keep both sides in sync
- **the notification ID** — to deliver notifications to your device
- **purchase data** — to verify your purchase, keep it working across reinstalls and devices, and meet legal and tax obligations for the transaction

None of it is used for advertising or profiling, and none of it is sold or shared with anyone else. The third parties named in this policy act only as service providers for the purposes described above.

## How long it is kept

Your account and scheduling data is kept until you delete your profile. Session tokens expire automatically five days after they are issued. Your notification ID is removed when you log out or delete your profile.

Purchase records are retained by RevenueCat and Google Play for as long as needed to support the purchase and to satisfy legal and accounting requirements.

## Deleting your data

You can delete your account from inside the app: open **Profile** and choose **Delete profile**. This deletes your account, the hours you published, your bookings, and your connections to other users, and clears the data stored on your device. Your notification ID is removed when you log out or delete your profile.

If you would rather have it done for you, or something went wrong, contact the developer at the address below.

## Children

The app is not directed at children under 13, and no personal data is knowingly collected from them.

## Your rights

If you are in the EU, UK, or another region with similar laws, you have the right to request access to, correction of, or deletion of data relating to you, and to object to its processing. Please contact the developer using the details below, and include the username of the account concerned. For a question about a purchase, please also include your Google Play order number so the record can be located.

## Changes

This policy may be updated if the app changes. The updated version will be posted at this address with a new "last updated" date.

## Contact

Questions about this policy: **btheofil7@gmail.com**
