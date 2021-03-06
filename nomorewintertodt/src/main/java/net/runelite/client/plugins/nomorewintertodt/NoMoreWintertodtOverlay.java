/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
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
package net.runelite.client.plugins.nomorewintertodt;

import java.awt.*;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;

public class NoMoreWintertodtOverlay extends Overlay
{

	private final Client client;
	private final NoMoreWintertodtPlugin plugin;
	private final NoMoreWintertodtConfig config;

	@Inject
	NoMoreWintertodtOverlay(final Client client, final NoMoreWintertodtPlugin plugin, final NoMoreWintertodtConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	private boolean gameActive;

	@Override
	public Dimension render(Graphics2D graphics)
	{
		LocalPoint playerLocation = client.getLocalPlayer().getLocalLocation();
		Point mousePosition = client.getMouseCanvasPosition();

		if (plugin.isMinigameActive()) {
			gameActive = true;
			// Responsible for checks on the Pyromancer animations.
			for (NPC pyromancer : plugin.getNpcs()) {

				if (pyromancer.getAnimation() == 7627) {

					WorldPoint pos = pyromancer.getWorldLocation();

					if (pos.getPlane() == client.getPlane()) {
						Shape npcConvexHull = pyromancer.getConvexHull();

						if (npcConvexHull != null) {

							switch (config.locationSide()) {
								case EAST:
									if (pos.getX() > 1630 && pos.getY() < 4000)
										renderStyleChoice(graphics, npcConvexHull, config.pyroColor(), config.pyroBoxSize());
									break;
								case WEAST:
									if (pos.getX() < 1630 && pos.getY() < 4000)
										renderStyleChoice(graphics, npcConvexHull, config.pyroColor(), config.pyroBoxSize());
									break;
							}

						}

					}

					return null;
				}

			}

			if (config.displayMinigameStatus()) {
				graphics.setColor(Color.GREEN);
				graphics.fillRect(0,0,5,5);
			}

			plugin.getObjects().forEach((object, obstacle) ->
			{
				Tile tile = obstacle.getTile();

				if (tile.getPlane() == client.getPlane()) {
					Shape objectClickBox = object.getClickbox();
					if (objectClickBox != null) {

						switch (config.locationSide()) {
							case EAST:
								if (object.getWorldLocation().getX() > 1630 && object.getWorldLocation().getY() < 4000)
									renderObjects(graphics, object, objectClickBox);
								break;
							case WEAST:
								if (object.getWorldLocation().getX() < 1630 && object.getWorldLocation().getY() < 4000)
									renderObjects(graphics, object, objectClickBox);
								break;
						}

					}
				}

			});
		}
		if (!plugin.isMinigameActive()) {
			gameActive = false;
			if (config.displayMinigameStatus()) {
				graphics.setColor(Color.RED);
				graphics.fillRect(0, 0, 5, 5);
			}
		}

		// Responsible for the Wintertodt widgets manipulation.
		Widget widget = client.getWidget(396,2);
		if (widget != null) {
			switch (config.wintertotdHUD()) {
				case VISIBLE:
					widget.setHidden(false);
					break;
				case HIDDEN:
					widget.setHidden(true);
					break;
				case MIXED:
					widget.setHidden(gameActive);
					break;
			}
		}
		return null;
	}

	public void renderObjects(Graphics2D graphics, TileObject object, Shape objectClickBox) {
		if (config.displayBruma()) {
			if (object.getId() == ObjectID.BRUMA_ROOTS)
				renderStyleChoice(graphics, objectClickBox, config.brumaColor(), config.brumaBoxSize());
		}
		if (config.displayLitBrazier()) {
			if (object.getId() == ObjectID.BURNING_BRAZIER_29314)
				renderStyleChoice(graphics, objectClickBox, config.litBrazierColor(), config.litBrazierBoxSize());
		}
		if (config.displayUnlitBrazier()) {
			if (object.getId() == ObjectID.BRAZIER_29312)
				renderStyleChoice(graphics, objectClickBox, config.unlitBrazierColor(), config.unlitBrazierBoxSize());
		}
		if (config.displayBrokenBrazier()) {
			if (object.getId() == ObjectID.BRAZIER_29313)
				renderStyleChoice(graphics, objectClickBox, config.brokenBrazierColor(), config.brokenBrazierBoxSize());
		}
	}

	public void renderStyleChoice(Graphics2D graphics, Shape shapeClickBox, Color color, int boxSize) {

		switch (config.renderStyle()) {
			case CLICKBOX:
				renderClickBox(graphics, shapeClickBox, color);
				break;
			case FILL:
				renderFilledClickBox(graphics, shapeClickBox, color);
				break;
			case BOX:
				renderBox(graphics, shapeClickBox, color, boxSize);
				break;
		}

	}

	public void renderClickBox(Graphics2D graphics, Shape shapeClickBox, Color color) {

		if (shapeClickBox != null) {
			OverlayUtil.renderPolygon(graphics, shapeClickBox, color);
		}

	}

	public void renderFilledClickBox(Graphics2D graphics, Shape shapeClickBox, Color color) {

		if (shapeClickBox != null) {
			OverlayUtil.renderFilledPolygon(graphics, shapeClickBox, color);
		}

	}

	public void renderBox(Graphics2D graphics, Shape shapeClickBox, Color color, int boxSize) {

		if (shapeClickBox != null) {
			int x = (int) shapeClickBox.getBounds().getCenterX() - boxSize / 2;
			int y = (int) shapeClickBox.getBounds().getCenterY() - boxSize / 2;
			graphics.setColor(color);
			graphics.fillRect(x, y, boxSize, boxSize);
		}

	}
}
