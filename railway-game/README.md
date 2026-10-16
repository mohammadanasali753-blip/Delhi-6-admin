# Indian Railway Open World — Android prototype

This is a Godot 4.3 mobile-first greybox prototype. It creates the railway scene procedurally using primitive 3D meshes, so no third-party assets are required.

## Prototype features
- Locomotive and two passenger coaches
- Track, sleepers, simple station platforms and sky lighting
- Touch buttons for acceleration, braking and horn feedback
- Speed readout and follow camera

## Build APK with GitHub Actions
1. Open the repository's **Actions** tab.
2. Select **Build Indian Railway APK**.
3. Press **Run workflow** on the main branch.
4. Wait for the job to finish, then open its run and download the **Indian-Railway-Open-World-APK** artifact.
5. Extract the artifact ZIP on your Android phone and install the APK. Android may ask you to allow installation from that source.

This is a prototype, not yet an ultra-realistic finished simulator. The first build uses placeholder geometry and has no authentic horn audio, detailed locomotive meshes, complete railway physics, large-world streaming, or real-world route data. The Android export needs the matching Godot export templates and Android build tools available in the CI image.
