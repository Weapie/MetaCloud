package eu.metacloudservice.networking;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.events.listeners.CloudPlayerConnectedEvent;
import eu.metacloudservice.networking.out.service.playerbased.PacketOutPlayerConnect;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import eu.metacloudservice.pool.player.entrys.CloudPlayer;
import eu.metacloudservice.storage.UUIDDriver;
import io.netty.channel.Channel;

public class HandlePacketOutPlayerConnect implements NettyAdaptor {
    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutPlayerConnect){
            CloudAPI.getInstance().getPlayerPool().registerPlayer(new CloudPlayer(((PacketOutPlayerConnect) packet).getName(), UUIDDriver.getUUID(((PacketOutPlayerConnect) packet).getName())));
            CloudAPI.getInstance().getEventDriver().executeEvent(new CloudPlayerConnectedEvent(((PacketOutPlayerConnect) packet).getName(),  UUIDDriver.getUUID(((PacketOutPlayerConnect) packet).getName()), CloudAPI.getInstance().getPlayerPool().getPlayer(((PacketOutPlayerConnect) packet).getName()).getProxyServer().getName()));
        }
    }
}