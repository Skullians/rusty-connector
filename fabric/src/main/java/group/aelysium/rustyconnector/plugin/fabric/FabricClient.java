package group.aelysium.rustyconnector.plugin.fabric;

import group.aelysium.rustyconnector.common.errors.Error;
import group.aelysium.rustyconnector.common.util.CommandClient;
import net.kyori.adventure.text.Component;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static group.aelysium.rustyconnector.plugin.fabric.FabricRustyConnector.serializer;

public interface FabricClient {

    class Player extends CommandClient.Player<ServerCommandSource> {
        public Player(ServerCommandSource source) {
            super(source);
        }

        public void send(Component message) {
            String legacy = serializer.serialize(message);
            ((ServerCommandSource) this.source).sendMessage(Text.literal(legacy));
        }
        public void send(Error error) {
            String legacy = serializer.serialize(error.toComponent());
            ((ServerCommandSource) this.source).sendMessage(Text.literal(legacy));
        }
        
        @Override
        public String id() {
            return Objects.requireNonNull(((ServerCommandSource) this.source).getPlayer()).getUuidAsString();
        }
        
        @Override
        public String username() {
            return ((ServerCommandSource) this.source).getName();
        }
    }

    class Console extends CommandClient.Console<ServerCommandSource> {
        public Console(@NotNull ServerCommandSource source) {
            super(source);
        }

        public void send(Component message) {
            String legacy = serializer.serialize(message);
            ((ServerCommandSource) this.source).sendMessage(Text.literal(legacy));
        }
        public void send(Error error) {
            String legacy = serializer.serialize(error.toComponent());
            ((ServerCommandSource) this.source).sendMessage(Text.literal(legacy));
        }
    }

    class Other extends CommandClient.Other<ServerCommandSource> {
        public Other(@NotNull ServerCommandSource source) {
            super(source);
        }

        public void send(Component message) {
            String legacy = serializer.serialize(message);
            ((ServerCommandSource) this.source).sendMessage(Text.literal(legacy));
        }
        public void send(Error error) {
            String legacy = serializer.serialize(error.toComponent());
            ((ServerCommandSource) this.source).sendMessage(Text.literal(legacy));
        }
    }
}