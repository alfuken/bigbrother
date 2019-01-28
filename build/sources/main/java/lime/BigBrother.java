package lime;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Mod(modid = BigBrother.MODID, name = BigBrother.NAME, version = BigBrother.VERSION)
@Mod.EventBusSubscriber
public class BigBrother {
    public static final String MODID = "big_brother";
    public static final String NAME = "Big Brother";
    public static final String VERSION = "1";

    private static Path big_brothers_little_book;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        big_brothers_little_book = Paths.get("logs",MODID, getTimestamp()+".log");
        try {
            Files.createDirectories(big_brothers_little_book.getParent());
            Files.createFile(big_brothers_little_book);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
    }

    @SubscribeEvent
    public static void onChunkEnter(EntityEvent.EnteringChunk event)
    {
        if (event.getEntity() instanceof EntityPlayer && !event.getEntity().getEntityWorld().isRemote) {
            observe(getTimestamp() + " " +
                ((EntityPlayer) event.getEntity()).getDisplayNameString() +
                " entered chunk " +
                event.getNewChunkX()+":"+event.getNewChunkZ()+
                " from chunk " +
                event.getOldChunkX()+":"+event.getOldChunkZ()+
                " dim" +
                event.getEntity().dimension
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerInteract(PlayerInteractEvent.RightClickBlock event)
    {
        if (event.getSide().isServer()) {
            Block block = event.getWorld().getBlockState(event.getPos()).getBlock();
            String str = getTimestamp() + " " +
                event.getEntityPlayer().getDisplayNameString() +
                " at " +
                event.getEntityPlayer().getPosition().getX()+":"+
                event.getEntityPlayer().getPosition().getY()+":"+
                event.getEntityPlayer().getPosition().getZ()+
                " dim" +
                event.getEntity().dimension +
                " interacted with " +
                block.getLocalizedName() +
                " at " +
                event.getPos().getX()+":"+event.getPos().getY()+":"+event.getPos().getZ() +
                " dim" +
                event.getEntity().dimension;

            if (event.getItemStack() != ItemStack.EMPTY){
                str += " holding " + event.getItemStack().getDisplayName();
            }

            observe(str);
        }
    }

    private static String getTimestamp(){
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH-mm-ss"));
    }

    static void observe(String string){
        try {
            Files.write(big_brothers_little_book, Collections.singletonList(string), Charset.forName("UTF-8"), StandardOpenOption.APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
