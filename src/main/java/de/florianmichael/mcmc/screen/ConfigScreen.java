/*
 * This file is part of BaseProject - https://github.com/FlorianMichael/BaseProject
 * Copyright (C) 2024 FlorianMichael/EnZaXD <florian.michael07@gmail.com> and contributors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.florianmichael.mcmc.screen;

import de.florianmichael.mcmc.MyConnectionMyChoice;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Pair;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class ConfigScreen extends Screen {

    private static final int BUTTON_WIDTH = 250;
    private static final int PADDING = 3;
    private static final Map<String, Pair<Supplier<Boolean>, Consumer<Boolean>>> settings = new HashMap<>();

    static {
        final MyConnectionMyChoice instance = MyConnectionMyChoice.instance();
        settings.put("keepConnectionInConfirmScreen", new Pair<>(instance::keepConnectionInConfirmScreen, instance::setKeepConnectionInConfirmScreen));
        settings.put("hideTransferConnectionIntent", new Pair<>(instance::hideTransferConnectionIntent, instance::setHideTransferConnectionIntent));
        settings.put("clearCookiesOnTransfer", new Pair<>(instance::clearCookiesOnTransfer, instance::setClearCookiesOnTransfer));
    }

    private final Screen parent;

    public ConfigScreen(final Screen parent) {
        super(Text.empty());
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();

        int y = 50;
        for (Map.Entry<String, Pair<Supplier<Boolean>, Consumer<Boolean>>> entry : settings.entrySet()) {
            final String key = entry.getKey().toLowerCase(Locale.ROOT);

            final Text label = Text.translatable("settings.mcmc." + key);
            final Text description = Text.translatable("settings.mcmc." + key + ".description");

            final Supplier<Boolean> supplier = entry.getValue().getLeft();
            addDrawableChild(ButtonWidget
                    .builder(
                            getButtonText(label, supplier.get()),
                            button -> {
                                entry.getValue().getRight().accept(!supplier.get());
                                button.setMessage(getButtonText(label, supplier.get()));
                            }
                    )
                    .position(this.width / 2 - BUTTON_WIDTH / 2, y)
                    .size(BUTTON_WIDTH, ButtonWidget.DEFAULT_HEIGHT)
                    .tooltip(Tooltip.of(description))
                    .build());

            y += ButtonWidget.DEFAULT_HEIGHT + PADDING;
        }

        addDrawableChild(ButtonWidget
                .builder(Text.of("<-"), button -> client.setScreen(parent))
                .position(PADDING, this.height - ButtonWidget.DEFAULT_HEIGHT - PADDING)
                .size(ButtonWidget.DEFAULT_HEIGHT, ButtonWidget.DEFAULT_HEIGHT)
                .build());
    }

    private Text getButtonText(final Text label, final boolean value) {
        final Text onText = Text.translatable("base.mcmc.on");
        final Text offText = Text.translatable("base.mcmc.off");

        return Text
                .literal("")
                .append(label)
                .append(": ")
                .append(Text.literal("")
                        .append(value ? onText : offText)
                        .formatted(value ? Formatting.GREEN : Formatting.RED)
                );
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);

        final Text title = Text.translatable("settings.mcmc.title");

        final MatrixStack matrices = context.getMatrices();
        matrices.push();
        matrices.scale(2.0F, 2.0F, 2.0F);
        context.drawText(client.textRenderer, title, this.width / 4 - client.textRenderer.getWidth(title) / 2, 5, -1, true);
        matrices.pop();
    }

}