package eu.metacloudservice.events.listeners;

import eu.metacloudservice.events.entrys.IEventAdapter;

public class CloudProxyDisconnectedEvent extends IEventAdapter {
    private final String name;

    public String getName() {
        return this.name;
    }

    public CloudProxyDisconnectedEvent(String name) {
        this.name = name;
    }
}
