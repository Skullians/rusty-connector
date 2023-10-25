package group.aelysium.rustyconnector.plugin.velocity.lib.magic_link.handlers;

import com.velocitypowered.api.proxy.server.ServerInfo;
import group.aelysium.rustyconnector.api.mc_loader.connection_intent.ConnectionIntent;
import group.aelysium.rustyconnector.core.lib.messenger.MessengerConnection;
import group.aelysium.rustyconnector.api.core.packet.PacketHandler;
import group.aelysium.rustyconnector.core.lib.packets.GenericPacket;
import group.aelysium.rustyconnector.api.core.packet.PacketOrigin;
import group.aelysium.rustyconnector.api.core.packet.PacketType;
import group.aelysium.rustyconnector.core.lib.packets.variants.ServerPingPacket;
import group.aelysium.rustyconnector.core.lib.packets.variants.ServerPingResponsePacket;
import group.aelysium.rustyconnector.api.core.log_gate.GateKey;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.lang.VelocityLang;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.PlayerServer;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.ServerService;
import net.kyori.adventure.text.format.NamedTextColor;

import java.net.InetSocketAddress;

public class MagicLinkPingHandler implements PacketHandler<GenericPacket> {
    @Override
    public void execute(GenericPacket genericPacket) throws Exception {
        ServerPingPacket packet = (ServerPingPacket) genericPacket;

        InetSocketAddress address = packet.address();
        Tinder api = Tinder.get();

        ServerInfo serverInfo = new ServerInfo(
                packet.serverName(),
                address
        );

        if(api.logger().loggerGate().check(GateKey.PING))
            api.logger().send(VelocityLang.PING.build(serverInfo));

        if(packet.intent() == ConnectionIntent.CONNECT)
            reviveOrConnectServer(serverInfo, packet);
        if(packet.intent() == ConnectionIntent.DISCONNECT)
            disconnectServer(serverInfo, packet);
    }

    private static void connectServer(ServerInfo serverInfo, ServerPingPacket packet) {
        Tinder api = Tinder.get();
        ServerService serverService = api.services().server();
        MessengerConnection backboneMessenger = api.flame().backbone().connection().orElseThrow();

        try {
            PlayerServer server = new ServerService.ServerBuilder()
                    .setServerInfo(serverInfo)
                    .setFamilyName(packet.familyName())
                    .setSoftPlayerCap(packet.softCap())
                    .setHardPlayerCap(packet.hardCap())
                    .setWeight(packet.weight())
                    .build();

            server.register(packet.familyName());

            ServerPingResponsePacket message = (ServerPingResponsePacket) new GenericPacket.Builder()
                    .setType(PacketType.PING_RESPONSE)
                    .setAddress(serverInfo.getAddress())
                    .setOrigin(PacketOrigin.PROXY)
                    .setParameter(ServerPingResponsePacket.ValidParameters.STATUS, String.valueOf(ServerPingResponsePacket.PingResponseStatus.ACCEPTED))
                    .setParameter(ServerPingResponsePacket.ValidParameters.MESSAGE, "Connected to the proxy! Registered as `"+server.serverInfo().getName()+"` into the family `"+server.family().name()+"`.")
                    .setParameter(ServerPingResponsePacket.ValidParameters.COLOR, NamedTextColor.GREEN.toString())
                    .setParameter(ServerPingResponsePacket.ValidParameters.INTERVAL_OPTIONAL, String.valueOf(serverService.serverInterval()))
                    .buildSendable();
            backboneMessenger.publish(message);

        } catch(Exception e) {
            ServerPingResponsePacket message = (ServerPingResponsePacket) new GenericPacket.Builder()
                    .setType(PacketType.PING_RESPONSE)
                    .setAddress(serverInfo.getAddress())
                    .setOrigin(PacketOrigin.PROXY)
                    .setParameter(ServerPingResponsePacket.ValidParameters.STATUS, String.valueOf(ServerPingResponsePacket.PingResponseStatus.DENIED))
                    .setParameter(ServerPingResponsePacket.ValidParameters.MESSAGE, "Attempt to connect to proxy failed! " + e.getMessage())
                    .setParameter(ServerPingResponsePacket.ValidParameters.COLOR, NamedTextColor.RED.toString())
                    .buildSendable();
            backboneMessenger.publish(message);
        }
    }

    private static void disconnectServer(ServerInfo serverInfo, ServerPingPacket packet) throws Exception {
        Tinder api = Tinder.get();
        api.services().server().unregisterServer(serverInfo, packet.familyName(), true);

    }

    private static void reviveOrConnectServer(ServerInfo serverInfo, ServerPingPacket packet) {
        Tinder api = Tinder.get();
        ServerService serverService = api.services().server();

        PlayerServer server = serverService.search(serverInfo);
        if (server == null) {
            connectServer(serverInfo, packet);
            return;
        }

        server.setTimeout(serverService.serverTimeout());
        server.setPlayerCount(packet.playerCount());
    }
}
