# QRVault – Smart Contact QR Sharing

> **Scan. Save. Share.**  
> A modern, secure, native Android application built with Kotlin and XML for offline vCard contact QR code generation, camera scanning, gallery saving, and SQLite history tracking.

---

## 📌 Project Details

- **Student Name:** Spencer Aaron Fernandes
- **USN:** 25MCAR0123
- **Course:** MCA – General
- **Project Title:** QRVault – Contact Sharing with QR Code Generator Application
- **Target Platform:** Native Android (Min SDK 24 / Target SDK 34+)
- **Architecture Pattern:** Clean separation (UI, Data, Storage, Utilities)

---

## 🌟 Features Overview

### 1. 📱 Home Dashboard
- **Quick Action Cards**: Instant access to **Contacts**, **Generate QR**, **Scan QR**, and **QR History**.
- **Live Statistics**: Real-time counters showing *Total Generated QRs*, *Saved QRs*, and *Most Recent QR Contact*.
- **Bottom Navigation**: Smooth switching between Home, Contacts, Scan, History, and Settings.

### 2. 📇 Device Contacts Integration
- **Runtime Permissions**: Securely requests `READ_CONTACTS` at runtime.
- **ContentResolver Querying**: Retrieves names, phone numbers, and email addresses directly from `ContactsContract`.
- **Real-Time Search**: Instant filtering of contacts by name, phone number, or email.
- **Contact Avatars**: Auto-generated initial avatars with vibrant theme colors.

### 3. 🏁 vCard 3.0 QR Code Generator
- **Standard vCard Payload**: Generates compliant vCard 3.0 strings containing Name (`FN`), Phone (`TEL`), and Email (`EMAIL`).
- **Strict Black & White Design**: Guaranteed pure black (`#000000`) modules on pure white (`#FFFFFF`) background for 100% reliable scanner compatibility.
- **High-Resolution Bitmaps**: Renders crisp QR code bitmaps centered inside pure white cards with subtle pink border accents.

### 4. 📷 Real-Time Camera Scanner
- **ZXing Embedded Scanner**: Instant camera QR detection using `DecoratedBarcodeView`.
- **Torch Control**: Dedicated Flashlight toggle button for low-light scanning.
- **Decoded Result View**: Displays parsed vCard contact details and raw payload data.
- **Add to Contacts**: Integrates with Android's system contacts via `ContactsContract.Intents.Insert`.

### 5. 💾 Gallery Saving & Intent Sharing
- **Gallery Storage**: Saves generated QR code PNG images directly to the device gallery (`Pictures/QRVault`) using modern `MediaStore` APIs.
- **Secure File Sharing**: Uses `FileProvider` (`androidx.core.content.FileProvider`) and `Intent.ACTION_SEND` to share QR code images securely across WhatsApp, Gmail, Bluetooth, and social apps.

### 6. 🗄️ Local SQLite QR History
- **Database Helper**: Managed via `SQLiteOpenHelper` (`qrvault.db`).
- **Persistent Storage**: Stores contact name, phone, email, vCard QR payload, and creation timestamp.
- **History Operations**: Re-view, re-share, delete single items, or clear all history records with confirmation dialogs.

### 7. ⚙️ Settings & Privacy
- **App Information**: Displays version details (`v1.0.0`) and about info.
- **Privacy Assurance**: 100% offline local processing—no external cloud servers or tracking.
- **Data Management**: Option to clear all QR history with safety confirmation dialog.

---

## 🎨 UI/UX Design System (White & Pink Theme)

The application interface follows a modern, clean **White and Pink** aesthetic:

| Color Role | Color Name | Hex Code |
|---|---|---|
| **Primary Pink** | `primary_pink` | `#E91E63` |
| **Dark Pink** | `dark_pink` | `#C2185B` |
| **Light Pink** | `light_pink` | `#FCE4EC` |
| **Background Pink** | `background_pink` | `#FFF8FA` |
| **Card Background** | `card_background` | `#FFFFFF` |
| **Primary Text** | `primary_text` | `#25212A` |
| **Secondary Text** | `secondary_text` | `#77717A` |
| **Border** | `border_pink` | `#F1D5E0` |
| **Success** | `success_green` | `#2E7D32` |
| **Error** | `error_red` | `#D32F2F` |

> **Note on QR Codes:** While the app interface follows the White and Pink theme, all generated QR code bitmaps strictly adhere to **Pure Black (`#000000`) on Pure White (`#FFFFFF`)** to ensure universal scanner compatibility.

---

## 🛠️ Project Structure

