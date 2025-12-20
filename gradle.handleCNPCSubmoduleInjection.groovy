sourceSets {
    main {
        java {
            srcDirs += ["CustomNPC-Plus/src/api/java", "CustomNPC-Plus/src/main/java"]
        }
        resources {
            srcDirs += ["CustomNPC-Plus/src/main/resources"]
        }
    }
}

println "BUILDS WILL HAPPEN WITH CNPC SOURCE, NOT JARS"

import groovy.json.JsonSlurper
import groovy.json.JsonOutput
import java.util.regex.Pattern
import java.util.regex.Matcher


// Task to merge mcmod.info files
tasks.register("mergeMcmodInfo") {
    doLast {
        def mergedModList = []

        // List all resource folders containing mcmod.info
        def resourceFolders = [
            file("src/main/resources"),
            file("CustomNPC-Plus/src/main/resources")
        ]

        resourceFolders.each { resDir ->
            def mcmodFile = new File(resDir, "mcmod.info")
            if (mcmodFile.exists()) {
                def propsFile = resDir.toPath().resolve("../../../gradle.properties").normalize().toFile()
                def props = [:]
                if (propsFile.exists()) {
                    propsFile.withReader("UTF-8") { reader ->
                        new Properties().with { p ->
                            p.load(reader)
                            p.each { k, v -> props[k] = v }
                        }
                    }
                }

                // Read mcmod.info as text
                def text = mcmodFile.text

                // Replace placeholders safely using Pattern.quote and Matcher.quoteReplacement
                props.each { key, value ->
                    def pattern = Pattern.compile('\\$\\{' + Pattern.quote(key) + '\\}')
                    def matcher = pattern.matcher(text)
                    text = matcher.replaceAll(Matcher.quoteReplacement(value))
                }


                // Parse JSON and extract modList
                def json = new JsonSlurper().parseText(text)
                if (json.modList) {
                    mergedModList.addAll(json.modList)
                }
            }
        }

        // Create merged mcmod.info JSON
        def outputJson = [
            modListVersion: 2,
            modList: mergedModList
        ]

        def outputDir = file("$buildDir/generatedResources/main")
        outputDir.mkdirs()
        def outputFile = new File(outputDir, "mcmod.info")
        outputFile.text = JsonOutput.prettyPrint(JsonOutput.toJson(outputJson))
//        println JsonOutput.prettyPrint(JsonOutput.toJson(mergedModList))
        println "Merged ${mergedModList.size()} modList entries into ${outputFile}"
    }
}

// Hook into processResources to prevent duplicates
tasks.named("processResources") {
    dependsOn tasks.named("mergeMcmodInfo")

    // Exclude original mcmod.info files
    from(sourceSets.main.resources.srcDirs) { exclude "mcmod.info" }

    // Include merged mcmod.info
    from("$buildDir/generatedResources/main") { include "mcmod.info" }

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}




// Hook into sourcesJar to prevent duplicates there as well
tasks.named("sourcesJar") {
    dependsOn(tasks.named("processResources"))
    from(sourceSets.main.allSource) { exclude "mcmod.info" }
    from("$buildDir/resources/main") { include "mcmod.info" }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

def atFiles = [
    file("src/main/resources/META-INF/npcdbc_at.cfg"),
    file("CustomNPC-Plus/src/main/resources/META-INF/customnpcs_at.cfg")
]

jar {
    dependsOn(tasks.named("processResources"))
    manifest {
        attributes(
            'FMLAT': atFiles.collect { it.name }.join(' ')
        )
    }
}
reobfJar {
    dependsOn(tasks.named("processResources"))
    manifest {
        attributes(
            'FMLAT': atFiles.collect {it.name}.join(' ')
        )
    }
}

project.afterEvaluate {
    tasks.named("processResources") {
        doLast {
            // Path to your generated/merged mcmod.info
            def generatedFile = file("$buildDir/generatedResources/main/mcmod.info")
            def targetFile = file("$buildDir/resources/main/mcmod.info")

            if (!generatedFile.exists()) {
                throw new GradleException("Generated mcmod.info not found: $generatedFile")
            }

            // Overwrite the plugin-expanded mcmod.info
            targetFile.text = generatedFile.text
            println "Replaced mcmod.info with generated version after plugin expansion"
        }
    }
}

tasks.named("runClient").configure {
    dependsOn(tasks.named("processResources"))
    extraArgs.addAll("--tweakClass",
        "org.spongepowered.asm.launch.MixinTweaker",
        "--mixin",
        "mixins.customnpcs.json")
}
