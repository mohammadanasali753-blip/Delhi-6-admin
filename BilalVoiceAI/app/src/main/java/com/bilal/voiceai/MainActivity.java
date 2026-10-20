package com.bilal.voiceai;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {
    private static final int SPEECH_REQUEST = 101;
    private static final int AUDIO_PERMISSION = 102;
    private TextView status;
    private AudioManager audio;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        audio = (AudioManager) getSystemService(AUDIO_SERVICE);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 48, 32, 32);
        TextView title = new TextView(this);
        title.setText("Bilal Voice AI");
        title.setTextSize(28);
        layout.addView(title);
        status = new TextView(this);
        status.setText("नीचे बटन दबाएँ और हिंदी में कमांड बोलें। यह शुरुआती वर्ज़न है; लगातार बैकग्राउंड में नहीं सुनता।");
        status.setTextSize(17);
        status.setPadding(0, 24, 0, 24);
        layout.addView(status);
        Button speak = new Button(this);
        speak.setText("बोलकर कंट्रोल करें");
        speak.setOnClickListener(v -> startVoice());
        layout.addView(speak);
        Button yt = new Button(this);
        yt.setText("YouTube खोलें");
        yt.setOnClickListener(v -> launchPackage("com.google.android.youtube"));
        layout.addView(yt);
        Button insta = new Button(this);
        insta.setText("Instagram खोलें");
        insta.setOnClickListener(v -> launchPackage("com.instagram.android"));
        layout.addView(insta);
        setContentView(layout);
    }

    private void startVoice() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, AUDIO_PERMISSION);
            return;
        }
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "कमांड बोलें, जैसे YouTube खोल");
        try { startActivityForResult(intent, SPEECH_REQUEST); }
        catch (ActivityNotFoundException e) { status.setText("इस फोन में वॉइस रिकग्निशन सेवा उपलब्ध नहीं है।"); }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (results != null && !results.isEmpty()) handleCommand(results.get(0).toLowerCase(Locale.ROOT));
        }
    }

    private void handleCommand(String command) {
        status.setText("आपने कहा: " + command);
        if (command.contains("youtube") || command.contains("यूट्यूब")) {
            launchPackage("com.google.android.youtube");
        } else if (command.contains("instagram") || command.contains("इंस्टाग्राम")) {
            launchPackage("com.instagram.android");
        } else if (command.contains("आवाज़ कम") || command.contains("वॉल्यूम कम") || command.contains("volume down")) {
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
        } else if (command.contains("आवाज़ बढ़ा") || command.contains("वॉल्यूम बढ़ा") || command.contains("volume up")) {
            audio.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
        } else if (command.contains("सेटिंग") || command.contains("settings")) {
            startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
        } else if (command.contains("कॉल") || command.contains("call")) {
            status.setText("कॉल करने के लिए फोन ऐप में संपर्क चुनें। इस वर्ज़न में ऑटो-कॉल नहीं है।");
            startActivity(new Intent(Intent.ACTION_DIAL));
        } else {
            Toast.makeText(this, "यह कमांड अभी सपोर्ट नहीं है", Toast.LENGTH_SHORT).show();
        }
    }

    private void launchPackage(String packageName) {
        Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch != null) startActivity(launch);
        else {
            String url = packageName.contains("youtube") ? "https://www.youtube.com" : "https://www.instagram.com";
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == AUDIO_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) startVoice();
        else status.setText("आवाज़ पहचानने के लिए माइक्रोफ़ोन की अनुमति ज़रूरी है।");
    }
}
