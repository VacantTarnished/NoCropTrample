package com.murqin.nocroptrample;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.murqin.nocroptrample.config.ModConfig;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.NonNull;

/**
 * Mod Menu integration for NoCropTrample configuration screen.
 */
public class ModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return NoCropTrampleConfigScreen::new;
    }

    /**
     * Configuration screen for the NoCropTrample mod.
     * <p>
     * Provides a simple GUI to toggle player and mob trampling prevention.
     * </p>
     */
    public static class NoCropTrampleConfigScreen extends Screen {
        private final Screen parent;

        public NoCropTrampleConfigScreen(Screen parent) {
            super(Component.literal("NoCropTrample Config"));
            this.parent = parent;
        }

        @Override
        protected void init() {
            int centerX = this.width / 2;
            int startY = this.height / 4;

            Button emptyButton = Button.builder(
                    getEmptyButtonText(),
                    this::toggleEmptyTrampling)
                    .bounds(centerX - 100, startY, 200, 20)
                    .build();
            this.addRenderableWidget(emptyButton);

            Button playerButton = Button.builder(
                    getPlayerButtonText(),
                    this::togglePlayerTrampling)
                    .bounds(centerX - 100, startY + 25, 200, 20)
                    .build();
            this.addRenderableWidget(playerButton);

            Button mobButton = Button.builder(
                            getMobButtonText(),
                            this::toggleMobTrampling)
                    .bounds(centerX - 100, startY + 50, 200, 20)
                    .build();
            this.addRenderableWidget(mobButton);

            this.addRenderableWidget(Button.builder(
                Component.translatable("gui.done"),
                button -> this.onClose())
                .bounds(centerX - 100, startY + 95, 200, 20)
                .build());
        }

        /**
         * Toggles the player trampling prevention setting.
         *
         * @param button the button that was clicked
         */
        private void toggleEmptyTrampling(Button button) {
            ModConfig.setPreventPlayerTrampling(!ModConfig.isPreventPlayerTrampling());
            button.setMessage(getEmptyButtonText());
        }

        /**
         * Toggles the player trampling prevention setting.
         *
         * @param button the button that was clicked
         */
        private void togglePlayerTrampling(Button button) {
            ModConfig.setPreventPlayerTrampling(!ModConfig.isPreventPlayerTrampling());
            button.setMessage(getPlayerButtonText());
        }

        /**
         * Toggles the mob trampling prevention setting.
         *
         * @param button the button that was clicked
         */
        private void toggleMobTrampling(Button button) {
            ModConfig.setPreventMobTrampling(!ModConfig.isPreventMobTrampling());
            button.setMessage(getMobButtonText());
        }

        /**
         * Gets the display text for the player trampling button.
         *
         * @return formatted component with current state
         */
        private @NonNull Component getPlayerButtonText() {
            return Component.literal("Player Trampling: ")
                    .append(ModConfig.isPreventPlayerTrampling()
                            ? Component.literal("§aPrevented")
                            : Component.literal("§cAllowed"));
        }

        private @NonNull Component getEmptyButtonText() {
            return Component.literal("Empty Trampling: ")
                    .append(ModConfig.isPreventEmptyTrampling()
                        ? Component.literal("§aPrevented")
                        : Component.literal("§cAllowed"));
        }

        /**
         * Gets the display text for the mob trampling button.
         *
         * @return formatted component with current state
         */
        private @NonNull Component getMobButtonText() {
            return Component.literal("Mob Trampling: ")
                    .append(ModConfig.isPreventMobTrampling()
                            ? Component.literal("§aPrevented")
                            : Component.literal("§cAllowed"));
        }

        @Override
        public void render(@NonNull GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
            super.render(guiGraphics, mouseX, mouseY, delta);
            guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);
        }

        @Override
        public void onClose() {
            if (this.minecraft != null) {
                this.minecraft.setScreen(this.parent);
            }
        }
    }
}
