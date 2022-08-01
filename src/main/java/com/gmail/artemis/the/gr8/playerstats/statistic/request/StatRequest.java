package com.gmail.artemis.the.gr8.playerstats.statistic.request;

import com.gmail.artemis.the.gr8.playerstats.api.RequestGenerator;
import com.gmail.artemis.the.gr8.playerstats.enums.Target;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

/** A StatRequest is the object PlayerStats uses to calculate and format the requested statistic.
 This object can be generated from two different sources:
 <br>- Internally: by PlayerStats itself when /stat is called, using the args provided by the CommandSender.
 <br>- Externally: through the API methods provided by the {@link RequestGenerator} interface.
 <br>
 <br>For this StatRequest to be valid, it needs the following values:
 <ul>
 <li> a {@link Statistic} <code>statistic</code> </li>
 <li> if this Statistic is not of {@link Statistic.Type} Untyped, a <code>subStatEntryName</code> needs to be set,
 together with one of the following values:
 <br>- for Type.Block: a {@link Material} <code>blockMaterial</code>
 <br>- for Type.Item: a {@link Material} <code>itemMaterial</code>
 <br>- for Type.Entity: an {@link EntityType} <code>entityType</code>
 <li> a {@link Target} <code>target</code> (automatically set for all API-requests)
 <li> if the <code>target</code> is Target.Player, a <code>playerName</code> needs to be added
 </ul>*/
public final class StatRequest {

    private final CommandSender sender;
    private boolean isAPIRequest;
    private Statistic statistic;
    private String playerName;
    private Target target;
    private int topListSize;

    private String subStatEntryName;
    private EntityType entity;
    private Material block;
    private Material item;
    private boolean playerFlag;

    /** Create a new {@link StatRequest} with default values:
     <br>- CommandSender sender (provided)
     <br>- Target <code>target</code> = {@link Target#TOP}
     <br>- boolean <code>playerFlag</code> = false
     <br>- boolean <code>isAPIRequest</code> = false

     @param sender the CommandSender who prompted this RequestGenerator
     */
    public StatRequest(@NotNull CommandSender sender) {
        this(sender, false);
    }

    /** Create a new {@link StatRequest} with default values:
     <br>- CommandSender sender (provided)
     <br>- Target target = {@link Target#TOP}
     <br>- boolean playerFlag = false
     <br>- boolean isAPIRequest (provided)

     @param sender the CommandSender who prompted this RequestGenerator
     @param isAPIRequest whether this RequestGenerator is coming through the API or the onCommand
     */
    public StatRequest(@NotNull CommandSender sender, boolean isAPIRequest) {
        this.sender = sender;
        this.isAPIRequest = isAPIRequest;
        target = Target.TOP;
        playerFlag = false;
    }

    public void setAPIRequest() {
        this.isAPIRequest = true;
    }

    public boolean isAPIRequest() {
        return isAPIRequest;
    }

    public @NotNull CommandSender getCommandSender() {
        return sender;
    }

    public boolean isConsoleSender() {
        return sender instanceof ConsoleCommandSender;
    }

    public void setStatistic(Statistic statistic) {
        this.statistic = statistic;
    }

    public Statistic getStatistic() {
        return statistic;
    }

    public void setSubStatEntryName(String subStatEntry) {
        this.subStatEntryName = subStatEntry;
    }

    public String getSubStatEntryName() {
        return subStatEntryName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerFlag(boolean playerFlag) {
        this.playerFlag = playerFlag;
    }

    public boolean getPlayerFlag() {
        return playerFlag;
    }

    public void setTarget(Target target) {
        this.target = target;
    }

    public @NotNull Target getTarget() {
        return target;
    }

    public void setTopListSize(int topListSize) {
        this.topListSize = topListSize;
    }

    public int getTopListSize() {
        return this.topListSize;
    }

    public void setEntity(EntityType entity) {
        this.entity = entity;
    }

    public EntityType getEntity() {
        return entity;
    }

    public void setBlock(Material block) {
        this.block = block;
    }

    public Material getBlock() {
        return block;
    }

    public void setItem(Material item) {
        this.item = item;
    }

    public Material getItem() {
        return item;
    }

    public boolean isValid() {
        if (statistic == null) {
            return false;
        } else if (target == Target.PLAYER && playerName == null) {
            return false;
        } else if (statistic.getType() != Statistic.Type.UNTYPED &&
                subStatEntryName == null) {
            return false;
        } else {
            return hasMatchingSubStat();
        }
    }

    private boolean hasMatchingSubStat() {
        switch (statistic.getType()) {
            case BLOCK -> {
                return block != null;
            }
            case ENTITY -> {
                return entity != null;
            }
            case ITEM -> {
                return item != null;
            }
            default -> {
                return true;
            }
        }
    }
}