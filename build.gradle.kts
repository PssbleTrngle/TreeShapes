plugins {
    id("com.possible-triangle.forge")
}

withKotlin()

dependencies {
    if (!env.isCI) {
        modRuntimeOnly(pack.modrinth.biomes.o.plenty)
        modRuntimeOnly(pack.modrinth.glitchcore)
        modRuntimeOnly(pack.modrinth.terrablender)
        modRuntimeOnly(pack.modrinth.treeplacer)
        modRuntimeOnly(pack.modrinth.botania)
        modRuntimeOnly(pack.modrinth.patchouli)
        modRuntimeOnly(pack.modrinth.curios)
    }
}

upload {
    maven.nexus()
    modrinth.syncBodyFromReadme()
}

enableSpotless()
