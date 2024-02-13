package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Lectern;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;
import org.bukkit.inventory.meta.BookMeta;

public class FlagDef_ReadLecterns extends FlagDefinition {
    
    public FlagDef_ReadLecterns(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }
    
    @EventHandler
    public void onInvOpen(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        
        Block block = event.getClickedBlock();
        if (block == null) return;
        BlockState state = block.getState();
        if (!(state instanceof Lectern)) return;
        
        Flag flag = this.getFlagInstanceAtLocation(block.getLocation(), null);
        if (flag == null) return;
        
        Player player = event.getPlayer();
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), false, playerData.lastClaim);
        if (claim == null) return;
        if (claim.ownerID == null) return;
        if (claim.ownerID.equals(player.getUniqueId()) || claim.hasExplicitPermission(player, ClaimPermission.Inventory)) return;
        
        Lectern lectern = (Lectern) state;
        ItemStack book = lectern.getInventory().getItem(0);
        if (book == null) return;

        // If it's a book and quill, pretend it's signed
        if (book.getType() == Material.WRITABLE_BOOK) {
            ItemStack writtenBook = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta fauxMeta = (BookMeta) writtenBook.getItemMeta();
            fauxMeta.setAuthor("GPFlags");
            fauxMeta.setTitle("Book and Quill");
            BookMeta meta = (BookMeta) book.getItemMeta();
            fauxMeta.pages(meta.pages());
            writtenBook.setItemMeta(fauxMeta);
            player.openBook(writtenBook);
            return;
        }

        player.openBook(book);
    }

    @Override
    public String getName() {
        return "ReadLecterns";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableReadLecterns);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableReadLecterns);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM);
    }
}