```text
com.example.qrvault/
│
├── activities/
│   ├── SplashActivity.kt           # Splash screen with logo & entry animation
│   ├── MainActivity.kt             # Main dashboard container & bottom navigation
│   ├── ContactDetailsActivity.kt   # Displays contact details & QR generate action
│   ├── QRGeneratorActivity.kt      # Displays B&W QR code, save, share & history insertion
│   ├── ScannerActivity.kt          # Camera QR scanner with torch toggle
│   └── ScannedResultActivity.kt    # Scanned vCard display & "Add to Contacts" action
│
├── fragments/
│   ├── HomeFragment.kt             # Dashboard cards & live statistics
│   ├── ContactsFragment.kt         # Device contacts list with search & permission handler
│   ├── HistoryFragment.kt          # Saved QR history list & deletion controls
│   └── SettingsFragment.kt         # App info, privacy notice & clear history option
│
├── adapters/
│   ├── ContactAdapter.kt           # RecyclerView adapter for contacts list
│   └── HistoryAdapter.kt           # RecyclerView adapter for QR history items
│
├── models/
│   ├── ContactModel.kt             # Data class representing a contact
│   └── QRHistoryModel.kt           # Data class representing a QR history record
│
├── database/
│   └── QRDatabaseHelper.kt         # SQLiteOpenHelper for qrvault.db
│
└── utils/
    ├── QRCodeGenerator.kt          # ZXing vCard 3.0 encoder & B&W bitmap builder
    ├── ContactRepository.kt        # ContentResolver queries for ContactsContract
    └── QRStorageUtils.kt           # MediaStore gallery saving & FileProvider sharing
```

---

## 🛢️ Database Schema (`qrvault.db`)

### Table: `qr_history`

| Column Name | Data Type | Constraints | Description |
|---|---|---|---|
| `id` | `INTEGER` | `PRIMARY KEY AUTOINCREMENT` | Unique history record ID |
| `contact_name` | `TEXT` | `NOT NULL` | Contact full display name |
| `phone_number` | `TEXT` | Default empty | Contact primary phone number |
| `email` | `TEXT` | Default empty | Contact email address |
| `qr_content` | `TEXT` | `NOT NULL` | Encoded vCard 3.0 raw string |
| `created_at` | `TEXT` | `NOT NULL` | Formatted creation date & time |

---

## 🧰 Tech Stack & Third-Party Libraries

- **Programming Language:** Kotlin 1.9+
- **UI Toolkit:** Android XML with Material Design 3 (`com.google.android.material:material:1.14.0`)
- **View Binding:** Enabled for null-safe layout bindings
- **Database:** Native SQLite via `SQLiteOpenHelper`
- **QR Code Generator:** ZXing Core (`com.google.zxing:core:3.5.3`)
- **QR Code Scanner:** ZXing Android Embedded (`com.journeyapps:zxing-android-embedded:4.3.0`)
- **Android Jetpack:** `androidx.core:core-ktx`, `androidx.activity:activity-ktx`, `androidx.appcompat:appcompat`, `androidx.constraintlayout:constraintlayout`

---

## 🧪 Unit Testing

Unit tests are written using **JUnit 4** to test QR generation logic, vCard formatting, and string parsing:

```bash
# Execute unit tests via Gradle
./gradlew app:testDebugUnitTest
```

---

## 🚀 Future Enhancements

Here are planned future features and enhancements for upcoming releases of **QRVault**:

### 1. 🌐 Expanded QR Code Types
- **Wi-Fi Network Sharing**: Generate QR codes for quick WPA/WPA2 Wi-Fi password sharing.
- **URL & Social Links**: Create QR codes for personal websites, LinkedIn profiles, and social channels.
- **Custom Text & Notes**: Support general text QR code generation.

### 2. 🎨 Enhanced QR Customization
- **Center Logo Overlay**: Option to embed a subtle app icon or user avatar in the center of the QR code without compromising scannability.
- **Custom Quiet Zone & Resolution Settings**: Export QR codes in multiple resolutions (e.g., 512px, 1024px, 2048px for printing).

### 3. 📤 Export & Backup Options
- **CSV & PDF Export**: Export saved QR code history to CSV or printable PDF documents.
- **Database Backup & Restore**: Backup local `qr_history` database to internal storage or JSON format for restoration on new devices.

### 4. 🌙 Dark Mode / Night Theme
- Dynamic switching between the signature **White & Pink** light theme and an **OLED Dark & Pink** night theme.

### 5. ⚡ NFC Contact Sharing
- Integration with Android NFC (Near Field Communication) to tap phones and transfer contact vCards instantly alongside QR codes.

### 6. 🔒 Security & Privacy Lock
- Biometric (Fingerprint / Face Unlock) or PIN lock protection for accessing saved QR history and confidential contact records.

---

## 📄 License

```text
Copyright (c) 2025 Spencer Aaron Fernandes. All Rights Reserved.
Developed for Mobile Application Development Lab Examination.
```
