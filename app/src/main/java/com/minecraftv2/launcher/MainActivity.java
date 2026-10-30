package com.minecraftv2.launcher;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends Activity {
    private static final String BEDROCK_PACKAGE = "com.mojang.minecraftpe";
    private final int bg = Color.rgb(11, 16, 24);
    private final int panel = Color.rgb(22, 30, 42);
    private final int panelLight = Color.rgb(30, 42, 56);
    private final int mint = Color.rgb(87, 240, 183);
    private final int muted = Color.rgb(167, 181, 197);
    private TextView status;
    private TextView version;
    private Button launch;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
        buildUi();
        refreshStatus();
    }

    @Override protected void onResume() {
        super.onResume();
        if (status != null) refreshStatus();
    }

    private void buildUi() {
        ScrollView scroll = new ScrollView(this);
        scroll.setFillViewport(true);
        scroll.setClipToPadding(false);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(20), dp(22), dp(20), dp(26));
        root.setBackground(new GradientDrawable(GradientDrawable.Orientation.TL_BR,
                new int[]{Color.rgb(14, 26, 35), bg, Color.rgb(17, 22, 34)}));
        scroll.addView(root);

        LinearLayout brandRow = row();
        TextView logo = text("M", 25, bg, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(round(mint, 16));
        brandRow.addView(logo, new LinearLayout.LayoutParams(dp(52), dp(52)));
        LinearLayout brandText = column();
        brandText.setPadding(dp(12), dp(2), 0, 0);
        brandText.addView(text("MINECRAFT V2", 13, mint, true));
        TextView kicker = text("BEDROCK COMPANION", 11, muted, true);
        LinearLayout.LayoutParams kickerLp = new LinearLayout.LayoutParams(-2, -2);
        kickerLp.topMargin = dp(4);
        brandText.addView(kicker, kickerLp);
        brandRow.addView(brandText, new LinearLayout.LayoutParams(0, -2, 1));
        root.addView(brandRow);

        TextView title = text("Your world.\nOne tap away.", 34, Color.WHITE, true);
        title.setLineSpacing(dp(1), 1f);
        LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(-1, -2);
        titleLp.topMargin = dp(28);
        root.addView(title, titleLp);
        TextView subtitle = text("A clean, lightweight launcher for Minecraft Bedrock on Android.", 15, muted, false);
        subtitle.setLineSpacing(dp(4), 1f);
        LinearLayout.LayoutParams subLp = new LinearLayout.LayoutParams(-1, -2);
        subLp.topMargin = dp(10);
        root.addView(subtitle, subLp);

        LinearLayout statusCard = column();
        statusCard.setPadding(dp(18), dp(18), dp(18), dp(18));
        statusCard.setBackground(round(panel, 22));
        LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(-1, -2);
        cardLp.topMargin = dp(26);
        root.addView(statusCard, cardLp);

        LinearLayout statusHead = row();
        TextView section = text("GAME STATUS", 11, muted, true);
        statusHead.addView(section, new LinearLayout.LayoutParams(0, -2, 1));
        TextView live = text("●  LIVE CHECK", 10, mint, true);
        statusHead.addView(live);
        statusCard.addView(statusHead);

        status = text("Checking Minecraft…", 21, Color.WHITE, true);
        LinearLayout.LayoutParams statusLp = new LinearLayout.LayoutParams(-1, -2);
        statusLp.topMargin = dp(12);
        statusCard.addView(status, statusLp);
        version = text("Checking whether the official game is installed.", 13, muted, false);
        version.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams verLp = new LinearLayout.LayoutParams(-1, -2);
        verLp.topMargin = dp(7);
        statusCard.addView(version, verLp);

        launch = button("▶   Open Minecraft", mint, bg);
        LinearLayout.LayoutParams launchLp = new LinearLayout.LayoutParams(-1, dp(54));
        launchLp.topMargin = dp(20);
        statusCard.addView(launch, launchLp);
        launch.setOnClickListener(v -> launchMinecraft());

        Button store = button("Get Minecraft  ↗", panelLight, Color.WHITE);
        LinearLayout.LayoutParams storeLp = new LinearLayout.LayoutParams(-1, dp(50));
        storeLp.topMargin = dp(10);
        statusCard.addView(store, storeLp);
        store.setOnClickListener(v -> openStore());

        TextView featuresTitle = text("Made to keep it simple", 20, Color.WHITE, true);
        LinearLayout.LayoutParams featuresLp = new LinearLayout.LayoutParams(-1, -2);
        featuresLp.topMargin = dp(28);
        root.addView(featuresTitle, featuresLp);
        root.addView(feature("01", "Quick launch", "Open the official game directly from one screen."));
        root.addView(feature("02", "Smart detection", "Checks whether Minecraft Bedrock is installed."));
        root.addView(feature("03", "Official source", "A direct Google Play link when the game is missing."));

        TextView disclaimer = text("This companion app does not include Minecraft or game files. Minecraft must be installed separately.", 12, muted, false);
        disclaimer.setLineSpacing(dp(3), 1f);
        LinearLayout.LayoutParams disclaimerLp = new LinearLayout.LayoutParams(-1, -2);
        disclaimerLp.topMargin = dp(22);
        root.addView(disclaimer, disclaimerLp);
        TextView footer = text("MINECRAFT V2   •   VERSION 1.0.0", 10, Color.rgb(111, 130, 149), true);
        footer.setGravity(Gravity.CENTER);
        LinearLayout.LayoutParams footerLp = new LinearLayout.LayoutParams(-1, -2);
        footerLp.topMargin = dp(24);
        root.addView(footer, footerLp);
        setContentView(scroll);
    }

    private View feature(String number, String heading, String description) {
        LinearLayout card = row();
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(14), dp(14), dp(14), dp(14));
        card.setBackground(round(panel, 17));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.topMargin = dp(10);
        card.setLayoutParams(lp);
        TextView numberView = text(number, 12, mint, true);
        numberView.setGravity(Gravity.CENTER);
        numberView.setBackground(round(panelLight, 11));
        card.addView(numberView, new LinearLayout.LayoutParams(dp(42), dp(42)));
        LinearLayout content = column();
        content.setPadding(dp(12), 0, 0, 0);
        content.addView(text(heading, 15, Color.WHITE, true));
        TextView desc = text(description, 12, muted, false);
        desc.setLineSpacing(dp(2), 1f);
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(-1, -2);
        descLp.topMargin = dp(4);
        content.addView(desc, descLp);
        card.addView(content, new LinearLayout.LayoutParams(0, -2, 1));
        return card;
    }

    private void refreshStatus() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(BEDROCK_PACKAGE, 0);
            status.setText("Minecraft is installed");
            status.setTextColor(mint);
            version.setText("Official app detected\nVersion: " + (info.versionName == null ? "Unknown" : info.versionName));
            launch.setEnabled(true);
            launch.setAlpha(1f);
        } catch (PackageManager.NameNotFoundException e) {
            status.setText("Game not detected");
            status.setTextColor(Color.rgb(255, 190, 95));
            version.setText("Install the official Minecraft Bedrock app first, then return here.");
            launch.setEnabled(true);
            launch.setAlpha(1f);
        }
    }

    private void launchMinecraft() {
        Intent intent = getPackageManager().getLaunchIntentForPackage(BEDROCK_PACKAGE);
        if (intent == null) {
            status.setText("Game not detected");
            status.setTextColor(Color.rgb(255, 190, 95));
            version.setText("The official app was not found. Install or update it from Google Play.");
            return;
        }
        try { startActivity(intent); }
        catch (Exception e) {
            status.setText("Could not open Minecraft");
            status.setTextColor(Color.rgb(255, 190, 95));
            version.setText("Check that the official game is installed and up to date.");
        }
    }

    private void openStore() {
        Uri market = Uri.parse("market://details?id=" + BEDROCK_PACKAGE);
        try { startActivity(new Intent(Intent.ACTION_VIEW, market)); }
        catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + BEDROCK_PACKAGE)));
        }
    }

    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        return l;
    }
    private LinearLayout column() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    private TextView text(String value, int sp, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(sp); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private Button button(String value, int color, int textColor) {
        Button b = new Button(this);
        b.setText(value); b.setTextSize(14); b.setTextColor(textColor);
        b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(round(color, 15));
        b.setPadding(dp(12), 0, dp(12), 0);
        return b;
    }
    private GradientDrawable round(int color, int radiusDp) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radiusDp));
        return d;
    }
    private int dp(int v) { return (int)(v * getResources().getDisplayMetrics().density + 0.5f); }
}
