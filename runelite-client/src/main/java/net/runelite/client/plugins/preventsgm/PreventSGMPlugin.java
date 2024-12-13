package net.runelite.client.plugins.preventsgm;

import com.google.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetID;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.junkfilter.JunkFilterOverlay;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@PluginDescriptor(
        name = "Prevent Superglass Make",
        description = "Prevents casting superglass make if you do not " +
                      "have exactly 18 buckets of sand and 3 giant seaweed."
)
public class PreventSGMPlugin extends Plugin {
    @Inject
    private Client client;

    private Widget superGlassMake;

    private final static int WITHDRAW = 786445;
    private final static int DEPOSIT_ALL = 786476;
    private final static int DEPOSIT = 983043;
    private final static int MAKE = 14286969;

    private int amountOfSeaweed = 0;
    private int amountOfSand = 0;

    public PreventSGMPlugin() {

    }

    public PreventSGMPlugin(Client client) {
        this.client = client;
    }

    @Override
    protected void startUp() throws Exception
    {
        System.out.println("PreventSGM Plugin Enabled");
    }

    @Override
    protected void shutDown() throws Exception
    {
        System.out.println("PreventSGM Plugin Disabled");
    }

    @Subscribe
    public void onClientTick(ClientTick event) {
        superGlassMake = extractSuperglassMake(client);
        if (superGlassMake == null || superGlassMake.isHidden()) {
            return;
        }
        changeSuperglassMake(superGlassMake);
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked event) {
        //I do it like this because this makes the plugin very responsive to clicks, which means
        //there is no delay if you click very fast
        switch (event.getParam1()) {
            case WITHDRAW:
                updatePredictedInventory(event.getItemId());
                break;
            case DEPOSIT:
                if (event.getItemId() == ItemID.GIANT_SEAWEED) {
                    amountOfSeaweed -= 1;
                } else if (event.getItemId() == ItemID.BUCKET_OF_SAND) {
                    amountOfSand = 0;
                }
                break;
            case DEPOSIT_ALL:
                amountOfSand = 0;
                amountOfSeaweed = 0;
            case MAKE:
                amountOfSand = 0;
                amountOfSeaweed = 0;
                break;
            default:
                return;
        }
    }

    private void updatePredictedInventory(int itemID) {
        if (itemID == ItemID.GIANT_SEAWEED) {
            System.out.println("clicked seaweed");
            amountOfSeaweed += 1;
        }
        else if (itemID == ItemID.BUCKET_OF_SAND) {
            System.out.println("clicked sand");
            amountOfSand += 18;
        }
        else {
            return;
        }
        changeSuperglassMake(superGlassMake);
    }

    private boolean checkInventory() {
        return amountOfSeaweed == 3 && amountOfSand == 18;
    }

    private void changeSuperglassMake(Widget widget) {
        if (checkInventory()) {
            enable(widget);
        } else {
            disable(widget);
        }
    }

    private void enable(Widget widget) {
        widget.setOpacity(0);
        widget.setAction(0, "Superglass Make");
    }

    private void disable(Widget widget) {
        widget.setOpacity(128);
        widget.setAction(0, "");
    }

    @Nullable
    private Widget extractSuperglassMake(Client client) {
        int[] magicNumbers = {14286849, 14286851, 14286969}; //These numbers were found with the widget inspector
        if (client.getWidget(ComponentID.SPELLBOOK_PARENT) == null) {
            return null;
        }
        Widget[] result = client.getWidget(ComponentID.SPELLBOOK_PARENT).getStaticChildren();
        for (int magicNumber : magicNumbers) {
            result = extractOneStep(result, magicNumber);
        }
        return result[0];
    }

    private Widget[] extractOneStep(Widget[] widgets, int magicNumber) {
        Widget[] nextStep = Arrays.stream(widgets).filter(widget -> widget.getId() == magicNumber)
                .toArray(Widget[]::new);
        if (magicNumber == 14286969) {
            return nextStep;
        }
        return nextStep[0].getStaticChildren();
    }
}