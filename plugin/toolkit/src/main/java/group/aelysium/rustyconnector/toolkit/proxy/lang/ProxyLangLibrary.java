package group.aelysium.rustyconnector.toolkit.proxy.lang;

import group.aelysium.rustyconnector.toolkit.common.absolute_redundancy.Particle;
import group.aelysium.rustyconnector.toolkit.common.lang.ASCIIAlphabet;
import group.aelysium.rustyconnector.toolkit.common.lang.IConfig;
import group.aelysium.rustyconnector.toolkit.common.lang.Lang;
import group.aelysium.rustyconnector.toolkit.mc_loader.lang.MCLoaderLangLibrary;
import group.aelysium.rustyconnector.toolkit.proxy.family.load_balancing.ILoadBalancer;
import group.aelysium.rustyconnector.toolkit.proxy.family.whitelist.IWhitelist;
import org.jetbrains.annotations.NotNull;

public class ProxyLangLibrary implements Particle {
    private final ProxyLang lang;
    private final ASCIIAlphabet asciiAlphabet;
    private final IConfig git;
    private final IConfig config;
    private final IConfig family;
    private final IConfig<ILoadBalancer.Settings> loadBalancer;
    private final IConfig<IWhitelist.Settings> whitelist;
    private final IConfig magicConfig;
    private final IConfig magicLink;

    protected ProxyLangLibrary(
            @NotNull ProxyLang lang,
            @NotNull ASCIIAlphabet asciiAlphabet,
            @NotNull IConfig git,
            @NotNull IConfig config,
            @NotNull IConfig family,
            @NotNull IConfig<ILoadBalancer.Settings> loadBalancer,
            @NotNull IConfig<IWhitelist.Settings> whitelist,
            @NotNull IConfig magicConfig,
            @NotNull IConfig magicLink
    ) {

        this.lang = lang;
        this.asciiAlphabet = asciiAlphabet;
        this.git = git;
        this.config = config;
        this.family = family;
        this.loadBalancer = loadBalancer;
        this.whitelist = whitelist;
        this.magicConfig = magicConfig;
        this.magicLink = magicLink;
    }

    @Override
    public void close() throws Exception {

    }

    public static class Tinder extends Particle.Tinder<ProxyLangLibrary> {
        private final Settings settings;

        public Tinder(@NotNull Settings settings) {
            this.settings = settings;
        }

        @Override
        public @NotNull ProxyLangLibrary ignite() throws Exception {
            return new ProxyLangLibrary(
                    settings.lang(),
                    settings.asciiAlphabet(),
                    settings.git(),
                    settings.config(),
                    settings.family(),
                    settings.loadBalancer(),
                    settings.whitelist(),
                    settings.magicConfig(),
                    settings.magicLink()
            );
        }
    }

    public record Settings(
            @NotNull ProxyLang lang,
            @NotNull ASCIIAlphabet asciiAlphabet,
            @NotNull IConfig git,
            @NotNull IConfig config,
            @NotNull IConfig family,
            @NotNull IConfig<ILoadBalancer.Settings> loadBalancer,
            @NotNull IConfig<IWhitelist.Settings> whitelist,
            @NotNull IConfig magicConfig,
            @NotNull IConfig magicLink
    ) {}
}
