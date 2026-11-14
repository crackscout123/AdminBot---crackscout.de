package de.crackscout.Collectors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;

import de.crackscout.Managers.ConfigManager;
import de.crackscout.Managers.Debug;
import de.crackscout.Managers.Utils;

public class WasserCollector implements Runnable {

    private final TS3Api api;

    public static final Map<Integer, Long> joinTimes = new ConcurrentHashMap<>();
    public static final Map<Integer, Long> leaveTimes = new ConcurrentHashMap<>();
    public static final Set<Integer> notifiedUsers = ConcurrentHashMap.newKeySet();

    public static ArrayList<Integer> whitelistedUsers = Utils.whitelistedUsers;

    public static String msgNotify = ConfigManager.loadProp("wasser.notify.msg", "messages.properties");

    public static int[] selectedChannels = ConfigManager.loadIntArray("wasser.selected.channels", "config.properties");
    public static int[] ignoredGroups = ConfigManager.loadIntArray("wasser.ignored.groups", "config.properties");

    public static int sleepTime = ConfigManager.loadInt("wasser.sleep.ms", "config.properties");
    public static int maxIdleTime = ConfigManager.loadInt("wasser.max.idle.ms", "config.properties");
    public static int notifyAfterSeconds = ConfigManager.loadInt("wasser.notify.seconds", "config.properties");

    static {
        if (notifyAfterSeconds <= 0) {
            notifyAfterSeconds = ConfigManager.loadInt("wasser.max.idle.s", "config.properties");
        }
    }

    public static boolean pokeEnabled = ConfigManager.loadBool("wasser.poke.enabled", "config.properties");

    public WasserCollector(TS3Api api) {
        this.api = api;
    }

    public static boolean isSelectedChannel(int channelId) {
        return Arrays.stream(selectedChannels).anyMatch(c -> c == channelId);
    }

    public static void onClientJoined(int clientId, int channelId) {
        if (!isSelectedChannel(channelId)) {
            return;
        }

        long now = System.currentTimeMillis();
        Long lastLeave = leaveTimes.get(clientId);
        Long start = joinTimes.get(clientId);

        if (start == null) {
            joinTimes.put(clientId, now);
            notifiedUsers.remove(clientId);
            return;
        }

        if (lastLeave != null && (now - lastLeave) <= maxIdleTime) {
            return;
        }

        joinTimes.put(clientId, now);
        notifiedUsers.remove(clientId);
    }

    public static void onClientMoved(int clientId, int channelId) {
        if (isSelectedChannel(channelId)) {
            onClientJoined(clientId, channelId);
            return;
        }

        onClientLeft(clientId);
    }

    public static void onClientLeft(int clientId) {
        leaveTimes.put(clientId, System.currentTimeMillis());
        notifiedUsers.remove(clientId);
    }

    public static long getSecondsInChannel(int clientId) {
        Long start = joinTimes.get(clientId);
        if (start == null) return 0L;
        return (System.currentTimeMillis() - start) / 1000L;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Debug.info("WasserCollector: Shutdown signal received, stopping.");
                return;
            }

            List<Client> clients = api.getClients();
            Debug.info("Trying to notify and evaluate clients...");

            for (Client client : clients) {
                if (whitelistedUsers.contains(client.getId())) {
                    continue;
                }

                if (!isSelectedChannel(client.getChannelId())) {
                    onClientLeft(client.getId());
                    continue;
                }

                if (!joinTimes.containsKey(client.getId())) {
                    onClientJoined(client.getId(), client.getChannelId());
                }

                long elapsedSeconds = getSecondsInChannel(client.getId());
                if (elapsedSeconds >= notifyAfterSeconds && notifiedUsers.add(client.getId())) {
                    notifyClient(client);
                }
            }
        }
    }

    public void notifyClient(Client client) {
        try {
            if (client.getId() == api.whoAmI().getId()) {
                return;
            }

            if (Arrays.stream(client.getServerGroups()).anyMatch(g -> Arrays.stream(ignoredGroups).anyMatch(ignored -> ignored == g))) {
                return;
            }

            if (pokeEnabled) {
                api.pokeClient(client.getId(), msgNotify);
            } else {
                api.sendPrivateMessage(client.getId(), msgNotify);
            }
        } catch (Exception e) {
            Debug.err("Failed to fetch client details, dumping error info: " + e.getMessage());
        }
    }
}

/**	
 * @date 27.03.2023 - 13:37:06
 *
 */