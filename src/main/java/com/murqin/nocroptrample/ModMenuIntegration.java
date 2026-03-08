package com.murqin.nocroptrample;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.murqin.nocroptrample.config.ModConfig;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

// Modern implementation (1.20+) using DrawContext
public class ModMenuIntegration implements ModMenuApi {
    
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return NoCropTrampleConfigScreen::new;
    }
    
    public static class NoCropTrampleConfigScreen extends Screen {
        private final Screen parent;

        public NoCropTrampleConfigScreen(Screen parent) {
            super(Text.literal("NoCropTrample Config"));
            this.parent = parent;
        }
        
        @Override
        protected void init() {
            int centerX = this.width / 2;
            int startY = this.height / 4;

            ButtonWidget emptyButton = ButtonWidget.builder(
                            getEmptyButtonText(),
                            button -> {
                                ModConfig.preventEmptyTrampling = !ModConfig.preventEmptyTrampling;
                                ModConfig.save();
                                button.setMessage(getEmptyButtonText());
                            })
                    .dimensions(centerX - 100, startY, 200, 20)
                    .build();
            this.addDrawableChild(emptyButton);

            ButtonWidget playerButton = ButtonWidget.builder(
                            getPlayerButtonText(),
                            button -> {
                                ModConfig.preventPlayerTrampling = !ModConfig.preventPlayerTrampling;
                                ModConfig.save();
                                button.setMessage(getPlayerButtonText());
                            })
                    .dimensions(centerX - 100, startY + 25, 200, 20)
                    .build();
            this.addDrawableChild(playerButton);

            ButtonWidget mobButton = ButtonWidget.builder(
                            getMobButtonText(),
                            button -> {
                                ModConfig.preventMobTrampling = !ModConfig.preventMobTrampling;
                                ModConfig.save();
                                button.setMessage(getMobButtonText());
                            })
                    .dimensions(centerX - 100, startY + 50, 200, 20)
                    .build();
            this.addDrawableChild(mobButton);
            
            this.addDrawableChild(ButtonWidget.builder(
                    Text.translatable("gui.done"),
                    button -> this.close())
                .dimensions(centerX - 100, startY + 95, 200, 20)
                .build());
        }

        private Text getEmptyButtonText() {
            return Text.literal("Empty Trampling: ")
                    .append(ModConfig.preventEmptyTrampling
                        ? Text.literal("§aPrevented")
                        : Text.literal("§cAllowed"));
        }

        private Text getPlayerButtonText() {
            return Text.literal("Player Trampling: ")
                    .append(ModConfig.preventPlayerTrampling 
                        ? Text.literal("§aPrevented") 
                        : Text.literal("§cAllowed"));
        }
        
        private Text getMobButtonText() {
            return Text.literal("Mob Trampling: ")
                    .append(ModConfig.preventMobTrampling 
                        ? Text.literal("§aPrevented") 
                        : Text.literal("§cAllowed"));
        }
        
        @Override
        public void render(DrawContext context, int mouseX, int mouseY, float delta) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 20, 0xFFFFFF);
        }
        
        @Override
        public void close() {
            if (this.client != null) {
                this.client.setScreen(this.parent);
            }
        }
    }
}
