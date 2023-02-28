package eu.metacloudservice.manager.networking.service;

import eu.metacloudservice.manager.CloudManager;
import eu.metacloudservice.networking.in.service.cloudapi.PacketInStopService;
import eu.metacloudservice.networking.packet.NettyAdaptor;
import eu.metacloudservice.networking.packet.Packet;
import io.netty.channel.Channel;

public class HandlePacketInStopService implements NettyAdaptor {
    @Override
    public void handle(Channel channel, Packet packet) {
        if (packet instanceof PacketInStopService){
            CloudManager.serviceDriver.unregister(((PacketInStopService) packet).getService());
        }

    }
}
