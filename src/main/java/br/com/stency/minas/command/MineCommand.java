package br.com.stency.minas.command;

import br.com.stency.common.util.command.CommonCommand;
import br.com.stency.common.util.command.annotation.Command;
import br.com.stency.common.util.item.ItemStackBuilder;
import br.com.stency.minas.Minas;
import br.com.stency.minas.data.Mine;
import br.com.stency.minas.service.MineService;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MineCommand extends CommonCommand {

    @Command("mina")
    @Override
    public void command(CommandSender commandSender, Player player, String[] strings) {
        if (player.isOp()) {
            if (strings.length == 0) {
                commandSender.sendMessage("§cUse /mina (create/edit/delete) (name)");
            } else {
                if (strings[0].equalsIgnoreCase("create")) {
                    if (strings.length < 2) {
                        player.sendMessage("§cUse /mina create (nome)");
                    } else {
                        Mine mine = Minas.getInstance().getService(MineService.class).get(strings[1]);
                        if (mine != null) {
                            player.sendMessage("§cJá existe uma mina com o nome §f" + strings[1] + "§c.");
                        } else {
                            Selection selection = WorldEditPlugin.getPlugin(WorldEditPlugin.class).getSelection(player);
                            if (selection.getMinimumPoint() != null && selection.getMaximumPoint() != null) {
                                mine = Minas.getInstance().getService(MineService.class).create(strings[1]);
                                mine.setPos1(selection.getMinimumPoint());
                                mine.setPos2(selection.getMaximumPoint());
                                mine.setStatusHologram(player.getLocation().getBlock().getLocation().add(0.5, 2.5, 0.5));
                                mine.setupHD();
                                player.sendMessage("§aVocê criou a mina §f" + mine.getName() + "§a com sucesso!");
                                player.sendMessage("");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f addore §a - adiciona minérios na mina");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f removeore §a - remove minérios da mina");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f hologram §a - muda a posição do holograma (é necessario resetar a mina)");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f resettime (time) §a - seta o tempo de reset da mina");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f reset §a - reseta a mina");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f ores §a - veja os minérios da mina");
                                player.sendMessage("§a - /mina edit " + mine.getName() + "§f resize §a - muda o tamanho da mina");
                                player.sendMessage("");
                            } else {
                                player.sendMessage("§cVocê deve selecionar a posição 1 e 2 de sua mina.");
                            }
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("edit")) {
                    if (strings.length < 3) {
                        player.sendMessage("§cUse /mina edit (name) (addore,removeore,hologram,resettime,reset,resize)");
                    } else {
                        Mine mine = Minas.getInstance().getService(MineService.class).get(strings[1]);
                        if (mine == null) {
                            player.sendMessage("§cA mina que você inseriu não existe.");
                        } else {
                            if (strings[2].equalsIgnoreCase("addore")) {
                                if (player.getItemInHand().getType() == Material.AIR || !player.getItemInHand().getType().isBlock()) {
                                    player.sendMessage("§cVocê deve estar segurando algum minério.");
                                } else {
                                    mine.getBlocks().add(player.getItemInHand());
                                    player.sendMessage("§aVocê adicionou o minério §f" + player.getItemInHand().getType().name() + "§a em sua mina.");
                                }
                            } else if (strings[2].equalsIgnoreCase("removeore")) {
                                if (player.getItemInHand().getType() == Material.AIR) {
                                    player.sendMessage("§cVocê deve estar segurando algum minério.");
                                } else {
                                    if (mine.getBlocks().contains(new ItemStackBuilder(player.getItemInHand().clone()).setAmount(1).build())) {
                                        mine.getBlocks().remove(new ItemStackBuilder(player.getItemInHand().clone()).setAmount(1).build());
                                        player.sendMessage("§aVocê removeu o minério §f" + player.getItemInHand().getType().name() + "§a em sua mina.");
                                    } else {
                                        player.sendMessage("§cO item que você está segurando não está incluido na lista de minérios de sua mina.");
                                    }
                                }
                            } else if (strings[2].equalsIgnoreCase("hologram")) {
                                mine.setStatusHologram(player.getLocation().getBlock().getLocation().add(0.5, 2.5, 0.5));
                                player.sendMessage("§aVocê alterou a localização de seu holograma com sucesso!");
                            } else if (strings[2].equalsIgnoreCase("reset")) {
                                mine.reset();
                                Minas.getInstance().getService(MineService.class).clearLocations();
                                player.sendMessage("§aVocê resetou a mina §f" + mine.getName() + "§a com sucesso!");
                            } else if (strings[2].equalsIgnoreCase("resettime")) {
                                if (strings.length < 4) {
                                    player.sendMessage("§cUse /mina edit " + strings[1] + " resettime (tempo)");
                                } else {
                                    mine.setResetTime(Long.valueOf(strings[3]));
                                    player.sendMessage("§aVocê definiu o tempo de reset da mina §f" + mine.getName() + "§a para §f" + strings[3] + "§a minutos.");
                                }
                            } else if (strings[2].equalsIgnoreCase("resize")) {
                                Selection selection = WorldEditPlugin.getPlugin(WorldEditPlugin.class).getSelection(player);
                                if (selection.getMinimumPoint() != null && selection.getMaximumPoint() != null) {
                                    mine.setPos1(selection.getMinimumPoint());
                                    mine.setPos2(selection.getMaximumPoint());
                                    player.sendMessage("§aVocê modificou o tamanho da mina §f" + mine.getName() + "§a.");
                                } else {
                                    player.sendMessage("§cVocê deve selecionar a posição 1 e 2 de sua mina.");
                                }
                            } else if (strings[2].equalsIgnoreCase("ores")) {
                                player.sendMessage("§aLista de minérios: §f" + mine.getBlocks());
                            }
                        }
                    }
                } else if (strings[0].equalsIgnoreCase("delete")) {
                    if (strings.length < 2) {
                        player.sendMessage("§cUse /mina delete (nome)");
                    } else {
                        Mine mine =   Minas.getInstance().getService(MineService.class).get(strings[1]);
                        if (mine == null) {
                            player.sendMessage("§cA mina §f" + strings[1] + "§c não existe!");
                        } else {
                            mine.getBlocks().clear();
                            mine.getBlocks().add(new ItemStack(Material.AIR));
                            mine.reset();
                            mine.getHologram().delete();
                            Minas.getInstance().getService(MineService.class).remove(mine.getName());
                            Minas.getInstance().getService(MineService.class).config().set("Mines." + mine.getName(), null);
                            Minas.getInstance().getService(MineService.class).config().save();
                            player.sendMessage("§aVocê deletou a mina §f" + mine.getName() + "§a com sucesso!");
                        }
                    }
                }
            }
        }else{
            player.sendMessage(getPermissionMessage());
        }
    }
}
