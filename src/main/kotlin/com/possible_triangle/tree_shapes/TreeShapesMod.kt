package com.possible_triangle.tree_shapes

import com.possible_triangle.tree_shapes.foliage.AdjustingBlobFoliagePlacer
import com.possible_triangle.tree_shapes.foliage.PoplarFoliagePlacer
import com.possible_triangle.tree_shapes.trunk.RootedTrunkPlacer
import net.minecraft.core.Registry
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.registries.DeferredRegister
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(TreeShapesMod.MOD_ID)
object TreeShapesMod {

    const val MOD_ID: String = "tree_shapes"
    val LOGGER: Logger = LogManager.getLogger()

    private val FOLIAGE_PLACERS = DeferredRegister.create(Registry.FOLIAGE_PLACER_TYPE_REGISTRY, MOD_ID)
    private val TRUNK_PLACERS = DeferredRegister.create(Registry.TRUNK_PLACER_TYPE_REGISTRY, MOD_ID)

    val POPLAR_FOLIAGE_TYPE = FOLIAGE_PLACERS.register("poplar") { FoliagePlacerType(PoplarFoliagePlacer.CODEC) }
    val BLOB_FOLIAGE_TYPE = FOLIAGE_PLACERS.register("blob") { FoliagePlacerType(AdjustingBlobFoliagePlacer.CODEC) }

    val ROOTED_TRUNK_TYPE = TRUNK_PLACERS.register("rooted") { TrunkPlacerType(RootedTrunkPlacer.CODEC) }

    init {
        FOLIAGE_PLACERS.register(MOD_BUS)
        TRUNK_PLACERS.register(MOD_BUS)
    }
}