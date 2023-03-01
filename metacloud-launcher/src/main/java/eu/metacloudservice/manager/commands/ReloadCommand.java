package eu.metacloudservice.manager.commands;

import eu.metacloudservice.Driver;
import eu.metacloudservice.configuration.ConfigDriver;
import eu.metacloudservice.configuration.dummys.managerconfig.ManagerConfig;
import eu.metacloudservice.configuration.dummys.message.Messages;
import eu.metacloudservice.terminal.commands.CommandAdapter;
import eu.metacloudservice.terminal.commands.CommandInfo;
import eu.metacloudservice.terminal.enums.Type;
import eu.metacloudservice.terminal.utils.TerminalStorageLine;
import eu.metacloudservice.webserver.dummys.Addresses;
import eu.metacloudservice.webserver.dummys.GroupList;
import eu.metacloudservice.webserver.dummys.WhiteList;
import eu.metacloudservice.webserver.entry.RouteEntry;

import java.util.ArrayList;

@CommandInfo(command = "reload", DEdescription = "lade alle module sowie weiter daten neu", ENdescription = "reload all modules and further data", aliases = {"rl"})
public class ReloadCommand extends CommandAdapter {


    @Override
    public void performCommand(CommandAdapter command, String[] args) {

          Driver.getInstance().getModuleDriver().disableModules();
        Driver.getInstance().getModuleDriver().enableModules();
        Messages raw = (Messages) new ConfigDriver("./local/messages.json").read(Messages.class);
        Messages msg = new Messages(Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getPrefix()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getSuccessfullyConnected()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getServiceIsFull()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getAlreadyOnFallback()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getConnectingGroupMaintenance()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getNoFallbackServer()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getKickNetworkIsFull()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getKickNetworkIsMaintenance()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getKickNoFallback()),
                Driver.getInstance().getMessageStorage().utf8ToUBase64(raw.getKickOnlyProxyJoin()));


        WhiteList whitelistConfig = new WhiteList();
        ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
        whitelistConfig.setWhitelist(config.getWhitelist());
        Driver.getInstance().getWebServer().updateRoute("/general/whitelist", new ConfigDriver().convert(whitelistConfig));
        Addresses AddressesConfig = new Addresses();

        ArrayList<String> addresses = new ArrayList<>();
        config.getNodes().forEach(managerConfigNodes -> {
            addresses.add(managerConfigNodes.getAddress());
        });
        AddressesConfig.setWhitelist(addresses);
        Driver.getInstance().getWebServer().updateRoute("/general/addresses", new ConfigDriver().convert(AddressesConfig));
        GroupList groupList = new GroupList();
        groupList.setGroups(Driver.getInstance().getGroupDriver().getAllStrings());
        Driver.getInstance().getWebServer().updateRoute("/general/grouplist", new ConfigDriver().convert(groupList));

        Driver.getInstance().getWebServer().updateRoute("/general/messages", new ConfigDriver().convert(msg));
        try {
            Driver.getInstance().getGroupDriver().getAll().forEach(group -> {
                if (Driver.getInstance().getWebServer().getRoute("/groups/" +group.getGroup()) == null){
                    Driver.getInstance().getWebServer().addRoute(new RouteEntry("/groups/" + group.getGroup(), new ConfigDriver().convert(group)));

                }else {
                    Driver.getInstance().getWebServer().updateRoute("/groups/"  + group.getGroup(), new ConfigDriver().convert(group));
                }
            });
        }catch (Exception ignored){}
        Driver.getInstance().getTerminalDriver().logSpeed(Type.COMMAND, "Die Cloud wurde mit Erfolg neu geladen", "The cloud was reloaded with success");

    }

    @Override
    public ArrayList<String> tabComplete(TerminalStorageLine consoleInput, String[] args) {
        return null;
    }
}