package com.invadermonky.sanguissupremus.rituals;

import WayofTime.bloodmagic.entity.projectile.EntityMeteor;
import WayofTime.bloodmagic.meteor.Meteor;
import WayofTime.bloodmagic.meteor.MeteorRegistry;
import WayofTime.bloodmagic.ritual.*;
import WayofTime.bloodmagic.util.Utils;
import com.invadermonky.sanguissupremus.config.ConfigHandlerSS;
import com.invadermonky.sanguissupremus.core.mixins.MixinEntityMeteor;
import com.invadermonky.sanguissupremus.util.LogHelper;
import com.invadermonky.sanguissupremus.util.libs.LibNames;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.function.Consumer;

@RitualRegister(LibNames.RITUAL_METEOR_CATCHER)
public class RitualMeteorCatcher extends AbstractRitualSS{
    public static final String METEOR_RANGE = "meteorRange";

    private Meteor meteor;

    public RitualMeteorCatcher() {
        super(LibNames.RITUAL_METEOR_CATCHER, 2, ConfigHandlerSS.rituals.cradle_of_the_blood_moon.activationCost, ConfigHandlerSS.rituals.cradle_of_the_blood_moon.refreshCost, 1);
        this.addBlockRange(METEOR_RANGE, new AreaDescriptor.Rectangle(new BlockPos(0, 1, 0), 0, 25, 0));
    }

    @Override
    public void performRitual(IMasterRitualStone masterRitualStone) {
        World world = masterRitualStone.getWorldObj();
        if(!world.isRemote) {
            BlockPos mrsPos = masterRitualStone.getBlockPos();
            List<EntityMeteor> meteors = world.getEntitiesWithinAABB(EntityMeteor.class, masterRitualStone.getBlockRange(METEOR_RANGE).getAABB(mrsPos), Entity::isEntityAlive);
            for(EntityMeteor meteorEntity : meteors) {
                if(this.hasInsufficientLP(masterRitualStone)) return;

                if(this.meteor == null) {
                    this.meteor = MeteorRegistry.getMeteorForItem(meteorEntity.meteorStack);
                }

                double radiusModifier = 1.0;
                double fillerChance = 0.0;
                try {
                    radiusModifier = ((MixinEntityMeteor) meteorEntity).getRadiusModifier();
                    fillerChance = ((MixinEntityMeteor) meteorEntity).getFillerChance();
                } catch (Exception e) {
                    LogHelper.error("Error occurred while attempting to process meteor catching.");
                    e.printStackTrace();
                }

                BlockPos meteorPos = meteorEntity.getPosition();
                int yStopOffset = 10 + (int) Math.ceil((double) this.meteor.getRadius() * radiusModifier);
                if(meteorPos.getY() <= mrsPos.add(0, yStopOffset, 0).getY()) {


                    if(this.meteor != null) {
                        generateMeteorWithoutExplosion(world, meteorPos, this.meteor, radiusModifier, fillerChance);
                        masterRitualStone.getOwnerNetwork().syphon(masterRitualStone.ticket(this.getRefreshCost()));
                        meteorEntity.setDead();
                        this.meteor = null;
                    }
                }
            }
        }
    }

    public static void generateMeteorWithoutExplosion(World world, BlockPos pos, Meteor meteor, double radiusModifier, double fillerChance) {
        IBlockState fillerBlock = Blocks.STONE.getDefaultState();
        int radius = (int)Math.ceil((double)meteor.getRadius() * radiusModifier);
        double floatingRadius = (double)meteor.getRadius() * radiusModifier;

        for(int i = -radius; i <= radius; ++i) {
            for(int j = -radius; j <= radius; ++j) {
                for(int k = -radius; k <= radius; ++k) {
                    if (!((double)(i * i + j * j + k * k) > (floatingRadius + 0.5) * (floatingRadius + 0.5))) {
                        BlockPos newPos = pos.add(i, j, k);
                        IBlockState state = world.getBlockState(newPos);
                        if (world.isAirBlock(newPos) || Utils.isBlockLiquid(state)) {
                            IBlockState placedState = meteor.getRandomOreFromComponents(fillerBlock, fillerChance);
                            if (placedState != null) {
                                world.setBlockState(newPos, placedState);
                            }
                        }
                    }
                }
            }
        }
    }

