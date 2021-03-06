/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.amiscplugin;

import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.PluginType;
import net.runelite.client.ui.overlay.OverlayManager;
import org.pf4j.Extension;

@Extension
@PluginDescriptor(
	name = "NoMore Misc Plugins",
	description = "Numerous miscellaneous plugins.",
	tags = {"ahk", "script", "mouse", "logging", "nomoreahk", "misc"},
	type = PluginType.MISCELLANEOUS
)
@Slf4j
public class AMiscPlugin extends Plugin {

	@Inject
	private Client client;

	@Inject
	private FileManager fileManager;

	@Inject
	private MouseLog mouseLog;

	@Inject
	private AMiscConfig config;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private AMiscOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Provides
	AMiscConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(AMiscConfig.class);
	}

	@Override
	protected void startUp() {
		fileManager.createStructure();
		mouseManager.registerMouseListener(mouseLog);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() {
		mouseManager.unregisterMouseListener(mouseLog);
		overlayManager.remove(overlay);
	}
}