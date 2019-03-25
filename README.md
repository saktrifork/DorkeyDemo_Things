# DorkeyDemo_Things

A demo of Android things integration with firebase

## Requiurements

- Any kind of Android Things [enabled device](https://developer.android.com/things/hardware)
- Android studio
- A google account

## Setting up

Follow the [official guide](https://developer.android.com/things/hardware/raspberrypi#serial-console) from Google to set up.

- Connect your laptop to your device via USB and make sure youre are on the same network.

- Boot up the device and check its IP address
- Connect to the device via the adb command line interface

```bash
adb connect <IPADDRESS>
$: connected to <IPADDRESS>
```

(If you get an error saying `adb command not found` make sure you have added the path to  `adb` to your environmental variables)

- Open the project in android studio
- Run the project on the device

### Notes

- Remember to update Google play services if you get an error saying "Unable to initialize Firebase"