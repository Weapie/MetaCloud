package eu.metacloudservice.storage;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class UUIDDriver {

    private static ArrayList<UUIDStorage> uuids;
    public static String getUUID(String name) {
        if (uuids == null){
            uuids = new ArrayList<>();
        }
        if (uuids.stream().anyMatch(uuidStorage -> uuidStorage.getUsername().equalsIgnoreCase(name))){
            return uuids.stream().filter(uuidStorage -> uuidStorage.getUsername().equalsIgnoreCase(name)).findFirst().get().getUniqueID();
        } else {
            System.out.println(name);
            if(name.startsWith("!")) {
                String userName = name.replaceFirst("!", "");
                String geyserXid = "https://api.geysermc.org/v2/xbox/xuid/" + userName;

                try {
                    URL url = new URL(geyserXid);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject json = new JSONObject(builder.toString());
                    String uuid = "" + json.getLong("xuid");
                    uuids.add(new UUIDStorage(userName, uuid));

                    System.out.println(uuids.toString());
                    System.out.println(uuid);

                    reader.close();

                    return uuid;
                } catch (Exception e) {
                    return null;
                }
            }

            String urlString = "https://minecraft-api.com/api/uuid/" + name + "/json";
            try {
                URL url = new URL(urlString);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject json = new JSONObject(builder.toString());
                String uuid = json.getString("uuid");
                uuids.add(new UUIDStorage(name, uuid));

                reader.close();

                return uuid;
            } catch (Exception e1) {
                try {
                    URL url = new URL("https://api.minetools.eu/uuid/" + name);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject json = new JSONObject(builder.toString());
                    String uuid = json.getString("id");
                    reader.close();
                    uuids.add(new UUIDStorage(name, uuid));
                    return uuid;
                }catch (Exception ignored){
                    try {
                        URL url = new URL("https://api.minetools.eu/uuid/" + name);
                        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                        StringBuilder builder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                        JSONObject json = new JSONObject(builder.toString());
                        String uuid = json.getString("id");
                        reader.close();
                        uuids.add(new UUIDStorage(name, uuid));
                        return uuid;
                    }catch (Exception E){
                        try {
                            URL url = new URL("https://api.minecraftservices.com/minecraft/profile/lookup/name/" + name);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                            StringBuilder builder = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                builder.append(line);
                            }
                            JSONObject json = new JSONObject(builder.toString());
                            String uuid = json.getString("id");
                            reader.close();
                            uuids.add(new UUIDStorage(name, uuid));
                            return uuid;
                        }catch (Exception exception){}
                    }
                }
            }
        }
        return null;

    }


    public static String getUsername(String uuid) {
        if (uuids == null) {
            uuids = new ArrayList<>();
        }

        if (uuids.stream().anyMatch(uuidStorage -> uuidStorage.getUniqueID().equalsIgnoreCase(uuid))){
            return uuids.stream().filter(uuidStorage -> uuidStorage.getUniqueID().equalsIgnoreCase(uuid)).findFirst().get().getUsername();
        }else {
            String urlString = "https://minecraft-api.com/api/pseudo/" + uuid + "/json";
            try {
                URL url = new URL(urlString);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                StringBuilder builder = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                JSONObject json = new JSONObject(builder.toString());
                String username = json.getString("pseudo");
                uuids.add(new UUIDStorage(username, uuid));

                reader.close();
                return username;
            } catch (Exception e) {
                try {
                    URL url = new URL("https://api.minetools.eu/uuid/" + uuid);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
                    StringBuilder builder = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                    JSONObject json = new JSONObject(builder.toString());
                    String username = json.getString("name");
                    uuids.add(new UUIDStorage(username, uuid));

                    reader.close();
                    return username;
                }catch (Exception ignored){}
            }
        }
        return null;

    }


}
