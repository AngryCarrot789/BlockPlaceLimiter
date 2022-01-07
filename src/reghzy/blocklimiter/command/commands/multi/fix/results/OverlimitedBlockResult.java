package reghzy.blocklimiter.command.commands.multi.fix.results;

import net.minecraft.tileentity.TileEntity;
import org.bukkit.ChatColor;
import reghzy.api.utils.NMSAPI;
import reghzy.blocklimiter.limit.BlockLimiter;
import reghzy.blocklimiter.limit.MetaLimiter;
import reghzy.blocklimiter.track.ServerTracker;
import reghzy.blocklimiter.track.world.WorldTracker;
import reghzy.carrottools.playerdata.results.custom.BlockLookupResult;
import reghzy.carrottools.playerdata.results.custom.ResultFormat;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class OverlimitedBlockResult extends BlockLookupResult {
    public static final String HEADER =
            ResultFormat.combine(ChatColor.GOLD + "Status",
                                 ChatColor.GRAY + "Owner",
                                 ResultFormat.LOCATION_HEADER,
                                 ChatColor.YELLOW + "ID:Data");


    private final TileEntity tileEntity;
    private final BlockLimiter limiter;
    private final MetaLimiter metaLimit;
    private final String nbtOwner;
    private final int id;
    private final int data;

    public OverlimitedBlockResult(@Nonnull TileEntity tileEntity, @Nonnull BlockLimiter limiter, @Nonnull MetaLimiter metaLimit, @Nullable String nbtOwner) {
        super(NMSAPI.getLocation(tileEntity));
        this.tileEntity = tileEntity;
        this.limiter = limiter;
        this.metaLimit = metaLimit;
        this.nbtOwner = nbtOwner;
        this.id = NMSAPI.getBlockId(tileEntity);
        this.data = NMSAPI.getBlockData(tileEntity);
    }

    @Nonnull
    public TileEntity getTileEntity() {
        return tileEntity;
    }

    @Nonnull
    public BlockLimiter getLimiter() {
        return limiter;
    }

    @Nonnull
    public MetaLimiter getMetaLimit() {
        return metaLimit;
    }

    @Nonnull
    public WorldTracker getWorldTracker() {
        return ServerTracker.getInstance().getWorldTracker(NMSAPI.getWorldName(this.tileEntity));
    }

    public String getWorldName() {
        return NMSAPI.getWorldName(this.tileEntity);
    }

    @Nullable
    public String getNbtOwner() {
        return nbtOwner;
    }

    @Nullable
    @Override
    public String getPlacer() {
        if (this.nbtOwner == null) {
            return super.getPlacer();
        }

        return this.nbtOwner;
    }

    @Override
    public String getPlacer(String defaultValue) {
        if (this.nbtOwner == null) {
            return super.getPlacer(defaultValue);
        }

        return this.nbtOwner;
    }

    public int getId() {
        return id;
    }

    public int getData() {
        return data;
    }

    @Override
    public String getContent() {
        return ResultFormat.combine(
                ChatColor.DARK_RED + "Overlimit",
                ChatColor.GRAY + getPlacer(),
                ResultFormat.combineLocationInt(this.getLocation()),
                ChatColor.YELLOW + (getId() + ":" + getData()));
    }

    public int getX() {
        return this.tileEntity.field_70329_l;
    }

    public int getY() {
        return this.tileEntity.field_70330_m;
    }

    public int getZ() {
        return this.tileEntity.field_70327_n;
    }
}
