package group.aelysium.rustyconnector.plugin.velocity.lib.whitelist;

import com.google.gson.Gson;
import com.velocitypowered.api.proxy.Player;
import group.aelysium.rustyconnector.api.velocity.whitelist.IWhitelist;
import group.aelysium.rustyconnector.core.lib.Callable;
import group.aelysium.rustyconnector.api.core.lang.LangFileMappings;
import group.aelysium.rustyconnector.core.lib.lang.LangService;
import group.aelysium.rustyconnector.api.velocity.util.DependencyInjector;
import group.aelysium.rustyconnector.plugin.velocity.central.Tinder;
import group.aelysium.rustyconnector.plugin.velocity.lib.whitelist.config.WhitelistConfig;
import group.aelysium.rustyconnector.plugin.velocity.lib.Permission;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Whitelist implements IWhitelist {
    private final String message;
    private final String name;
    private final String permission;
    private final List<WhitelistPlayerFilter> playerFilters = new ArrayList<>();

    private final boolean usePlayers;
    private final boolean usePermission;
    private final boolean strict;
    private final boolean inverted;

    public Whitelist(String name, boolean usePlayers, boolean usePermission, String message, boolean strict, boolean inverted) {
        this.name = name;
        this.usePlayers = usePlayers;
        this.usePermission = usePermission;
        this.message = message;
        this.strict = strict;
        this.inverted = inverted;
        this.permission = Permission.constructNode("rustyconnector.whitelist.<whitelist name>",this.name);
    }

    public boolean usesPlayers() {
        return usePlayers;
    }
    public boolean usesPermission() {
        return usePermission;
    }
    public String name() {
        return name;
    }
    public String message() {
        return message;
    }
    public boolean inverted() {
        return this.inverted;
    }

    public List<WhitelistPlayerFilter> playerFilters() {
        return this.playerFilters;
    }

    /**
     * Validate a player against the whitelist.
     * @param player The player to validate.
     * @return `true` if the player is whitelisted. `false` otherwise.
     */
    public boolean validate(Player player) {
        Callable<Boolean> validate = () -> {
            if (Whitelist.this.strict)
                return validateStrict(player);
            else
                return validateSoft(player);
        };

        if(this.inverted)
            return !validate.execute();
        else
            return validate.execute();
    }

    private boolean validateStrict(Player player) {
        boolean playersValid = true;
        boolean countryValid = true;
        boolean permissionValid = true;


        if (this.usesPlayers())
            if (!WhitelistPlayerFilter.validate(this, player))
                playersValid = false;


        // if(this.usesCountries()) valid = this.validateCountry(ipAddress);


        if (this.usesPermission())
            if (!Permission.validate(player, this.permission))
                permissionValid = false;


        return (playersValid && countryValid && permissionValid);
    }

    private boolean validateSoft(Player player) {
        if (this.usesPlayers())
            if (WhitelistPlayerFilter.validate(this, player))
                return true;

        // if(this.usesCountries()) valid = this.validateCountry(ipAddress);

        if (this.usesPermission())
            return Permission.validate(player, this.permission);

        return false;
    }

    /**
     * Initializes a whitelist based on a config.
     * @return A whitelist.
     */
    public static Whitelist init(DependencyInjector.DI2<List<Component>, LangService> dependencies, String whitelistName) throws IOException {
        Tinder api = Tinder.get();
        List<Component> bootOutput = dependencies.d1();

        bootOutput.add(Component.text(" | Registering whitelist "+whitelistName+"...", NamedTextColor.DARK_GRAY));

        WhitelistConfig whitelistConfig = new WhitelistConfig(new File(String.valueOf(api.dataFolder()), "whitelists/"+whitelistName+".yml"));
        if(!whitelistConfig.generate(bootOutput, dependencies.d2(), LangFileMappings.VELOCITY_WHITELIST_TEMPLATE)) {
            throw new IllegalStateException("Unable to load or create whitelists/"+whitelistName+".yml!");
        }
        whitelistConfig.register();

        Whitelist whitelist = new Whitelist(
                whitelistName,
                whitelistConfig.getUse_players(),
                whitelistConfig.getUse_permission(),
                whitelistConfig.getMessage(),
                whitelistConfig.isStrict(),
                whitelistConfig.isInverted()
        );
        if(whitelistConfig.getUse_players()) {
            List<Object> players = whitelistConfig.getPlayers();
            Gson gson = new Gson();
            players.forEach(entry -> {
                String json = gson.toJson(entry);
                WhitelistPlayerFilter player = gson.fromJson(json, WhitelistPlayerFilter.class);

                whitelist.playerFilters().add(player);
            });
        }

        bootOutput.add(Component.text(" | Registered whitelist: "+whitelistName, NamedTextColor.YELLOW));
        return whitelist;
    }
}
