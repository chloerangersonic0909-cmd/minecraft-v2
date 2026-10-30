package com.minecraftv2.launcher;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private final int bg = Color.rgb(12, 17, 23);
    private final int panel = Color.rgb(24, 33, 43);
    private final int panel2 = Color.rgb(34, 47, 59);
    private final int green = Color.rgb(105, 222, 112);
    private final int cyan = Color.rgb(88, 220, 232);
    private final int muted = Color.rgb(171, 188, 199);
    private LinearLayout root;
    private String page = "play";
    private String selectedBlock = "🟩";
    private int x = 2, z = 2, y = 0;
    private int blocksPlaced = 0, blocksBroken = 0;
    private final String[][] world = new String[5][5];
    private TextView positionLabel, actionLabel, worldName;
    private final List<Button> hotbarButtons = new ArrayList<>();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(bg);
        getWindow().setNavigationBarColor(bg);
        getWindow().getDecorView().setSystemUiVisibility(0);
        for (int r = 0; r < 5; r++) for (int c = 0; c < 5; c++)
            world[r][c] = ((r + c) % 4 == 0) ? "🌱" : "🟩";
        showPage("play");
    }

    private void showPage(String which) {
        page = which;
        root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(16), dp(14), dp(16), dp(16));
        root.setBackgroundColor(bg);
        setContentView(root);
        header();
        if ("play".equals(which)) playPage();
        else if ("worlds".equals(which)) worldsPage();
        else controlsPage();
        navigation();
    }

    private void header() {
        LinearLayout row = row();
        TextView logo = text("▦", 25, bg, true);
        logo.setGravity(Gravity.CENTER);
        logo.setBackground(round(green, 13));
        row.addView(logo, new LinearLayout.LayoutParams(dp(48), dp(48)));
        LinearLayout brand = col();
        brand.setPadding(dp(10), dp(2), 0, 0);
        brand.addView(text("MINECRAFT V2", 14, Color.WHITE, true));
        brand.addView(text("PC-STYLE SANDBOX", 10, cyan, true));
        row.addView(brand, new LinearLayout.LayoutParams(0, -2, 1));
        TextView mode = text("SIM", 11, green, true);
        mode.setGravity(Gravity.CENTER);
        mode.setBackground(round(panel, 10));
        row.addView(mode, new LinearLayout.LayoutParams(dp(48), dp(34)));
        root.addView(row);
    }

    private void playPage() {
        TextView title = text("Create & Explore", 27, Color.WHITE, true);
        addTop(title, 20);
        worldName = text("🌿  My Creative World", 14, muted, true);
        addTop(worldName, 5);

        LinearLayout scene = col();
        scene.setPadding(dp(12), dp(12), dp(12), dp(12));
        scene.setBackground(round(Color.rgb(38, 69, 78), 18));
        LinearLayout.LayoutParams sceneLp = new LinearLayout.LayoutParams(-1, -2);
        sceneLp.topMargin = dp(16);
        root.addView(scene, sceneLp);
        LinearLayout sky = row();
        sky.setGravity(Gravity.CENTER);
        TextView skyLabel = text("☀️  BLOCK WORLD  ☁️", 13, Color.WHITE, true);
        skyLabel.setGravity(Gravity.CENTER);
        sky.addView(skyLabel);
        scene.addView(sky);
        GridLayout map = new GridLayout(this);
        map.setColumnCount(5);
        map.setRowCount(5);
        LinearLayout.LayoutParams mapLp = new LinearLayout.LayoutParams(-1, -2);
        mapLp.topMargin = dp(12);
        scene.addView(map, mapLp);
        for (int r = 0; r < 5; r++) {
            for (int c = 0; c < 5; c++) {
                final int rr = r, cc = c;
                Button block = new Button(this);
                block.setText((r == z && c == x) ? "🧍" : world[r][c]);
                block.setTextSize(19);
                block.setAllCaps(false);
                block.setPadding(0, 0, 0, 0);
                block.setMinWidth(0);
                block.setMinimumWidth(0);
                block.setMinHeight(0);
                block.setMinimumHeight(0);
                block.setBackground(round((r == z && c == x) ? Color.rgb(78, 126, 137) : Color.rgb(78, 130, 78), 5));
                GridLayout.LayoutParams p = new GridLayout.LayoutParams();
                p.width = 0; p.height = dp(45); p.columnSpec = GridLayout.spec(c, 1f); p.rowSpec = GridLayout.spec(r);
                p.setMargins(dp(2), dp(2), dp(2), dp(2));
                map.addView(block, p);
                block.setOnClickListener(v -> {
                    z = rr; x = cc;
                    actionLabel.setText("Moved to tile " + (x + 1) + ", " + (z + 1));
                    redrawPlay();
                });
            }
        }
        positionLabel = text("Position  X:" + x + "  Y:" + y + "  Z:" + z, 12, Color.WHITE, true);
        LinearLayout.LayoutParams posLp = new LinearLayout.LayoutParams(-1, -2);
        posLp.topMargin = dp(8);
        scene.addView(positionLabel, posLp);

        actionLabel = text("Tap a block to move. Use PC-style controls below.", 12, muted, false);
        addTop(actionLabel, 10);
        TextView controlTitle = text("CONTROLS", 12, cyan, true);
        addTop(controlTitle, 17);
        LinearLayout controls = row();
        controls.setGravity(Gravity.CENTER);
        controls.addView(controlButton("↖", "NW"));
        LinearLayout mid = col();
        mid.setGravity(Gravity.CENTER);
        mid.addView(controlButton("▲", "UP"));
        LinearLayout leftRight = row();
        leftRight.addView(controlButton("◀", "LEFT"));
        leftRight.addView(controlButton("▼", "DOWN"));
        leftRight.addView(controlButton("▶", "RIGHT"));
        mid.addView(leftRight);
        controls.addView(mid);
        LinearLayout actions = col();
        actions.setPadding(dp(8), 0, 0, 0);
        actions.addView(controlButton("⤒", "JUMP"));
        actions.addView(controlButton("⛏", "BREAK"));
        actions.addView(controlButton("＋", "PLACE"));
        controls.addView(actions);
        addTop(controls, 5);

        TextView hotbarTitle = text("HOTBAR  ·  SELECT BLOCK", 11, muted, true);
        addTop(hotbarTitle, 15);
        LinearLayout hotbar = row();
        hotbar.setGravity(Gravity.CENTER);
        hotbarButtons.clear();
        String[] blocks = {"🟩", "🟫", "🪨", "🪵", "🧱"};
        for (String block : blocks) {
            Button b = button(block, block.equals(selectedBlock) ? green : panel2, block.equals(selectedBlock) ? bg : Color.WHITE);
            b.setTextSize(19);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(48), 1);
            p.setMargins(dp(3), 0, dp(3), 0);
            hotbar.addView(b, p);
            hotbarButtons.add(b);
            b.setOnClickListener(v -> { selectedBlock = block; redrawPlay(); });
        }
        addTop(hotbar, 6);
        TextView stats = text("Placed: " + blocksPlaced + "   Broken: " + blocksBroken, 11, muted, false);
        addTop(stats, 8);
    }

    private void redrawPlay() {
        showPage("play");
    }

    private Button controlButton(String label, String action) {
        Button b = button(label, panel2, Color.WHITE);
        b.setTextSize(17);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(48), dp(44));
        p.setMargins(dp(3), dp(3), dp(3), dp(3));
        b.setLayoutParams(p);
        b.setOnClickListener(v -> doAction(action));
        return b;
    }

    private void doAction(String action) {
        switch (action) {
            case "UP": z = Math.max(0, z - 1); break;
            case "DOWN": z = Math.min(4, z + 1); break;
            case "LEFT": x = Math.max(0, x - 1); break;
            case "RIGHT": x = Math.min(4, x + 1); break;
            case "NW": x = Math.max(0, x - 1); z = Math.max(0, z - 1); break;
            case "JUMP": y = y == 0 ? 1 : 0; break;
            case "BREAK":
                world[z][x] = "⬜"; blocksBroken++;
                break;
            case "PLACE":
                world[z][x] = selectedBlock; blocksPlaced++;
                break;
        }
        redrawPlay();
    }

    private void worldsPage() {
        addTop(text("Worlds", 28, Color.WHITE, true), 20);
        addTop(text("Create a local sandbox world and choose your play style.", 14, muted, false), 8);
        addTop(worldCard("🌿  My Creative World", "Creative · Local demo world", true), 18);
        addTop(worldCard("🏜️  New World", "Generate a fresh 5 × 5 block world", false), 10);
        addTop(worldCard("🧊  Flat Test World", "Simple flat map for building practice", false), 10);
        addTop(text("Worlds in this version are saved only while the app remains open. Cloud saves and real Minecraft world import are not included.", 12, muted, false), 20);
    }

    private View worldCard(String title, String desc, boolean current) {
        LinearLayout card = col();
        card.setPadding(dp(14), dp(15), dp(14), dp(15));
        card.setBackground(round(panel, 16));
        card.addView(text(title, 16, Color.WHITE, true));
        TextView sub = text(desc, 12, muted, false);
        LinearLayout.LayoutParams slp = new LinearLayout.LayoutParams(-1, -2);
        slp.topMargin = dp(5);
        card.addView(sub, slp);
        Button choose = button(current ? "Continue" : "Create / Select", current ? green : panel2, current ? bg : Color.WHITE);
        LinearLayout.LayoutParams bp = new LinearLayout.LayoutParams(-1, dp(44));
        bp.topMargin = dp(12);
        card.addView(choose, bp);
        choose.setOnClickListener(v -> {
            if (!current) {
                for (int r = 0; r < 5; r++) for (int c = 0; c < 5; c++) world[r][c] = ((int)(Math.random() * 4) == 0) ? "🪨" : "🟩";
                x = 2; z = 2; y = 0; blocksPlaced = 0; blocksBroken = 0;
                worldName.setText("🌿  New Creative World");
                Toast.makeText(this, "Created a new demo world", Toast.LENGTH_SHORT).show();
            }
            showPage("play");
        });
        return card;
    }

    private void controlsPage() {
        addTop(text("Control Settings", 27, Color.WHITE, true), 20);
        addTop(text("PC-inspired touch layout", 14, cyan, true), 7);
        addTop(setting("Movement", "W A S D / directional pad", "Touch buttons move around the demo world."), 18);
        addTop(setting("Jump", "Space", "Toggle the demo jump state with the ⤒ button."), 10);
        addTop(setting("Break block", "Left-click", "Select ⛏ to clear the current tile."), 10);
        addTop(setting("Place block", "Right-click", "Select ＋ to place the selected hotbar block."), 10);
        addTop(setting("Hotbar", "1–5", "Tap a block slot to change the selected building block."), 10);
        addTop(text("Important", 15, Color.WHITE, true), 22);
        addTop(text("This is a standalone Minecraft-inspired sandbox simulation. It does not run Mojang's Minecraft PC engine, does not connect to the official game, and its touch controls do not control another app. A real PC edition launcher needs a compatible game runtime and licensed game files.", 13, muted, false), 8);
    }

    private View setting(String heading, String key, String description) {
        LinearLayout card = col();
        card.setPadding(dp(14), dp(13), dp(14), dp(13));
        card.setBackground(round(panel, 15));
        LinearLayout top = row();
        top.addView(text(heading, 15, Color.WHITE, true), new LinearLayout.LayoutParams(0, -2, 1));
        top.addView(text(key, 11, green, true));
        card.addView(top);
        TextView desc = text(description, 12, muted, false);
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(7);
        card.addView(desc, p);
        return card;
    }

    private void navigation() {
        LinearLayout nav = row();
        nav.setPadding(0, dp(12), 0, 0);
        String[][] items = {{"play", "▣", "PLAY"}, {"worlds", "▦", "WORLDS"}, {"controls", "⌘", "CONTROLS"}};
        for (String[] item : items) {
            boolean active = page.equals(item[0]);
            Button b = button(item[1] + "\n" + item[2], active ? panel2 : bg, active ? green : muted);
            b.setTextSize(10);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, dp(52), 1);
            p.setMargins(dp(3), 0, dp(3), 0);
            nav.addView(b, p);
            b.setOnClickListener(v -> showPage(item[0]));
        }
        root.addView(nav);
    }

    private void addTop(View view, int margin) {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -2);
        p.topMargin = dp(margin);
        root.addView(view, p);
    }
    private LinearLayout row() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.HORIZONTAL);
        return l;
    }
    private LinearLayout col() {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        return l;
    }
    private TextView text(String value, int size, int color, boolean bold) {
        TextView t = new TextView(this);
        t.setText(value); t.setTextSize(size); t.setTextColor(color);
        if (bold) t.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        return t;
    }
    private Button button(String value, int color, int textColor) {
        Button b = new Button(this);
        b.setText(value); b.setTextSize(13); b.setTextColor(textColor);
        b.setAllCaps(false); b.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        b.setBackground(round(color, 12));
        b.setPadding(dp(4), 0, dp(4), 0);
        return b;
    }
    private GradientDrawable round(int color, int radius) {
        GradientDrawable d = new GradientDrawable();
        d.setColor(color); d.setCornerRadius(dp(radius));
        return d;
    }
    private int dp(int value) {
        return (int)(value * getResources().getDisplayMetrics().density + 0.5f);
    }
}
