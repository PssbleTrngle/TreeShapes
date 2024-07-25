val mod_id: String by extra
val mc_version: String by extra
val mod_version: String by extra
val jei_version: String by extra

plugins {
    id("com.possible-triangle.gradle") version ("0.1.4")
}

withKotlin()

forge {
    dataGen()
}

repositories {
    modrinthMaven()

    maven {
        url = uri("https://maven.blamejared.com/")
        content {
            includeGroup("mezz.jei")
        }
    }
}

dependencies {
    if (!env.isCI) {
        modRuntimeOnly("maven.modrinth:biomes-o-plenty:T0achJ6F")
        modRuntimeOnly("maven.modrinth:terrablender:qpCqqA93")
        modRuntimeOnly("maven.modrinth:treeplacer:S2Yqxmo6")
        modRuntimeOnly("maven.modrinth:botania:ruMuBKgi")
        modRuntimeOnly("maven.modrinth:patchouli:ruMuBKgi")
        modRuntimeOnly("maven.modrinth:patchouli:62ztr7HA")
        modRuntimeOnly("maven.modrinth:curios:U8r7nIbi")
        modRuntimeOnly("maven.modrinth:ecologics:hOFm4e6B")
    }
}

enablePublishing {
    githubPackages()
}

uploadToCurseforge()
uploadToModrinth {
    syncBodyFromReadme()
}