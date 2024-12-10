package net.runelite.client.plugins.junkfilter.bank;

import com.google.gson.Gson;
import com.google.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Item;
import net.runelite.client.config.ConfigManager;

import java.util.ArrayList;
import java.util.List;

public class JunkBank {

    private final ConfigManager configManager;
    private final Client client;
    private final Gson gson;

    private static final String BANK_KEY = "bankitems";

    private List<Item> bankItems;
    private final JunkBankData junkBankData;
    private String rsProfileKey;

    @Inject
    public JunkBank(Client client, ConfigManager configManager, Gson gson)
    {
        this.configManager = configManager;
        this.client = client;
        this.gson = gson;
        this.junkBankData = new JunkBankData();
        this.bankItems = new ArrayList<>();
    }

    public List<Item> getBankItems()
    {
        return bankItems;
    }

    public void updateLocalBank(Item[] items)
    {
        junkBankData.set(items);
        bankItems = junkBankData.getAsList();
    }

}
