package eu.metacloudservice.networking;

import eu.metacloudservice.CloudAPI;
import eu.metacloudservice.Driver;
import eu.metacloudservice.events.listeners.CloudProxyPreparedEvent;
import eu.metacloudservice.events.listeners.CloudServicePreparedEvent;
import eu.metacloudservice.networking.out.service.PacketOutServicePrepared;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import io.netty.channel.Channel;

public class HandlePacketOutServicePrepared implements NettyAdaptor {
    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketOutServicePrepared){
            if (((PacketOutServicePrepared) packet).isProxy()){
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudProxyPreparedEvent(((PacketOutServicePrepared) packet).getName(), ((PacketOutServicePrepared) packet).getGroup(), ((PacketOutServicePrepared) packet).getNode()));
            }else {
                CloudAPI.getInstance().getEventDriver().executeEvent(new CloudServicePreparedEvent(((PacketOutServicePrepared) packet).getName(), ((PacketOutServicePrepared) packet).getGroup(), ((PacketOutServicePrepared) packet).getNode()));
            }
        }
    }
}