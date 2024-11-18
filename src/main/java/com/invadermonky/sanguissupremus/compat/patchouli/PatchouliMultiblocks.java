package com.invadermonky.sanguissupremus.compat.patchouli;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.block.base.BlockEnum;
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.ritual.EnumRuneType;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualComponent;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class PatchouliMultiblocks {
    private int minX;
    private int minY;
    private int minZ;
    private int maxX;
    private int maxY;
    private int maxZ;

    public static void init() {
        PatchouliMultiblocks instance = new PatchouliMultiblocks();

        instance.initAltarMultiblocks();
        instance.initRitualMultiblocks();
    }

    private void initAltarMultiblocks() {

    }

    private void initRitualMultiblocks() {
        PatchouliAPI.IPatchouliAPI patchouliAPI = PatchouliAPI.instance;
        for(Ritual ritual : BloodMagic.RITUAL_MANAGER.getSortedRituals()) {
            String ritualId = BloodMagic.RITUAL_MANAGER.getId(ritual);
            Map<BlockPos, EnumRuneType> ritualMap = new HashMap<>();
            List<RitualComponent> components = new ArrayList<>();
            ritual.gatherComponents(components::add);

            resetMinMaxValues();
            for(RitualComponent component : components) {
                ritualMap.put(component.getOffset(), component.getRuneType());
                checkAndSetMinMaxValues(component.getX(EnumFacing.NORTH), component.getY(), component.getZ(EnumFacing.NORTH));
            }

            String[][] pattern = makeRitualPattern(ritualMap);

            handleExtraRitualComponents(ritualId, pattern);

            //TODO: Any new components must be added here so as to not break the multiblock display.
            IMultiblock multiblock = patchouliAPI.makeMultiblock(
                    pattern,
                    '0', RegistrarBloodMagicBlocks.RITUAL_CONTROLLER,
                    'B', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState(),
                    'W', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState().withProperty(((BlockEnum<EnumRuneType>) RegistrarBloodMagicBlocks.RITUAL_STONE).getProperty(), EnumRuneType.WATER),
                    'F', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState().withProperty(((BlockEnum<EnumRuneType>) RegistrarBloodMagicBlocks.RITUAL_STONE).getProperty(), EnumRuneType.FIRE),
                    'E', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState().withProperty(((BlockEnum<EnumRuneType>) RegistrarBloodMagicBlocks.RITUAL_STONE).getProperty(), EnumRuneType.EARTH),
                    'A', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState().withProperty(((BlockEnum<EnumRuneType>) RegistrarBloodMagicBlocks.RITUAL_STONE).getProperty(), EnumRuneType.AIR),
                    'D', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState().withProperty(((BlockEnum<EnumRuneType>) RegistrarBloodMagicBlocks.RITUAL_STONE).getProperty(), EnumRuneType.DUSK),
                    'd', RegistrarBloodMagicBlocks.RITUAL_STONE.getDefaultState().withProperty(((BlockEnum<EnumRuneType>) RegistrarBloodMagicBlocks.RITUAL_STONE).getProperty(), EnumRuneType.DAWN),
                    'C', Blocks.CHEST
                    //TODO: Blood Glass block
            );

            patchouliAPI.registerMultiblock(new ResourceLocation(SanguisSupremus.MOD_ID, ritualId), multiblock);
        }
    }

    private void resetMinMaxValues() {
        this.minX = this.minY = this.minZ = this.maxX = this.maxY = this.maxZ = 0;
    }

    private void checkAndSetMinMaxValues(int x, int y, int z) {
        if(x < this.minX) {
            this.minX = x;
        }
        if(y < this.minY) {
            this.minY = y;
        }
        if(z < this.minZ) {
            this.minZ = z;
        }
        if(x > this.maxX) {
            this.maxX = x;
        }
        if(y > this.maxY) {
            this.maxY = y;
        }
        if(z > this.maxZ) {
            this.maxZ = z;
        }
    }

    private String[][] makeRitualPattern(Map<BlockPos, EnumRuneType> ritualMap) {
        String[][] pattern = new String[1 + this.maxY - this.minY][1 + this.maxX - this.minX];
        //Top to Bottom
        for (int y = this.maxY; y >= this.minY; y--) {
            //West to East
            for (int x = this.minX; x <= this.maxX; x++) {
                StringBuilder row = new StringBuilder();
                //North to South
                for (int z = this.minZ; z <= this.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    if (!ritualMap.isEmpty()) {     // Rituals
                        EnumRuneType rune = ritualMap.get(pos);
                        if (rune != null) {
                            String name = rune.name();
                            PickRune:
                            switch (name) {
                                case "BLANK":
                                    row.append('B');
                                    break PickRune;
                                case "WATER":
                                    row.append('W');
                                    break PickRune;
                                case "FIRE":
                                    row.append('F');
                                    break PickRune;
                                case "EARTH":
                                    row.append('E');
                                    break PickRune;
                                case "AIR":
                                    row.append('A');
                                    break PickRune;
                                case "DUSK":
                                    row.append('D');
                                    break PickRune;
                                case "DAWN":
                                    row.append('d');
                                    break PickRune;
                            }
                        } else {
                            row.append(checkEmptySpace(x, y, z));
                        }
                    }
                }
                pattern[this.maxY - y][x - this.minX] = row.toString();
            }
        }
        return pattern;
    }

    private Character checkEmptySpace(int x, int y, int z) {
        if(x == 0 && y == 0 && z == 0) {
            return '0';
        }
        return '_';
    }

    private void handleExtraRitualComponents(String ritualId, String[][] pattern) {
        //TODO: Rituals with chests need to be added as cases here.
        if(ritualId.equals("downgrade")) {
            pattern[2][3] = "_FDC______";
        }
    }
}
