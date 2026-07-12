package net.ccc_ai_age.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import java.io.File;

/**
 * Custom configuration notice GUI screen shown to the player when joining a world.
 * Prompts them to enable automatic Ollama options and allows opening the config file directly on their desktop.
 */
public class ConfigNotificationScreen extends Screen {

	protected ConfigNotificationScreen() {
		super(Text.literal("Kinetic AI Core Configuration"));
	}

	@Override
	protected void init() {
		super.init();

		int btnWidth = 120;
		int btnHeight = 20;
		int spacing = 20;
		
		int startX = (this.width - (btnWidth * 2 + spacing)) / 2;
		int startY = this.height / 2 + 40;

		// Okay Button (Left)
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Okay"), button -> {
			this.client.setScreen(null);
		}).dimensions(startX, startY, btnWidth, btnHeight).build());

		// Open Config Button (Right)
		this.addDrawableChild(ButtonWidget.builder(Text.literal("Open Config"), button -> {
			File configFile = new File(this.client.runDirectory, "config/ccc-ai-age.json");
			if (configFile.exists()) {
				Util.getOperatingSystem().open(configFile);
			} else {
				File configDir = new File(this.client.runDirectory, "config");
				if (configDir.exists()) {
					Util.getOperatingSystem().open(configDir);
				}
			}
		}).dimensions(startX + btnWidth + spacing, startY, btnWidth, btnHeight).build());
	}

	@Override
	public void render(DrawContext context, int mouseX, int mouseY, float delta) {
		this.renderBackground(context);
		super.render(context, mouseX, mouseY, delta);

		int centerY = this.height / 2 - 50;

		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("§bCC:C AI Age - Configuration Notice"), this.width / 2, centerY, 0xFFFFFF);
		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("To enable automatic Ollama startup and model downloading,"), this.width / 2, centerY + 20, 0xCCCCCC);
		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("you need to enable them in the config file: config/ccc-ai-age.json."), this.width / 2, centerY + 32, 0xCCCCCC);
		context.drawCenteredTextWithShadow(this.textRenderer, Text.literal("Would you like to open the configuration file now?"), this.width / 2, centerY + 50, 0xCCCCCC);
	}

	@Override
	public void close() {
		this.client.setScreen(null);
	}
}
