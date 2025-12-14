package org.infiniteflameteam.umoiftng.client.gui;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.infiniteflameteam.umoiftng.client.ClientDialogHandler;
import org.infiniteflameteam.umoiftng.localization.LanguageManager;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class OfficialDialogScreen extends Screen {
    private final String dialogId;
    private final JsonObject dialog;
    private int leftPos;
    private int topPos;
    private int dialogWidth = 320;
    private int dialogHeight = 240;
    private List<Button> buttons = new ArrayList<>();

    public OfficialDialogScreen(String dialogId, JsonObject dialog) {
        super(Component.empty());
        this.dialogId = dialogId;
        this.dialog = dialog;
    }

    @Override
    protected void init() {
        // 计算对话框位置（居中）
        this.leftPos = (this.width - dialogWidth) / 2;
        this.topPos = (this.height - dialogHeight) / 2;

        this.clearWidgets();
        buttons.clear();

        // 根据对话框类型初始化不同的界面
        String type = dialog.has("type") ? dialog.get("type").getAsString() : "notice";

        switch (type) {
            case "notice":
                initNoticeDialog();
                break;
            case "confirmation":
                initConfirmationDialog();
                break;
            case "dialog_list":
                initDialogListDialog();
                break;
            default:
                initNoticeDialog(); // 默认为notice类型
        }
    }

    private void initNoticeDialog() {
        // 添加确定按钮
        if (dialog.has("action")) {
            JsonObject action = dialog.getAsJsonObject("action");
            String label = extractText(action.get("label"));

            // 如果没有标签，使用默认翻译
            if (label == null || label.isEmpty()) {
                label = LanguageManager.getClientTranslation("umoiftng.dialog.button.ok");
            }

            Button button = Button.builder(Component.literal(label), btn -> {
                        handleAction(action.getAsJsonObject("action"));
                        this.onClose();
                    })
                    .bounds(leftPos + dialogWidth / 2 - 75, topPos + dialogHeight - 40, 150, 20)
                    .build();

            this.addRenderableWidget(button);
            buttons.add(button);
        }
    }

    private void initConfirmationDialog() {
        if (dialog.has("yes") && dialog.has("no")) {
            JsonObject yesAction = dialog.getAsJsonObject("yes");
            JsonObject noAction = dialog.getAsJsonObject("no");

            String yesLabel = extractText(yesAction.get("label"));
            String noLabel = extractText(noAction.get("label"));

            // 如果没有标签，使用默认翻译
            if (yesLabel == null || yesLabel.isEmpty()) {
                yesLabel = LanguageManager.getClientTranslation("umoiftng.dialog.button.yes");
            }
            if (noLabel == null || noLabel.isEmpty()) {
                noLabel = LanguageManager.getClientTranslation("umoiftng.dialog.button.no");
            }

            // 是按钮
            Button yesButton = Button.builder(Component.literal(yesLabel), btn -> {
                        handleAction(yesAction.getAsJsonObject("action"));
                        this.onClose();
                    })
                    .bounds(leftPos + dialogWidth / 2 - 155, topPos + dialogHeight - 40, 150, 20)
                    .build();

            // 否按钮
            Button noButton = Button.builder(Component.literal(noLabel), btn -> {
                        handleAction(noAction.getAsJsonObject("action"));
                        this.onClose();
                    })
                    .bounds(leftPos + dialogWidth / 2 + 5, topPos + dialogHeight - 40, 150, 20)
                    .build();

            this.addRenderableWidget(yesButton);
            this.addRenderableWidget(noButton);
            buttons.add(yesButton);
            buttons.add(noButton);
        }
    }

    private void initDialogListDialog() {
        if (dialog.has("dialogs")) {
            JsonArray dialogs = dialog.getAsJsonArray("dialogs");
            int buttonY = topPos + 60;

            for (int i = 0; i < dialogs.size(); i++) {
                JsonObject dialogItem = dialogs.get(i).getAsJsonObject();
                String label = extractText(dialogItem.get("label"));

                // 如果没有标签，使用默认翻译
                if (label == null || label.isEmpty()) {
                    label = LanguageManager.getClientTranslation("umoiftng.gui.dialog.select") + " " + (i + 1);
                }

                Button dialogButton = Button.builder(Component.literal(label), btn -> {
                            handleAction(dialogItem.getAsJsonObject("action"));
                            this.onClose();
                        })
                        .bounds(leftPos + 60, buttonY, dialogWidth - 120, 20)
                        .build();

                this.addRenderableWidget(dialogButton);
                buttons.add(dialogButton);
                buttonY += 25;
            }
        }
    }

    private String extractText(com.google.gson.JsonElement element) {
        if (element.isJsonPrimitive()) {
            return element.getAsString();
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            if (obj.has("text")) {
                return obj.get("text").getAsString();
            }
        }
        return LanguageManager.getClientTranslation("umoiftng.dialog.button.ok");
    }

    private void handleAction(JsonObject action) {
        String actionType = action.get("type").getAsString();
        ClientDialogHandler.sendOfficialDialogAction(dialogId, actionType, action);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 渲染半透明背景 - 修复了参数错误
        this.renderBackground(guiGraphics);

        // 渲染对话框背景 - 使用更现代化的UI设计
        renderDialogBackground(guiGraphics);

        // 渲染标题
        renderTitle(guiGraphics);

        // 渲染正文内容
        renderBody(guiGraphics);

        // 渲染按钮
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private void renderDialogBackground(GuiGraphics guiGraphics) {
        // 绘制对话框背景 - 使用半透明效果，去除黑色边框
        int backgroundColor = 0xE0101010; // 半透明黑色背景
        int borderColor = 0x80333333; // 半透明边框

        // 主背景
        guiGraphics.fill(leftPos, topPos, leftPos + dialogWidth, topPos + dialogHeight, backgroundColor);

        // 轻微边框效果
        guiGraphics.fill(leftPos - 1, topPos - 1, leftPos + dialogWidth + 1, topPos, borderColor); // 上边框
        guiGraphics.fill(leftPos - 1, topPos + dialogHeight, leftPos + dialogWidth + 1, topPos + dialogHeight + 1, borderColor); // 下边框
        guiGraphics.fill(leftPos - 1, topPos, leftPos, topPos + dialogHeight, borderColor); // 左边框
        guiGraphics.fill(leftPos + dialogWidth, topPos, leftPos + dialogWidth + 1, topPos + dialogHeight, borderColor); // 右边框

        // 绘制标题栏 - 使用渐变效果
        int titleColor1 = 0xFF2D2D2D;
        int titleColor2 = 0xFF3D3D3D;

        // 简单渐变效果
        for (int i = 0; i < 30; i++) {
            int color = interpolateColor(titleColor1, titleColor2, i / 30.0f);
            guiGraphics.fill(leftPos, topPos + i, leftPos + dialogWidth, topPos + i + 1, color);
        }

        // 标题栏底部边框
        guiGraphics.hLine(leftPos, leftPos + dialogWidth - 1, topPos + 30, 0xFF555555);
    }

    private int interpolateColor(int color1, int color2, float ratio) {
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int r = (int)(r1 + (r2 - r1) * ratio);
        int g = (int)(g1 + (g2 - g1) * ratio);
        int b = (int)(b1 + (b2 - b1) * ratio);

        return (r << 16) | (g << 8) | b;
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        if (dialog.has("title")) {
            String title = extractText(dialog.get("title"));
            guiGraphics.drawCenteredString(this.font, title,
                    leftPos + dialogWidth / 2, topPos + 10, 0xFFFFFF);
        }
    }

    private void renderBody(GuiGraphics guiGraphics) {
        if (dialog.has("body")) {
            JsonArray body = dialog.getAsJsonArray("body");
            int yPos = topPos + 50;

            for (int i = 0; i < body.size(); i++) {
                JsonObject element = body.get(i).getAsJsonObject();
                String type = element.get("type").getAsString();

                if ("plain_message".equals(type) && element.has("contents")) {
                    String content = extractText(element.get("contents"));
                    renderText(guiGraphics, content, yPos);
                    yPos += calculateTextHeight(content) + 10;
                }
            }
        }
    }

    private void renderText(GuiGraphics guiGraphics, String text, int yPos) {
        List<FormattedCharSequence> lines = this.font.split(Component.literal(text), dialogWidth - 40);

        for (int i = 0; i < lines.size(); i++) {
            guiGraphics.drawString(this.font, lines.get(i),
                    leftPos + 20, yPos + i * 10, 0xCCCCCC, false);
        }
    }

    private int calculateTextHeight(String text) {
        List<FormattedCharSequence> lines = this.font.split(Component.literal(text), dialogWidth - 40);
        return lines.size() * 10;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        boolean canCloseWithEscape = !dialog.has("can_close_with_escape") ||
                dialog.get("can_close_with_escape").getAsBoolean();

        if (keyCode == GLFW.GLFW_KEY_ESCAPE && canCloseWithEscape) {
            this.onClose();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return !dialog.has("pause") || dialog.get("pause").getAsBoolean();
    }
}