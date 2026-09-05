package com.tvman.antiremover;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_FILE = 1001;

    private static final String[] ANTI_CHEAT_NAMES = {
            "GCS-Anti-Cheat",
            "GCSAntiCheat",
            "KSHRAnti",
            "KshrAnti",
            "KosherAnti",
            "QuestLink",
            "BodgeAntiCheat",
            "BODAntiCheat",
            "AntiCheatManager",
            "AntiCheatLogger",
            "AntiCheatResponder",
            "AntiCheatWebhook",
            "ModLoaderDetector",
            "AntiVPN",
            "VersionChecker",
            "HaltAntiCheat",
            "HorrorAI",
            "AIKhr",
            "ZZYXX",
            "AntiCheat",
            "AntiCheatV1",
            "AntiCheatV2",
            "AntiChe",
            "AntiCheese",
            "KSHR",
            "Kshr",
            "Kosher",
            "ShivaAnti",
            "PrimalFearAnti",
            "NicoAnti",
            "TimmehAnti",
            "PolarAnti",
            "AlinterexAnti",
            "TomatoAnti",
            "QuestAnti",
            "QuestAuthAnti",
            "AntiAPK",
            "AntiMod",
            "AntiMods",
            "AntiMenu",
            "AntiLoader",
            "MelonDetector",
            "LemonDetector",
            "HarmonyDetector",
            "BepInExDetector",
            "AssemblyChecker",
            "FolderChecker",
            "WebhookReporter",
            "BanWebhook",
            "ReportWebhook",
            "Drinking",
            "Modu",
            "FPSManager"
    };

    private TextView txtResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtResults = findViewById(R.id.txtResults);
        Button btn = findViewById(R.id.btnSelect);
        btn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            startActivityForResult(Intent.createChooser(intent, "Select data.unity3d"), PICK_FILE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                scanUri(uri);
            }
        }
    }

    private void scanUri(Uri uri) {
        txtResults.setText("Scanning...");
        new Thread(() -> {
            try {
                byte[] bytes = readAll(uri);
                List<String> found = new ArrayList<>();
                for (String name : ANTI_CHEAT_NAMES) {
                    if (containsUtf8(bytes, name) || containsUtf16Le(bytes, name)) {
                        found.add(name);
                    }
                }
                StringBuilder sb = new StringBuilder();
                sb.append("File size: ").append(bytes.length).append(" bytes\n");
                sb.append("Matches: ").append(found.size()).append("\n\n");
                if (found.isEmpty()) {
                    sb.append("No listed anti-cheat names found.\n");
                } else {
                    for (String n : found) {
                        sb.append("[FOUND] ").append(n).append("\n");
                    }
                }
                runOnUiThread(() -> txtResults.setText(sb.toString()));
            } catch (Exception e) {
                runOnUiThread(() -> txtResults.setText("Error: " + e.getMessage()));
            }
        }).start();
    }

    private byte[] readAll(Uri uri) throws Exception {
        try (InputStream in = getContentResolver().openInputStream(uri);
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            if (in == null) throw new Exception("Cannot open file");
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            return out.toByteArray();
        }
    }

    private static boolean containsUtf8(byte[] data, String needle) {
        byte[] n = needle.getBytes(StandardCharsets.UTF_8);
        return indexOf(data, n) >= 0;
    }

    private static boolean containsUtf16Le(byte[] data, String needle) {
        byte[] n = needle.getBytes(StandardCharsets.UTF_16LE);
        return indexOf(data, n) >= 0;
    }

    private static int indexOf(byte[] hay, byte[] needle) {
        if (needle.length == 0 || hay.length < needle.length) return -1;
        outer:
        for (int i = 0; i <= hay.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (hay[i + j] != needle[j]) continue outer;
            }
            return i;
        }
        return -1;
    }
}
