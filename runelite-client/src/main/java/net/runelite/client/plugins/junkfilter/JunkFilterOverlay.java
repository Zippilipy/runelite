package net.runelite.client.plugins.junkfilter;

import net.runelite.api.Item;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class JunkFilterOverlay extends Overlay
{
    private List<Integer> filteredItems = new ArrayList<>();

    public JunkFilterOverlay()
    {
        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (filteredItems != null && !filteredItems.isEmpty())
        {
            int y = 10;
            for (int id : filteredItems)
            {
                graphics.drawString(String.valueOf(id), 10, y);
                y += 15;
            }
        }
        return null;
    }

    public void setFilteredItems(List<Integer> filteredItems)
    {
        this.filteredItems = filteredItems;
    }

    public void clear() {
        filteredItems.clear();
    }
}
