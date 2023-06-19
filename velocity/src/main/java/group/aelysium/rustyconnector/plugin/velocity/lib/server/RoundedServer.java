package group.aelysium.rustyconnector.plugin.velocity.lib.server;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.core.lib.database.redis.RedisPublisher;
import group.aelysium.rustyconnector.core.lib.database.redis.RedisService;
import group.aelysium.rustyconnector.core.lib.database.redis.messages.GenericRedisMessage;
import group.aelysium.rustyconnector.core.lib.database.redis.messages.MessageOrigin;
import group.aelysium.rustyconnector.core.lib.database.redis.messages.RedisMessageType;
import group.aelysium.rustyconnector.plugin.velocity.VelocityRustyConnector;
import group.aelysium.rustyconnector.plugin.velocity.central.VelocityAPI;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.RoundedServerFamily;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.rounded.NullRoundedSession;
import group.aelysium.rustyconnector.plugin.velocity.lib.family.rounded.RoundedSession;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.PlayerServer;
import net.kyori.adventure.text.Component;

import java.util.List;

public class RoundedServer extends PlayerServer {
    private RoundedSession session;

    public RoundedServer(ServerInfo serverInfo, int softPlayerCap, int hardPlayerCap, int weight) {
        super(serverInfo, softPlayerCap, hardPlayerCap, weight);
        this.session = NullRoundedSession.get();
    }

    /**
     * Gets the current session.
     * @return Gets the current session. If no session is active, will return {@link NullRoundedSession} if no session is currently assigned here.
     */
    public RoundedSession getSession() {
        return this.session;
    }

    /**
     * Start a new session with the provided round.
     * @param session The session to assign to this server.
     */
    public void assignSession(RoundedSession session) {
        if(!(this.session instanceof NullRoundedSession)) throw new IllegalStateException("You can't start a new session while one is active!");
        this.session = session;
    }

    /**
     * Close the current session.
     * This method will kick all current players out into the parent family.
     *
     * Additionally, this server will inform its family that it is now open for another session to join.
     */
    public void closeSession() {
        if(this.session instanceof NullRoundedSession) return;
        this.session = NullRoundedSession.get();

        RoundedServerFamily family = (RoundedServerFamily) this.getFamily();

        List<Player> players = this.session.getPlayers();

        for (Player player : players) {
            try {
                family.getParentFamily().connect(player);
            } catch (Exception e) {
                player.disconnect(Component.text(e.getMessage()));
            }
        }

        this.session.decompose();

        ((RoundedServerFamily) this.getFamily()).startAvailableSessions();
    }

    public void startSession() {
        VelocityAPI api = VelocityRustyConnector.getAPI();

        if(this.session instanceof NullRoundedSession) throw new NullPointerException("There was no available session to start!");

        session.assignServer(this);
        this.session.connect();

        GenericRedisMessage message = new GenericRedisMessage.Builder()
                .setType(RedisMessageType.ROUNDED_SESSION_START_EVENT)
                .setOrigin(MessageOrigin.PROXY)
                .setAddress(this.getServerInfo().getAddress())
                .buildSendable();

        api.getService(RedisService.class).publish(message);
    }


    /**
     * Validates the player against the server's current player count.
     * If the server is full or the player doesn't have permissions to bypass soft and hard player caps. They will be kicked
     *
     * @param player The player to validate
     * @return `true` if the player is able to join. `false` otherwise.
     * @deprecated This method always returns `false`. Running validation checks against players directly isn't supported for RoundedServers.
     */
    @Deprecated
    @Override
    public boolean validatePlayer(Player player) {
        return false;
    }
}
