package net.runelite.client.plugins.junkfilter;

import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.WidgetClosed;
import net.runelite.api.events.WidgetLoaded;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.InterfaceID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.runelite.api.ItemID;

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

    public JunkFilterPlugin() {
        junkFilterOverlay = new JunkFilterOverlay();
    }

    @Override
    protected void startUp() throws Exception
    {
        allowedItems.add(ItemID.COINS_995);
        allowedItems.add(ItemID.GOLD_BAR);
        overlayManager.add(junkFilterOverlay);
        System.out.println("Junk Filter Plugin Enabled");
    }

    @Override
    protected void shutDown() throws Exception
    {
        System.out.println("Junk Filter Plugin Disabled");
    }

    private void updateBankDisplay(List<Item> items) {
        Widget bankItemContainer = client.getWidget(ComponentID.BANK_ITEM_CONTAINER);
        if (bankItemContainer == null || bankItemContainer.getChildren() == null) {
            return;
        }
        Widget[] bankItems = bankItemContainer.getChildren();
        int type = bankItems[0].getType();
        int id = bankItems[0].getItemQuantity();
        for(int i = 0; i < bankItems.length; i++) {
            bankItemContainer.setChildren(bankItems);
        }
        System.out.println("Bank contains " + bankItems.length + " items.");
        //List<Widget> children = new ArrayList<>();
        for (Widget bankItemWidget : bankItems) {
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        if (event.getContainerId() !=  InventoryID.BANK.getId()) {
            return;
        }
        ItemContainer bank = event.getItemContainer();
        List<Item> items = Arrays.asList(bank.getItems());
        List<Item> allowedItems = allowedItems(items);
        System.out.println(allowedItems);
        updateBankDisplay(allowedItems);
    }

    @Subscribe
    public void onWidgetClosed(WidgetClosed event)
    {
        if (event.getGroupId() == InterfaceID.BANK_INVENTORY)
        {
            junkFilterOverlay.clear();
        }
    }

    private List<Item> allowedItems(List<Item> items) {
        return intersection(items, allowedItems);
    }

    private List<Item> intersection(List<Item> items, List<Integer> itemIds) {
        return items.stream()
                .distinct()
                .filter(item -> itemIds.contains(item.getId()))
                .collect(Collectors.toList());
    }
}
