# USB Debugging Popup Overlay

A lookalike of Android's stock **"Allow USB debugging?"** dialog in which the **Allow** button is permanently disabled. The checkbox and **Cancel** work normally. It is built for locked-down phones where USB debugging should never be authorized, but where the user should still see a familiar, non-confusing prompt.

The RSA fingerprint shown in the dialog is a **hardcoded fake**, not a real key. Change `FINGERPRINT` in `Blocked.java` if you want a different one.

## What's in this repo

| Folder | What it is |
|---|---|
| `popup-app/` | apktool project for the popup app (`com.kosherapps.usbguard`). `Blocked.java` builds the dialog. |
| `overlay/` | Resource-only overlay APK (`usbdlg_overlay`) that points Android's USB debugging confirmation at the popup app (see `res/values/config.xml`). |
| `builds/` | Prebuilt, debug-signed `usbguard.apk` and `usbdlg_overlay.apk`. |

## Install

The popup app and the overlay must be installed as system components to replace the stock dialog (for example `/product/priv-app/usbguard/` and `/product/overlay/` in a custom ROM). How you package that is up to you, for example your own Magisk module.

For a quick look at the popup without touching the system:

```powershell
adb install -r -d builds\usbguard.apk
adb shell am start -n com.kosherapps.usbguard/.Blocked
```

## Build from source (Windows, PowerShell)

Requires a JDK, `apktool`, `uber-apk-signer`, and the Android SDK (an `android.jar` plus `d8` from build-tools).

```powershell
$ANDROID_JAR = "$env:LOCALAPPDATA\Android\Sdk\platforms\android-34\android.jar"

# 1. Compile the popup app and dex it
mkdir popup-app\classes -Force | Out-Null
javac --release 8 -cp $ANDROID_JAR -d popup-app\classes popup-app\src\com\kosherapps\usbguard\Blocked.java
d8 --min-api 26 --lib $ANDROID_JAR --output popup-app (Get-ChildItem popup-app\classes -Recurse -Filter *.class).FullName

# 2. Build both APKs (apktool picks up popup-app\classes.dex)
apktool b popup-app -o usbguard.apk
apktool b overlay -o usbdlg_overlay.apk

# 3. Sign
uber-apk-signer -a usbguard.apk
uber-apk-signer -a usbdlg_overlay.apk
```

### Gotchas

- `resources.arsc` must stay uncompressed on Android 11+. Keep `doNotCompress: [resources.arsc]` in each `apktool.yml`, or install fails with a "Targeting R+ requires resources.arsc ... uncompressed" error.
- `apktool b` does not recompile Java. After editing `Blocked.java`, redo step 1 first.
- Delete the old `classes.dex` and `classes/` before recompiling, so `classes.dex` never ends up in the APK twice. A duplicate entry makes `uber-apk-signer` silently produce an empty APK.
- The popup needs no permissions, so no `privapp-permissions` XML is required.

## Notes

- Builds are signed with the standard Android **debug** key. Re-sign with your own key for anything beyond testing.
- The checkbox is inset 16dp so it lines up with the dialog's text, matching the stock dialog.
