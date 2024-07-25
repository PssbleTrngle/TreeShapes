package com.possible_triangle.tree_shapes.trunk

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.possible_triangle.tree_shapes.TreeShapesMod
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.util.RandomSource
import net.minecraft.world.level.LevelSimulatedReader
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.feature.configurations.TreeConfiguration
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacer.FoliageAttachment
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacer
import java.util.*
import java.util.function.BiConsumer
import kotlin.math.min

class RootedTrunkPlacer(
    baseHeight: Int,
    heightRandA: Int,
    heightRandB: Int,
    private val rootsProvider: Optional<BlockStateProvider>,
) : TrunkPlacer(baseHeight, heightRandA, heightRandB) {

    companion object {
        val CODEC: Codec<RootedTrunkPlacer> = RecordCodecBuilder.create { instance ->
            trunkPlacerParts(instance).and(BlockStateProvider.CODEC.optionalFieldOf("roots_provider")
                .forGetter { it.rootsProvider }).apply(instance, ::RootedTrunkPlacer)
        }
    }

    override fun type() = TreeShapesMod.ROOTED_TRUNK_TYPE.get()

    override fun placeTrunk(
        level: LevelSimulatedReader,
        setBlock: BiConsumer<BlockPos, BlockState>,
        random: RandomSource,
        height: Int,
        pos: BlockPos,
        config: TreeConfiguration,
    ): MutableList<FoliageAttachment> {
        val branchHeight = random.nextInt(4, 6)
        val trunkHeight = height - branchHeight
        val branchCount = random.nextInt(3, 7)

        for (i in 0..3) {
            createRoot(level, setBlock, random, pos, config)
        }

        if(random.nextBoolean()) {
            createRoot(level, setBlock, random, pos.above(trunkHeight - 2), config)
        }

        for (y in 0..height) {
            placeLog(level, setBlock, random, pos.above(y), config)
            placeLog(level, setBlock, random, pos.above(y).east(), config)
            placeLog(level, setBlock, random, pos.above(y).south(), config)
            placeLog(level, setBlock, random, pos.above(y).east().south(), config)
        }

        val topOfTrunk = pos.above(trunkHeight + 1)

        val branchEnds = (0 until branchCount).map {
            createBranch(level, setBlock, random, topOfTrunk, config, branchHeight - random.nextInt(2, 3))
        }

        val blob = pos.above(height).offset(
            random.nextInt(-2, 3),
            0,
            random.nextInt(-2, 3),
        )

        return (branchEnds
                + FoliageAttachment(pos.above(height + 4), 2, true)
                + FoliageAttachment(blob, 0, true)
                ).toMutableList()
    }

    private fun placeRoot(
        level: LevelSimulatedReader,
        setBlock: BiConsumer<BlockPos, BlockState>,
        random: RandomSource,
        pos: BlockPos,
        config: TreeConfiguration,
    ) {
        placeLog(level, setBlock, random, pos, config) { log ->
            rootsProvider.map { it.getState(random, pos) }.orElse(log)
        }
    }

    private fun createRoot(
        level: LevelSimulatedReader,
        setBlock: BiConsumer<BlockPos, BlockState>,
        random: RandomSource,
        pos: BlockPos,
        config: TreeConfiguration,
    ) {
        val rootX = random.nextInt(2) * 3 - 1
        val rootZ = random.nextInt(2)
        val at = if (random.nextBoolean()) pos.offset(rootX, 0, rootZ)
        else pos.offset(rootZ, 0, rootX)
        placeRoot(level, setBlock, random, at, config)
    }

    private fun createBranch(
        level: LevelSimulatedReader,
        setBlock: BiConsumer<BlockPos, BlockState>,
        random: RandomSource,
        pos: BlockPos,
        config: TreeConfiguration,
        length: Int,
    ): FoliageAttachment {
        val directionA = Direction.Plane.HORIZONTAL.getRandomDirection(random)
        val directionB = if (random.nextBoolean()) directionA.clockWise
        else directionA.counterClockWise

        val mutable = listOf(directionA, directionB).fold(pos) { it, direction ->
            when (direction) {
                Direction.EAST -> it.east()
                Direction.SOUTH -> it.south()
                else -> it
            }
        }.mutable()

        placeRoot(level, setBlock, random, mutable, config)

        for (i in 0 until length) {
            if (random.nextBoolean()) mutable.set(mutable.relative(directionA))
            if (random.nextBoolean()) mutable.set(mutable.relative(directionB))
            placeRoot(level, setBlock, random, mutable, config)

            val upAmount = min(2, random.nextInt(length - i))

            for (y in 0 until upAmount) {
                mutable.setY(mutable.y + 1)
                placeRoot(level, setBlock, random, mutable, config)
            }

            if (random.nextInt(4) < 2) {
                mutable.set(mutable.relative(directionA))
                if (random.nextBoolean()) placeRoot(level, setBlock, random, mutable, config)
            }

            if (random.nextBoolean()) {
                mutable.set(mutable.relative(directionB))
                if (random.nextBoolean()) placeRoot(level, setBlock, random, mutable, config)
            }
        }

        return FoliageAttachment(mutable.above(2), -1, false)
    }
}
