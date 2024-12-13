package net.runelite.client.plugins.preventsgm;

import com.google.inject.Inject;
import java.util.Arrays;
import javax.annotation.Nullable;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
    name = "Prevent Superglass Make",
    description = "Prevents casting superglass make if you do not "
            + "have exactly 18 buckets of sand and 3 giant seaweed.")
public class PreventSGMPlugin extends Plugin {
  @Inject private Client client;

  private Widget superGlassMake;

  // Gotta love magic numbers!
  // These numbers were found with print debugging
  private static final int WITHDRAW = 786445;
  private static final int DEPOSIT_ALL = 786476;
  private static final int DEPOSIT = 983043;
  private static final int MAKE = 14286969;
  // These numbers were found with the widget inspector
  private static final int[] magicNumbers = {14286849, 14286851, 14286969};

  private int amountOfSeaweed = 0;
  private int amountOfSand = 0;

  public PreventSGMPlugin(Client client) {
    this.client = client;
  }

  @Override
  protected void startUp() throws Exception {
    amountOfSand = 0;
    amountOfSeaweed = 0;
  }

  @Override
  protected void shutDown() throws Exception {
    amountOfSand = 0;
    amountOfSeaweed = 0;
    enable(superGlassMake);
  }

  @Subscribe
  public void onClientTick(ClientTick event) {
    superGlassMake = extractSuperglassMake(client);
    if (superGlassMake == null || superGlassMake.isHidden()) {
      return;
    }
    changeSuperglassMake(superGlassMake);
  }

  /*
   * This is a pretty silly way of doing this, but
   * onMenuOptionClicked gives the fastest response
   * and so has to be used if you want it to work
   * being tick perfect (that is to say, pressing
   * the superglass make spell before your inventory
   * has updated)
   */
  @Subscribe
  public void onMenuOptionClicked(MenuOptionClicked event) {
    switch (event.getParam1()) {
      case WITHDRAW:
        updateInventory(event.getItemId());
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

  private void updateInventory(int itemID) {
    if (itemID == ItemID.GIANT_SEAWEED) {
      System.out.println("clicked seaweed");
      amountOfSeaweed += 1;
    } else if (itemID == ItemID.BUCKET_OF_SAND) {
      System.out.println("clicked sand");
      amountOfSand += 18;
    } else {
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
    Widget[] nextStep =
        Arrays.stream(widgets)
            .filter(widget -> widget.getId() == magicNumber)
            .toArray(Widget[]::new);
    if (magicNumber == magicNumbers[magicNumbers.length - 1]) {
      return nextStep;
    }
    return nextStep[0].getStaticChildren();
  }
}
