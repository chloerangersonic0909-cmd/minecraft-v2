package com.minecraftv2.launcher;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends Activity {
    private static final int BG = Color.rgb(17, 18, 20);
    private static final int PANEL = Color.rgb(29, 30, 33);
    private static final int PANEL2 = Color.rgb(42, 43, 47);
    private static final int TEXT = Color.rgb(236, 236, 236);
    private static final int MUTED = Color.rgb(164, 166, 172);
    private static final int ACCENT = Color.rgb(105, 180, 255);
    private static final String PREFS = "fochat_private_settings";
    private LinearLayout root, messages;
    private ScrollView scroll;
    private EditText composer;
    private TextView status;
    private Button sendButton;
    private boolean busy = false;
    private final JSONArray history = new JSONArray();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(BG);
        getWindow().setNavigationBarColor(BG);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildUi();
        if (getApiKey().isEmpty()) composer.postDelayed(() -> showApiKeyDialog(false), 400);
    }

    private void buildUi() {
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(BG);
        root.setPadding(dp(14), dp(8), dp(14), dp(8));
        setContentView(root);

        LinearLayout top = new LinearLayout(this);
        top.setGravity(Gravity.CENTER_VERTICAL);
        top.setPadding(dp(4), dp(8), dp(2), dp(12));
        TextView brand = new TextView(this);
        brand.setText("FoChat gpi");
        brand.setTextColor(TEXT);
        brand.setTextSize(21);
        brand.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        top.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        Button settings = button("ตั้งค่า");
        settings.setOnClickListener(v -> showApiKeyDialog(true));
        top.addView(settings);
        root.addView(top);

        TextView subtitle = new TextView(this);
        subtitle.setText("FoChat aii  ·  ผู้ช่วย AI ส่วนตัว");
        subtitle.setTextColor(MUTED);
        subtitle.setTextSize(12);
        subtitle.setPadding(dp(4), 0, 0, dp(12));
        root.addView(subtitle);

        scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        messages = new LinearLayout(this);
        messages.setOrientation(LinearLayout.VERTICAL);
        messages.setPadding(dp(2), dp(4), dp(2), dp(12));
        scroll.addView(messages);
        root.addView(scroll, new LinearLayout.LayoutParams(-1, 0, 1));

        if (history.length() == 0) addWelcome();

        LinearLayout inputRow = new LinearLayout(this);
        inputRow.setGravity(Gravity.CENTER_VERTICAL);
        inputRow.setPadding(dp(7), dp(5), dp(5), dp(5));
        inputRow.setBackground(shape(PANEL2, 22));
        composer = new EditText(this);
        composer.setTextColor(TEXT);
        composer.setHintTextColor(MUTED);
        composer.setHint("ถามอะไรก็ได้ หรือวางโค้ดที่นี่...");
        composer.setTextSize(15);
        composer.setPadding(dp(10), dp(8), dp(8), dp(8));
        composer.setBackgroundColor(Color.TRANSPARENT);
        composer.setMinHeight(dp(46));
        composer.setMaxLines(5);
        composer.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        composer.setImeOptions(EditorInfo.IME_ACTION_SEND);
        composer.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) { sendMessage(); return true; }
            return false;
        });
        inputRow.addView(composer, new LinearLayout.LayoutParams(0, -2, 1));
        sendButton = button("ส่ง");
        sendButton.setTextColor(Color.WHITE);
        sendButton.setBackground(shape(ACCENT, 18));
        inputRow.addView(sendButton, new LinearLayout.LayoutParams(dp(58), dp(44)));
        sendButton.setOnClickListener(v -> sendMessage());
        root.addView(inputRow);

        TextView foot = new TextView(this);
        foot.setText("AI อาจตอบผิดพลาด · ตรวจสอบข้อมูลสำคัญก่อนนำไปใช้");
        foot.setTextColor(MUTED);
        foot.setTextSize(10);
        foot.setGravity(Gravity.CENTER);
        foot.setPadding(0, dp(7), 0, dp(3));
        root.addView(foot);
    }

    private void addWelcome() {
        TextView title = new TextView(this);
        title.setText("สวัสดี เราคือ FoChat aii");
        title.setTextColor(TEXT);
        title.setTextSize(24);
        title.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        title.setGravity(Gravity.CENTER);
        title.setPadding(dp(8), dp(42), dp(8), dp(12));
        messages.addView(title);
        TextView desc = new TextView(this);
        desc.setText("ผู้ช่วยส่วนตัวสำหรับเขียนโค้ด JavaScript / Python, แก้ปัญหายาก ๆ, คำนวณเลข และอธิบายข้อมูลเชิงลึก\n\nถามได้ตรง ๆ ไม่ต้องเขียนคำสั่งยาว");
        desc.setTextColor(MUTED);
        desc.setTextSize(15);
        desc.setGravity(Gravity.CENTER);
        desc.setPadding(dp(18), dp(4), dp(18), dp(20));
        messages.addView(desc);
        addSuggestion("ช่วยเขียนโค้ด JavaScript ให้หน่อย");
        addSuggestion("อธิบายเรื่องยากให้เข้าใจง่าย");
        addSuggestion("ช่วยคำนวณและตรวจคำตอบ");
    }

    private void addSuggestion(String text) {
        Button b = button(text);
        b.setGravity(Gravity.CENTER);
        b.setTextColor(TEXT);
        b.setBackground(shape(PANEL, 18));
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(dp(8), dp(4), dp(8), dp(4));
        messages.addView(b, p);
        b.setOnClickListener(v -> { composer.setText(text); composer.setSelection(composer.length()); composer.requestFocus(); });
    }

    private void sendMessage() {
        if (busy) return;
        String prompt = composer.getText().toString().trim();
        if (prompt.isEmpty()) return;
        if (getApiKey().isEmpty()) { showApiKeyDialog(true); return; }
        composer.setText("");
        hideKeyboard();
        if (messages.getChildCount() > 0 && history.length() == 0) messages.removeAllViews();
        addBubble("คุณ", prompt, true);
        try {
            JSONObject user = new JSONObject();
            user.put("role", "user");
            user.put("content", prompt);
            history.put(user);
        } catch (Exception ignored) {}
        busy = true;
        sendButton.setEnabled(false);
        status = addBubble("FoChat aii", "กำลังคิดคำตอบ…", false);
        new Thread(() -> requestAnswer()).start();
    }

    private void requestAnswer() {
        HttpURLConnection conn = null;
        String answer;
        try {
            JSONObject body = new JSONObject();
            body.put("model", "openrouter/free");
            body.put("messages", makeMessages());
            body.put("temperature", 0.4);
            URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(25000);
            conn.setReadTimeout(90000);
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + getApiKey());
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            conn.setRequestProperty("HTTP-Referer", "https://fochat-gpi.local");
            conn.setRequestProperty("X-Title", "FoChat gpi");
            byte[] data = body.toString().getBytes("UTF-8");
            conn.getOutputStream().write(data);
            int code = conn.getResponseCode();
            InputStream stream = code >= 200 && code < 300 ? conn.getInputStream() : conn.getErrorStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder result = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) result.append(line);
            reader.close();
            JSONObject json = new JSONObject(result.toString());
            if (code < 200 || code >= 300) {
                String detail = json.optJSONObject("error") != null ? json.optJSONObject("error").optString("message", "HTTP " + code) : "HTTP " + code;
                answer = "เชื่อมต่อ AI ไม่สำเร็จ: " + detail + "\n\nตรวจสอบ API Key หรือรอสักครู่แล้วลองใหม่";
            } else {
                JSONArray choices = json.optJSONArray("choices");
                answer = choices != null && choices.length() > 0
                    ? choices.getJSONObject(0).getJSONObject("message").optString("content", "AI ไม่ได้ส่งข้อความกลับมา")
                    : "AI ไม่ได้ส่งคำตอบกลับมา ลองส่งอีกครั้ง";
                JSONObject assistant = new JSONObject();
                assistant.put("role", "assistant");
                assistant.put("content", answer);
                history.put(assistant);
            }
        } catch (Exception e) {
            answer = "เชื่อมต่อไม่สำเร็จ: " + e.getMessage() + "\nตรวจสอบอินเทอร์เน็ตและ API Key แล้วลองอีกครั้ง";
        } finally {
            if (conn != null) conn.disconnect();
        }
        final String finalAnswer = answer;
        runOnUiThread(() -> {
            if (status != null) status.setText(finalAnswer);
            busy = false;
            sendButton.setEnabled(true);
            scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
        });
    }

    private JSONArray makeMessages() throws Exception {
        JSONArray out = new JSONArray();
        JSONObject system = new JSONObject();
        system.put("role", "system");
        system.put("content",
            "You are FoChat aii, the private AI assistant inside FoChat gpi. Speak Thai by default. " +
            "Your personality is calm, clever, kind, lightly teasing when appropriate, and respectful. " +
            "Your top specialty is JavaScript and Python coding: provide complete usable code, explain where it goes, debug carefully, and avoid unnecessary filler. " +
            "Also excel at arithmetic, detailed research-style explanations, and practical step-by-step help. " +
            "When giving factual information, distinguish known facts from uncertainty and provide useful source URLs when you know them. Never fabricate a claim that you performed live web browsing; if you cannot verify a link, say so. " +
            "Keep responses readable with headings and code blocks, use few emojis, and be honest about limitations. " +
            "Never ask the user to share passwords, private keys, or API secrets in chat.");
        out.put(system);
        int start = Math.max(0, history.length() - 20);
        for (int i = start; i < history.length(); i++) out.put(history.getJSONObject(i));
        return out;
    }

    private TextView addBubble(String who, String content, boolean user) {
        LinearLayout wrapper = new LinearLayout(this);
        wrapper.setOrientation(LinearLayout.VERTICAL);
        wrapper.setPadding(dp(12), dp(10), dp(12), dp(10));
        wrapper.setBackground(shape(user ? Color.rgb(45, 55, 67) : PANEL, 18));
        TextView label = new TextView(this);
        label.setText(who);
        label.setTextColor(user ? ACCENT : Color.rgb(135, 206, 170));
        label.setTextSize(12);
        label.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        wrapper.addView(label);
        TextView body = new TextView(this);
        body.setText(content);
        body.setTextColor(TEXT);
        body.setTextSize(15);
        body.setTextIsSelectable(true);
        body.setPadding(0, dp(5), 0, dp(1));
        wrapper.addView(body);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.setMargins(dp(2), dp(5), dp(2), dp(8));
        messages.addView(wrapper, p);
        scroll.post(() -> scroll.fullScroll(View.FOCUS_DOWN));
        return body;
    }

    private void showApiKeyDialog(boolean allowCancel) {
        EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setHint("sk-or-v1-...");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        input.setText(getApiKey());
        LinearLayout box = new LinearLayout(this);
        box.setPadding(dp(22), dp(6), dp(22), dp(2));
        box.addView(input, new LinearLayout.LayoutParams(-1, -2));
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
            .setTitle("ตั้งค่า OpenRouter API Key")
            .setMessage("สร้างคีย์ได้ที่ openrouter.ai/settings/keys\nคีย์จะเก็บไว้ในพื้นที่ข้อมูลส่วนตัวของแอปบนเครื่องนี้ อย่าแชร์คีย์กับผู้อื่น")
            .setView(box)
            .setPositiveButton("บันทึก", (d, w) -> {
                String key = input.getText().toString().trim();
                if (key.isEmpty()) {
                    getPrefs().edit().remove("api_key").apply();
                    Toast.makeText(this, "ลบ API Key แล้ว", Toast.LENGTH_SHORT).show();
                } else {
                    getPrefs().edit().putString("api_key", key).apply();
                    Toast.makeText(this, "บันทึก API Key แล้ว", Toast.LENGTH_SHORT).show();
                }
            });
        if (allowCancel) builder.setNegativeButton("ยกเลิก", null);
        builder.setNeutralButton("ลบคีย์", (d, w) -> {
            getPrefs().edit().remove("api_key").apply();
            Toast.makeText(this, "ลบ API Key แล้ว", Toast.LENGTH_SHORT).show();
        });
        builder.show();
    }

    private SharedPreferences getPrefs() { return getSharedPreferences(PREFS, MODE_PRIVATE); }
    private String getApiKey() { return getPrefs().getString("api_key", "").trim(); }
    private Button button(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setTextColor(TEXT);
        b.setPadding(dp(10), dp(4), dp(10), dp(4));
        return b;
    }
    private GradientDrawable shape(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(dp(radius));
        return d;
    }
    private int dp(int value) { return (int) (value * getResources().getDisplayMetrics().density + 0.5f); }
    private void hideKeyboard() {
        try {
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(composer.getWindowToken(), 0);
            composer.clearFocus();
        } catch (Exception ignored) {}
    }
}