    public void gatherRitualMeteorComponents(Consumer<RitualComponent> components) {
        this.addParallelRunes(components, 2, -5, EnumRuneType.FIRE);
        this.addOffsetRunes(components, 3, 1, -5, EnumRuneType.AIR);
        this.addOffsetRunes(components, 4, 2, -5, EnumRuneType.AIR);
        this.addOffsetRunes(components, 5, 3, -5, EnumRuneType.DUSK);
        this.addCornerRunes(components, 4, -5, EnumRuneType.DUSK);

        for(int i = 4; i <= 6; ++i) {
            this.addParallelRunes(components, 4, -5, EnumRuneType.EARTH);
        }

        this.addParallelRunes(components, 8, -5, EnumRuneType.EARTH);
        this.addParallelRunes(components, 8, -4, EnumRuneType.EARTH);
        this.addParallelRunes(components, 7, -4, EnumRuneType.EARTH);
        this.addParallelRunes(components, 7, -3, EnumRuneType.EARTH);
        this.addParallelRunes(components, 6, -3, EnumRuneType.FIRE);
        this.addParallelRunes(components, 6, -2, EnumRuneType.WATER);
        this.addParallelRunes(components, 5, -2, EnumRuneType.WATER);
        this.addParallelRunes(components, 5, -1, EnumRuneType.AIR);
        this.addOffsetRunes(components, 1, 4, -1, EnumRuneType.AIR);
        this.addParallelRunes(components, 4, -1, EnumRuneType.AIR);
        this.addOffsetRunes(components, 2, 4, -1, EnumRuneType.WATER);
        this.addOffsetRunes(components, 2, 3, -1, EnumRuneType.FIRE);
        this.addCornerRunes(components, 3, -1, EnumRuneType.FIRE);
    }

    @Override
    public void gatherComponents(Consumer<RitualComponent> components) {
        this.gatherRitualMeteorComponents(components);

        this.addParallelRunes(components, 1, 0, EnumRuneType.BLANK);
        this.addParallelRunes(components, 4, 0, EnumRuneType.DUSK);
        this.addCornerRunes(components, 2, 0, EnumRuneType.DUSK);

        this.addParallelRunes(components, 2, 1, EnumRuneType.DUSK);
        this.addParallelRunes(components, 3, 1, EnumRuneType.WATER);
        this.addCornerRunes(components, 2, 1, EnumRuneType.WATER);
        this.addOffsetRunes(components, 3, 1, 1, EnumRuneType.FIRE);

        this.addParallelRunes(components, 3, 2, EnumRuneType.EARTH);
        this.addCornerRunes(components, 2, 2, EnumRuneType.EARTH);

        this.addParallelRunes(components, 3, 3, EnumRuneType.EARTH);
        this.addCornerRunes(components, 2, 3, EnumRuneType.EARTH);

        this.addParallelRunes(components, 3, 4, EnumRuneType.FIRE);
        this.addParallelRunes(components, 4, 4, EnumRuneType.AIR);
        this.addCornerRunes(components, 2, 4, EnumRuneType.FIRE);
        this.addOffsetRunes(components, 3, 1, 4, EnumRuneType.WATER);

        this.addParallelRunes(components, 4, 5, EnumRuneType.AIR);
        this.addParallelRunes(components, 5, 5, EnumRuneType.DUSK);

        this.addParallelRunes(components, 5, 6, EnumRuneType.DUSK);
    }

    @Override
    public Ritual getNewCopy() {
        return new RitualMeteorCatcher();
    }
}
