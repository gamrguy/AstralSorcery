package hellfirepvp.astralsorcery.common.starlight.transmission.base;

import hellfirepvp.astralsorcery.AstralSorcery;
import hellfirepvp.astralsorcery.common.constellation.Constellation;
import hellfirepvp.astralsorcery.common.starlight.transmission.IPrismTransmissionNode;
import hellfirepvp.astralsorcery.common.starlight.transmission.ITransmissionReceiver;
import hellfirepvp.astralsorcery.common.starlight.transmission.registry.TransmissionClassRegistry;
import hellfirepvp.astralsorcery.common.util.nbt.NBTUtils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is part of the Astral Sorcery Mod
 * The complete source code for this mod can be found on github.
 * Class: SimpleTransmissionReceiver
 * Created by HellFirePvP
 * Date: 05.08.2016 / 13:59
 */
public class SimpleTransmissionReceiver implements ITransmissionReceiver {

    private BlockPos thisPos;

    private Set<BlockPos> sourcesToThis = new HashSet<>();

    public SimpleTransmissionReceiver(@Nonnull BlockPos thisPos) {
        this.thisPos = thisPos;
    }

    @Override
    public BlockPos getPos() {
        return thisPos;
    }

    @Override
    public void notifySourceLink(World world, BlockPos source) {
        if(!sourcesToThis.contains(source)) sourcesToThis.add(source);
    }

    @Override
    public void notifySourceUnlink(World world, BlockPos source) {
        sourcesToThis.remove(source);
    }

    @Override
    public boolean notifyBlockChange(World world, BlockPos changed) {
        return false;
    }

    @Override
    public List<BlockPos> getSources() {
        return sourcesToThis.stream().collect(Collectors.toCollection(LinkedList::new));
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        this.thisPos = NBTUtils.readBlockPosFromNBT(compound);
        this.sourcesToThis.clear();

        NBTTagList list = compound.getTagList("sources", 10);
        for (int i = 0; i < list.tagCount(); i++) {
            sourcesToThis.add(NBTUtils.readBlockPosFromNBT(list.getCompoundTagAt(i)));
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound compound) {
        NBTUtils.writeBlockPosToNBT(thisPos, compound);

        NBTTagList sources = new NBTTagList();
        for (BlockPos source : sourcesToThis) {
            NBTTagCompound comp = new NBTTagCompound();
            NBTUtils.writeBlockPosToNBT(source, comp);
            sources.appendTag(comp);
        }
        compound.setTag("sources", sources);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimpleTransmissionReceiver that = (SimpleTransmissionReceiver) o;
        return !(thisPos != null ? !thisPos.equals(that.thisPos) : that.thisPos != null);
    }

    @Override
    public TransmissionClassRegistry.TransmissionProvider getProvider() {
        return new Provider();
    }

    @Override
    public void onStarlightReceive(World world, boolean isChunkLoaded, Constellation type, double amount) {
        System.out.println("Received " + amount + " of " + type.getName());
    }

    public static class Provider implements TransmissionClassRegistry.TransmissionProvider {

        @Override
        public IPrismTransmissionNode provideEmptyNode() {
            return new SimpleTransmissionReceiver(null);
        }

        @Override
        public String getIdentifier() {
            return AstralSorcery.MODID + ":SimpleTransmissionReceiver";
        }

    }
}