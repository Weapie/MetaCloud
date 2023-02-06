package eu.themetacloudservice.manager.cloudservices.entry;

import eu.themetacloudservice.Driver;
import eu.themetacloudservice.configuration.ConfigDriver;
import eu.themetacloudservice.configuration.dummys.managerconfig.ManagerConfig;
import eu.themetacloudservice.manager.CloudManager;
import eu.themetacloudservice.manager.cloudservices.enums.TaskedServiceStatus;
import eu.themetacloudservice.manager.cloudservices.interfaces.ITaskedService;
import eu.themetacloudservice.network.nodes.to.PackageToNodeHandelServiceExit;
import eu.themetacloudservice.network.nodes.to.PackageToNodeHandelServiceLaunch;
import eu.themetacloudservice.network.nodes.to.PackageToNodeHandelSync;
import eu.themetacloudservice.network.service.PackageRunCommand;
import eu.themetacloudservice.networking.NettyDriver;
import eu.themetacloudservice.process.ServiceProcess;
import eu.themetacloudservice.terminal.enums.Type;
import eu.themetacloudservice.webserver.dummys.CloudService;
import eu.themetacloudservice.webserver.entry.RouteEntry;
import lombok.SneakyThrows;

public class TaskedService implements ITaskedService {


    private final TaskedEntry entry;
    private ServiceProcess process;
    public boolean hasStartedNew;

    public String restKey;

    public TaskedService(TaskedEntry entry) {
        this.entry = entry;
        hasStartedNew = false;
        restKey = "/" +entry.getServiceName();
    }


    public ServiceProcess getProcess() {
        return process;
    }

    @SneakyThrows
    @Override
    public void handelExecute(String line) {

       if (entry.getNode().equalsIgnoreCase("InternalNode")){
           process.getProcess().getOutputStream().write((line + "\n").getBytes());
           process.getProcess().getOutputStream().flush();
       }else {
           NettyDriver.getInstance().nettyServer.sendPacket(entry.getNode(), new PackageRunCommand(line, entry.getServiceName()));
       }

    }


    @Override
    public void handelSync() {
        if (this.getEntry().getNode().equals("InternalNode")){
            this.process.sync();
           }else {
            PackageToNodeHandelSync sync = new PackageToNodeHandelSync(getEntry().getServiceName());
            NettyDriver.getInstance().nettyServer.sendPacket(getEntry().getNode(), sync);
        }
    }

    @Override
    public void handelLaunch() {


        CloudService service = new CloudService(entry.getServiceName(), entry.getCurrentPlayers(), entry.getStatus().toString());

        Driver.getInstance().getWebServer().addRoute(new RouteEntry(this.restKey, new ConfigDriver().convert(service)));

        if (this.getEntry().getNode().equals("InternalNode")){
            Driver.getInstance().getTerminalDriver().logSpeed(Type.INFO, "Der Service '§f"+getEntry().getServiceName()+"§r' wird gestartet 'node: §f"+entry.getNode()+"§r, port: §f"+entry.getUsedPort()+"§r'",
                    "The service '§f"+getEntry().getServiceName()+"§r' is starting 'node: §f"+entry.getNode()+"§r, port: §f"+entry.getUsedPort()+"§r'");
            ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
            process = new ServiceProcess(Driver.getInstance().getGroupDriver().load(getEntry().getGroupName()), getEntry().getServiceName(), entry.getUsedPort(), entry.isUseProtocol(), config.getSpigotVersion().equals("MINESTOM"));
            process.handelLaunch();
        }else {
            PackageToNodeHandelServiceLaunch launch = new PackageToNodeHandelServiceLaunch(entry.getServiceName(), new ConfigDriver().convert(Driver.getInstance().getGroupDriver().load(getEntry().getGroupName())), entry.isUseProtocol());
            NettyDriver.getInstance().nettyServer.sendPacket(getEntry().getNode(), launch);
        }
    }

    @Override
    public void handelScreen() {

    }

    @Override
    public void handelQuit() {

        Driver.getInstance().getWebServer().removeRoute(this.restKey);

        if (this.getEntry().getNode().equals("InternalNode")){
            Driver.getInstance().getTerminalDriver().logSpeed(Type.INFO, "Der Service '§f"+getEntry().getServiceName()+"§r' wird angehalten",
                    "The service '§f"+getEntry().getServiceName()+"§r' is stopping");
            process.handelShutdown();
        }else {
            PackageToNodeHandelServiceExit exit = new PackageToNodeHandelServiceExit(entry.getServiceName());
            if (  NettyDriver.getInstance().nettyServer.isChannelFound(entry.getNode())){
                NettyDriver.getInstance().nettyServer.sendPacket(getEntry().getNode(), exit);
            }
        }
    }


    @Override
    public void handelStatusChange(TaskedServiceStatus status) {
        this.entry.setStatus(status);

        CloudService service = (CloudService) new ConfigDriver().convert(CloudManager.restDriver.get(this.restKey), CloudService.class);

        service.setStatus(status.toString());

        Driver.getInstance().getWebServer().updateRoute(this.restKey, new ConfigDriver().convert(service));


        if (status == TaskedServiceStatus.IN_GAME){
            ManagerConfig config = (ManagerConfig) new ConfigDriver("./service.json").read(ManagerConfig.class);
            hasStartedNew = true;
            if (entry.getNode().equals("InternalNode")){
                int freeMemory = Driver.getInstance().getMessageStorage().canUseMemory;
                int memoryAfter = freeMemory- Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getUsedMemory();

                if (memoryAfter >= 0){
                    TaskedService taskedService = CloudManager.serviceDriver.register(new TaskedEntry(
                            CloudManager.serviceDriver.getFreePort(Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getGroupType().equalsIgnoreCase("PROXY")),
                            getEntry().getGroupName(),
                            getEntry().getGroupName() + config.getSplitter() + CloudManager.serviceDriver.getFreeUUID(entry.getGroupName()),
                            "InternalNode", getEntry().isUseProtocol()));
                    Driver.getInstance().getMessageStorage().canUseMemory  =Driver.getInstance().getMessageStorage().canUseMemory -  Driver.getInstance().getGroupDriver().load(entry.getGroupName()).getUsedMemory();
                    taskedService.handelLaunch();
                }
            }else {
                TaskedService taskedService = CloudManager.serviceDriver.register(new TaskedEntry(
                        -1,
                        getEntry().getGroupName(),
                        getEntry().getGroupName() + config.getSplitter() + CloudManager.serviceDriver.getFreeUUID(entry.getGroupName()),
                        getEntry().getNode(), getEntry().isUseProtocol()));

                taskedService.handelLaunch();
            }
        }
    }

    @Override
    public void handelCloudPlayerConnection(boolean connect) {
        if (connect){
            entry.setCurrentPlayers(entry.getCurrentPlayers() + 1);
        }else {
            entry.setCurrentPlayers(entry.getCurrentPlayers()-1);
        }

        CloudService service = (CloudService) new ConfigDriver().convert(CloudManager.restDriver.get(this.restKey), CloudService.class);

        service.setPlayers(entry.getCurrentPlayers());

        Driver.getInstance().getWebServer().updateRoute(this.restKey, new ConfigDriver().convert(service));
    }

    public TaskedEntry getEntry() {
        return entry;
    }
}