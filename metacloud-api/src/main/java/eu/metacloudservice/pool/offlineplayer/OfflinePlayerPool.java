package eu.metacloudservice.pool.offlineplayer;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.cloudplayer.offlineplayer.ceched.OfflinePlayerCacheConfiguration;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.pool.offlineplayer.entrys.OfflinePlayer;
import eu.metacloudservice.webserver.interfaces.IRest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OfflinePlayerPool{


    public OfflinePlayerPool() {}

    public ArrayList<OfflinePlayer> getAllOfflinePlayers(){
        ArrayList<OfflinePlayer> players = new ArrayList<>();

        OfflinePlayerCacheConfiguration configuration = (OfflinePlayerCacheConfiguration) new ConfigDriver().convert(CloudAPI.getInstance().getRestDriver().get("/cloudplayer/offlinecache"), OfflinePlayerCacheConfiguration.class);
        configuration.getPlayerCaches().forEach(cache -> {
            players.add(new OfflinePlayer(cache.getUsername(), cache.getUniqueID(), cache.getFirstConnected(), cache.getLastConnected(),cache.getLastProxy(),cache.getLastService()));
        });
        return players;
    }

    public OfflinePlayer getOfflinePlayer(String name){
        return getAllOfflinePlayers().stream().filter(offlinePlayer -> offlinePlayer.getUsername().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public OfflinePlayer getOfflinePlayer(UUID uniqueID){
      return   getAllOfflinePlayers().stream().filter(offlinePlayer -> offlinePlayer.getUniqueID().equalsIgnoreCase(uniqueID.toString())).findFirst().orElse(null);
    }

    public List<OfflinePlayer> getOfflinePlayerFromProxy(String proxy){
        return getAllOfflinePlayers().stream().filter(offlinePlayer -> offlinePlayer.getLastProxy().equalsIgnoreCase(proxy)).toList();
    }

    public List<OfflinePlayer> getOfflinePlayerFromService(String service){
        return getAllOfflinePlayers().stream().filter(offlinePlayer -> offlinePlayer.getLastService().equalsIgnoreCase(service)).toList();
    }
}
