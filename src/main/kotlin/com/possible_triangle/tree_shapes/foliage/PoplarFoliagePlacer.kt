package com.possible_triangle.tree_shapes.foliage

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.possible_triangle.tree_shapes.TreeShapesMod.POPLAR_FOLIAGE_TYPE
import net.minecraft.core.BlockPos
import net.minecraft.core.BlockPos.MutableBlockPos
import net.minecraft.util.RandomSource
import net.minecraft.util.valueproviders.IntProvider
import net.minecraft.world.level.LevelSimulatedReader
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer
import kotlin.math.max
import kotlin.math.min

class PoplarFoliagePlacer(radius: IntProvider, offset: IntProvider, private val height: IntProvider) :
    FoliagePlacer(radius, offset) {

    companion object {
        val CODEC: Codec<PoplarFoliagePlacer> =
            RecordCodecBuilder.create { instance ->
                foliagePlacerParts(instance).and(IntProvider.codec(0, 24).fieldOf("height").forGetter { it.height })
                    .apply(instance, ::PoplarFoliagePlacer)
            }
    }

    override fun type() = POPLAR_FOLIAGE_TYPE.get()

    override fun createFoliage(
        level: LevelSimulatedReader,
        setBlock: FoliageSetter,
        random: RandomSource,
        config: TreeConfiguration,
        something: Int,
        attachment: FoliageAttachment,
        height: Int,
        radius: Int,
        offset: Int,
    ) {
        val size = max(radius.toFloat() + attachment.radiusOffset(), 1.5F)
        val mid = height / 3
        val padding = 1

        for (y in 0..height) {
            val factor = if (y > mid) {
                (height - y) / (height - mid - padding).toFloat()
            } else {
                y / mid.toFloat()
            }

            placeLeaves(
                level,
                setBlock,
                random,
                config,
                attachment.pos(),
                min(factor, 1F) * size,
                y + offset - height / 2 - radius / 2,
                attachment.doubleTrunk()
            )
        }
    }

    private fun placeLeaves(
        level: LevelSimulatedReader,
        setBlock: FoliageSetter,
        random: RandomSource,
        config: TreeConfiguration,
        pos: BlockPos,
        size: Float,
        y: Int,
        doubleTrunk: Boolean,
    ) {
        val offset = if (doubleTrunk) 1 else 0
        val mutable = MutableBlockPos()
        val radius = size.toInt()

        for (x in -radius..radius + offset) {
            for (z in -radius..radius + offset) {
                val skipEdges = (size - radius - random.nextFloat() * 0.2F) < 0.3F
                if (skipEdges && shouldSkipLocationSigned(random, x, y, z, radius, doubleTrunk)) continue

                mutable.setWithOffset(pos, x, y, z)
                tryPlaceLeaf(level, setBlock, random, config, mutable)
            }
        }
    }

    override fun shouldSkipLocation(
        random: RandomSource,
        x: Int,
        y: Int,
        z: Int,
        radius: Int,
        doubleTrunk: Boolean,
    ): Boolean {
        return x == radius && z == radius
    }

    override fun foliageHeight(random: RandomSource, trunkHeight: Int, config: TreeConfiguration): Int {
        return height.sample(random)
    }
}
