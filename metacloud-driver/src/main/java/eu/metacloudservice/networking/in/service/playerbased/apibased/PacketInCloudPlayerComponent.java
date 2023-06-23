package eu.metacloudservice.networking.in.service.playerbased.apibased;

import eu.metacloudservice.networking.packet.NettyBuffer;
import eu.metacloudservice.networking.packet.Packet;
import lombok.Getter;
import net.kyori.adventure.text.Component;

public class PacketInCloudPlayerComponent extends Packet {

    @Getter
    private Component component;
    @Getter
    private String player;


    public PacketInCloudPlayerComponent() {
        setPacketUUID(935423342);
    }

    public PacketInCloudPlayerComponent(Component component, String player) {
        setPacketUUID(935423342);
        this.component = component;
        this.player = player;
    }

    @Override
    public void readPacket(NettyBuffer buffer) {
        component = (Component) buffer.readClass(Component.class);
        player = buffer.readString();
    }

    @Override
    public void writePacket(NettyBuffer buffer) {
        buffer.writeClass(component);
        buffer.writeString(player);
    }
}
