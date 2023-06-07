package eu.metacloudservice.codec;

public enum GameMode {
    /**
     * Creative mode may fly, build instantly, become invulnerable and create
     * free items.
     */
    CREATIVE,

    /**
     * Survival mode is the "normal" gameplay type, with no special features.
     */
    SURVIVAL,

    /**
     * Adventure mode cannot break blocks without the correct tools.
     */
    ADVENTURE,

    /**
     * Spectator mode cannot interact with the world in anyway and is
     * invisible to normal players. This grants the player the
     * ability to no-clip through the world.
     */
    SPECTATOR;
}