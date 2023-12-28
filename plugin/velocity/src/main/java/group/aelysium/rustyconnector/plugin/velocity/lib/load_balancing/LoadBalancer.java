package group.aelysium.rustyconnector.plugin.velocity.lib.load_balancing;

import group.aelysium.rustyconnector.core.lib.algorithm.SingleSort;
import group.aelysium.rustyconnector.plugin.velocity.event_handlers.EventDispatch;
import group.aelysium.rustyconnector.toolkit.velocity.events.family.MCLoaderLockedEvent;
import group.aelysium.rustyconnector.toolkit.velocity.events.family.MCLoaderUnlockedEvent;
import group.aelysium.rustyconnector.toolkit.velocity.family.Family;
import group.aelysium.rustyconnector.toolkit.velocity.load_balancing.ILoadBalancer;
import group.aelysium.rustyconnector.plugin.velocity.lib.server.MCLoader;
import group.aelysium.rustyconnector.toolkit.velocity.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public abstract class LoadBalancer implements ILoadBalancer<MCLoader> {
    private boolean weighted;
    private boolean persistence;
    private int attempts;
    protected int index = 0;
    protected Vector<MCLoader> servers = new Vector<>();
    protected Vector<MCLoader> lockedServers = new Vector<>();

    public LoadBalancer(Settings settings) {
        this.weighted = settings.weighted();
        this.persistence = settings.persistence();
        this.attempts = settings.attempts();
    }

    public boolean persistent() {
        return this.persistence;
    }

    public int attempts() {
        if(!this.persistent()) return 0;
        return this.attempts;
    }

    public boolean weighted() {
        return this.weighted;
    }

    public MCLoader current() {
        MCLoader item;
        if(this.index >= this.size()) {
            this.index = 0;
            item = this.servers.get(this.index);
        } else item = this.servers.get(this.index);

        assert item != null;

        return item;
    }

    public int index() {
        return this.index;
    }

    public void iterate() {
        this.index += 1;
        if(this.index >= this.servers.size()) this.index = 0;
    }

    final public void forceIterate() {
        this.index += 1;
        if(this.index >= this.servers.size()) this.index = 0;
    }

    public abstract void completeSort();

    public abstract void singleSort();

    public void add(MCLoader item) {
        if(this.servers.contains(item)) return;
        this.servers.add(item);
    }

    public void remove(MCLoader item) {
        if(this.servers.remove(item)) return;
        this.lockedServers.remove(item);
    }

    public int size() {
        return this.servers.size() + this.lockedServers.size();
    }

    public int size(boolean locked) {
        if(locked) return this.lockedServers.size();
        return this.servers.size();
    }

    public List<MCLoader> servers() {
        List<MCLoader> servers = new ArrayList<>();

        servers.addAll(openServers());
        servers.addAll(lockedServers());

        return servers;
    }
    public List<MCLoader> openServers() {
        return this.servers.stream().toList();
    }
    public List<MCLoader> lockedServers() {
        return this.lockedServers.stream().toList();
    }

    public String toString() {
        return "LoadBalancer (RoundRobin): "+this.size()+" items";
    }

    public void setPersistence(boolean persistence, int attempts) {
        this.persistence = persistence;
        this.attempts = attempts;
    }

    public void setWeighted(boolean weighted) {
        this.weighted = weighted;
    }

    public void resetIndex() {
        this.index = 0;
    }

    public boolean contains(MCLoader item) {
        return this.servers.contains(item);
    }

    public void lock(MCLoader server) {
        if(!this.servers.remove(server)) return;
        this.lockedServers.add(server);

        EventDispatch.Safe.fireAndForget(new MCLoaderLockedEvent(server.family(), server));
    }

    public void unlock(MCLoader server) {
        if(!this.lockedServers.remove(server)) return;
        this.servers.add(server);

        EventDispatch.Safe.fireAndForget(new MCLoaderUnlockedEvent(server.family(), server));
    }

    public boolean joinable(MCLoader server) {
        return this.servers.contains(server);
    }

    public record Settings(boolean weighted, boolean persistence, int attempts) {}
}