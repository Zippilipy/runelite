package net.runelite.client.plugins.junkfilter.bank;

import lombok.Data;
import net.runelite.api.Item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
public class JunkBankData {
    int[] idAndQuantity;

    JunkBankData()
    {
        idAndQuantity = new int[0];
    }
    void set(List<Item> items)
    {
        int[] newIdAndQuantity = new int[(items.size() + 1) * 2];
        for (int i = 0; i < items.size(); i++)
        {
            Item item = items.get(i);
            newIdAndQuantity[i*2] = item.getId();
            newIdAndQuantity[(i*2)+1] = item.getQuantity();
        }
        idAndQuantity = newIdAndQuantity;
    }

    void set(Item[] items)
    {
        set(Arrays.asList(items));
    }

    void setEmpty()
    {
        idAndQuantity = new int[0];
    }


    List<Item> getAsList()
    {
        List<Item> items = new ArrayList<>();

        if (idAndQuantity == null) return items;

        for (int i = 0; i < idAndQuantity.length - 2; i += 2)
        {
            items.add(new Item(idAndQuantity[i], idAndQuantity[i+1]));
        }
        return items;
    }

}
