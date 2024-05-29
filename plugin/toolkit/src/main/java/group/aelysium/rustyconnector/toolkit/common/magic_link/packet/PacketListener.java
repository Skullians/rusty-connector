package group.aelysium.rustyconnector.toolkit.common.magic_link.packet;

public abstract class PacketListener<Packet extends IPacket> {
    /**
     * The target will be used to decide if this listener should be executed for the passed packet.
     * @return {@link PacketIdentification}
     */
    public abstract PacketIdentification target();

    /**
     * Wraps the packet so that it can be passed to {@link #execute(Packet)}.
     * @param packet The packet to wrap.
     */
    public abstract Packet wrap(IPacket packet);

    public abstract void execute(Packet packet) throws Exception;

    public void wrapAndExecute(Packet packet) throws Exception {
        this.execute(this.wrap(packet));
    }
}
