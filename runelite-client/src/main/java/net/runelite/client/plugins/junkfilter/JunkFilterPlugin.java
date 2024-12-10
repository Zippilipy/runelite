package net.runelite.client.plugins.junkfilter;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.List;

@PluginDescriptor(
        name = "Junk Filter",
        description = "Filters out unwanted items from your inventory and bank."
)
public class JunkFilterPlugin extends Plugin
{

    private boolean isBankOpen = false;

    @Inject
    private Client client;
    @Inject
    private OverlayManager overlayManager;

    private final List<Integer> allowedItems = new ArrayList<>();
    private final JunkFilterOverlay junkFilterOverlay;
    private final List<Item> filteredItems = new ArrayList<>();

    public JunkFilterPlugin() {
        junkFilterOverlay = new JunkFilterOverlay();
    }

    @Override
    protected void startUp() throws Exception
    {
        // Runs when the plugin is enabled.
        allowedItems.add(995);
        overlayManager.add(junkFilterOverlay);
        System.out.println("Junk Filter Plugin Enabled");
    }

    @Override
    protected void shutDown() throws Exception
    {
        // Runs when the plugin is disabled.
        System.out.println("Junk Filter Plugin Disabled");
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() !=  InventoryID.BANK.getId()) {
            return;
        }
        ItemContainer bank = event.getItemContainer();
        filterItems(bank.getItems());
        junkFilterOverlay.setFilteredItems(filteredItems);

    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event)
    {
        if (event.getGroupId() == InterfaceID.BANK_INVENTORY)
        {
            junkFilterOverlay.clear();
        }
    }

    private void filterItems(Item[] items)
    {
        for (Item item : items)
        {
            if (isJunk(item))
            {
                filteredItems.add(item);
            }
        }
    }

    private boolean isJunk(Item item)
    {
        return allowedItems.contains(item.getId());
    }
}
