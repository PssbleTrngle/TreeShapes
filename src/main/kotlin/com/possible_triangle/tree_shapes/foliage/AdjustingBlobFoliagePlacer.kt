package com.possible_triangle.tree_shapes.foliage

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.possible_triangle.tree_shapes.TreeShapesMod
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.level.LevelSimulatedReader
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
import net.minecraft.world.level.levelgen.feature.foliageplacers.FancyFoliagePlacer
import java.util.function.BiConsumer

class AdjustingBlobFoliagePlacer(radius: IntProvider, offset: IntProvider, height: Int) :
    FancyFoliagePlacer(radius, offset, height) {

    override fun type() = TreeShapesMod.BLOB_FOLIAGE_TYPE.get()

    companion object {
        val CODEC: Codec<AdjustingBlobFoliagePlacer> =
            RecordCodecBuilder.create { instance ->
                blobParts(instance).apply(instance, ::AdjustingBlobFoliagePlacer)
            }
    }

    override fun createFoliage(
        level: LevelSimulatedReader,
        setBlock: BiConsumer<BlockPos, BlockState>,
        random: RandomSource,
        config: TreeConfiguration,
        something: Int,
        attachment: FoliageAttachment,
        height: Int,
        radius: Int,
        offset: Int,
    ) {
        super.createFoliage(
            level,
            setBlock,
            random,
            config,
            something,
            attachment,
            height + attachment.radiusOffset(),
            radius + attachment.radiusOffset(),
            offset,
        )
    }
}
