package group.aelysium.rustyconnector.toolkit.velocity.central;

import group.aelysium.rustyconnector.toolkit.core.config.IConfigService;
import group.aelysium.rustyconnector.toolkit.core.events.IEventManager;
import group.aelysium.rustyconnector.toolkit.core.packet.VelocityPacketBuilder;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.IServiceableService;
import group.aelysium.rustyconnector.toolkit.velocity.dynamic_teleport.IDynamicTeleportServiceHandler;
import group.aelysium.rustyconnector.toolkit.velocity.family.IFamilies;
import group.aelysium.rustyconnector.toolkit.velocity.friends.IFriendsService;
import group.aelysium.rustyconnector.toolkit.velocity.magic_link.IMagicLink;
import group.aelysium.rustyconnector.toolkit.velocity.parties.IPartyService;
import group.aelysium.rustyconnector.toolkit.velocity.player.IPlayerService;
import group.aelysium.rustyconnector.toolkit.velocity.server.IServerService;
import group.aelysium.rustyconnector.toolkit.core.serviceable.interfaces.IServiceHandler;
import group.aelysium.rustyconnector.toolkit.velocity.storage.IStorage;
import group.aelysium.rustyconnector.toolkit.velocity.family.whitelist.IWhitelistService;

import java.util.Optional;

public interface ICoreServiceHandler extends IServiceHandler {

    /**
     * Gets the {@link IEventManager event manager} which allows access to event based logic.
     * @return {@link IEventManager}
     */
    IEventManager events();

    /**
     * Gets the {@link IFamilies family service} which allows access to server families and other family related logic.
     * @return {@link IFamilies}
     */
    IFamilies family();

    /**
     * Gets the {@link IServerService server service} which allows access to server registration, unregistration, connection, and other server related logic.
     * @return {@link IServerService}
     */
    IServerService server();

    /**
     * Gets RustyConnector's {@link IConfigService config service} which allows direct access to RC configs.
     * @return {@link IConfigService}
     */
    IConfigService config();

    /**
     * Gets RustyConnector's {@link IStorage remote storage connector service} which allows direct access to database storage.
     * @return {@link IStorage}
     */
    IStorage storage();

    /**
     * Gets the {@link IPlayerService player service} which allows access to player resolving logic for usage when dealing with offline player data.
     * @return {@link IPlayerService}
     */
    IPlayerService player();

    /**
     * Gets the {@link IWhitelistService whitelist service} which allows access to the proxy's configured whitelists.
     * @return {@link IWhitelistService}
     */
    IWhitelistService whitelist();

    /**
     * Gets the {@link IMagicLink dynamic teleport service}.
     * The dynamic teleport module may not always be enabled, hence this returns an {@link Optional<IServiceableService<IDynamicTeleportServiceHandler>>}
     * @return {@link Optional<IServiceableService>}
     */
    IMagicLink magicLink();

    VelocityPacketBuilder packetBuilder();

    /**
     * Gets the {@link IPartyService party service}.
     * The party module may not always be enabled, hence this returns an {@link Optional<IPartyService>}
     * @return {@link Optional<IPartyService>}
     */
    Optional<? extends IPartyService> party();

    /**
     * Gets the {@link IFriendsService friends service}.
     * The friends module may not always be enabled, hence this returns an {@link Optional<IFriendsService>}
     * @return {@link Optional<IFriendsService>}
     */
    Optional<? extends IFriendsService> friends();

    /**
     * Gets the {@link IServiceableService<IDynamicTeleportServiceHandler> dynamic teleport service}.
     * The dynamic teleport module may not always be enabled, hence this returns an {@link Optional<IServiceableService<IDynamicTeleportServiceHandler>>}
     * @return {@link Optional<IServiceableService>}
     */
    Optional<? extends IServiceableService<? extends IDynamicTeleportServiceHandler>> dynamicTeleport();
}