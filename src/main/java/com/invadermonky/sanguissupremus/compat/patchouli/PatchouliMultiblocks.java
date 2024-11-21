package com.invadermonky.sanguissupremus.compat.patchouli;

import WayofTime.bloodmagic.BloodMagic;
import WayofTime.bloodmagic.altar.AltarComponent;
import WayofTime.bloodmagic.altar.AltarTier;
import WayofTime.bloodmagic.altar.ComponentType;
import WayofTime.bloodmagic.block.base.BlockEnum;
import WayofTime.bloodmagic.core.RegistrarBloodMagicBlocks;
import WayofTime.bloodmagic.ritual.EnumRuneType;
import WayofTime.bloodmagic.ritual.Ritual;
import WayofTime.bloodmagic.ritual.RitualComponent;
import com.invadermonky.sanguissupremus.SanguisSupremus;
import com.invadermonky.sanguissupremus.registry.ModBlocksSS;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import org.apache.commons.lang3.StringUtils;
import vazkii.patchouli.api.IMultiblock;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.*;

import static com.invadermonky.sanguissupremus.util.libs.LibNames.*;

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
        for(AltarTier tier : AltarTier.values()) {
            String[][] pattern;
            int shiftMultiblock = 1;
            //TODO
            switch(tier) {
                case ONE:
                    pattern = new String[][] { {"0"}, {"_"} };
                    shiftMultiblock = 0;
                    break;
                default:
                    Map<BlockPos, ComponentType> altarMap = new HashMap<>();
                    List<AltarComponent> components = tier.getAltarComponents();
                    resetMinMaxValues();
                    for(AltarComponent component : components) {
                        BlockPos offset = component.getOffset();
                        altarMap.put(offset, component.getComponent());
                        checkAndSetMinMaxValues(offset.getX(), offset.getY(), offset.getZ());
                    }
                    pattern = makeAltarPattern(altarMap);
            }
        }
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

            pattern = handleExtraRitualComponents(ritualId, pattern);

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
                    'C', Blocks.CHEST,
                    'w', RegistrarBloodMagicBlocks.DEMON_CRYSTALLIZER,
                    'Z', ModBlocksSS.ENDER_CHEST_ACCESSOR,
                    'T', RegistrarBloodMagicBlocks.BLOOD_TANK.getDefaultState()
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

    private String[][] makeAltarPattern(Map<BlockPos, ComponentType> altarMap) {
        String[][] pattern = new String[1 + this.maxY - this.minY][1 + this.maxX - this.minX];
        //Top to Bottom
        for(int y = this.maxY; y >= this.minY; y--) {
            //West to East
            for(int x = this.minX; x <= this.maxX; x++) {
                StringBuilder row = new StringBuilder();
                for(int z = this.minZ; z <= this.maxZ; z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    ComponentType component = altarMap.get(pos);
                    if(component != null) {
                        String name = component.name();
                        switch(component) {
                            case BLOODRUNE:
                                row.append('R');
                                break;
                            case NOTAIR:
                                row.append('P');
                                break;
                            case GLOWSTONE:
                                row.append('G');
                                break;
                            case BLOODSTONE:
                                row.append('S');
                                break;
                            case BEACON:
                                row.append('B');
                                break;
                            case CRYSTAL:
                                row.append('C');
                                break;
                        }
                    } else {
                        row.append(checkEmptySpace(x, y, z));
                    }
                }
                pattern[this.maxY - y][x - this.minX] = row.toString();
            }
        }
        return pattern;
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

    private String[][] handleExtraRitualComponents(String ritualId, String[][] pattern) {
        //TODO: Rituals with chests or other components need to be added as cases here.
        //  ellipsoid chest
        //  placer chest
        String[][] newPattern;
        switch (ritualId) {
            case "altar_builder":
                pattern[4][13] = "_____________C_____________";
                break;
            case RITUAL_CHASING_SHADOWS:
            case RITUAL_FADING_LIGHT:
            case "zephyr":
                pattern[0][2] = "__C__";
                break;
            case "crystal_split":
                return injectCenteredBlockAbove(pattern, 'w');
            case "armour_downgrade":
                pattern[2][3] = "_FDC______";
                break;
            case "ellipsoid":
            case "placer":
            case "full_stomach":
                return injectCenteredBlockAbove(pattern, 'C');
            case RITUAL_ENDER_ACCESS:
                //TODO: Multiblock not displaying correctly. May be because of invalid texture.
                return injectCenteredBlockAbove(pattern, 'Z');
            case "felling":
                pattern[0][1] = "_C_";
                break;
            case RITUAL_IMPRISONED_SOULS:
                pattern[1][5] = "A____C____A";
                break;
            case "pump":
                pattern[0][1] = "_T_";
                break;
            case RITUAL_REFORMING_VOID:
                pattern[0][2] = "__C__";
                pattern[1][2] = "__C__";
                break;
        }
        return pattern;
    }

    /**
     * Increases the size of the multiblock pattern array and fills the new array elements with empty blocks. The center
     * of the top layer of the multiblock structure will have the passed block code added. Don't use this unless you know
     * exactly what you're doing.
     *
     * @param pattern The multiblock pattern.
     * @param blockCode The character code used for the block in the multiblock structure
     * @return Adjusted pattern
     */
    public static String[][] injectCenteredBlockAbove(String[][] pattern, char blockCode) {
        int len1 = pattern.length;
        int len2 = pattern[0].length;
        String[][] newPattern = new String[len1 + 1][len2];

        StringBuilder fillBuilder = new StringBuilder(StringUtils.repeat('_', len2));
        Arrays.fill(newPattern[0], fillBuilder.toString());
        System.arraycopy(pattern, 0, newPattern, 1, len1);

        int center = (len2 / 2) - ((len2 + 1) % 2);
        fillBuilder.setCharAt(center, blockCode);
        newPattern[0][center] = fillBuilder.toString();

        return newPattern;
    }
}
