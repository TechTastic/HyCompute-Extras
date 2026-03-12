import dev.scaffoldit.hytale.wire.HytaleManifest

rootProject.name = "HyCompute-Extras"

plugins {
    // See documentation on https://scaffoldit.dev
    id("dev.scaffoldit") version "0.2.+"
}

// Would you like to do a split project?
// Create a folder named "common", then configure details with `common { }`

hytale {
    usePatchline("release")
    useVersion("latest")

    repositories {
        // Any external repositories besides: MavenLocal, MavenCentral, HytaleMaven, and CurseMaven
        exclusiveContent {
            forRepository {
                ivy {
                    name = "Modtale"
                    url = uri("https://api.modtale.net/api/v1")
                    patternLayout {
                        artifact("projects/[module]/versions/[revision]/download")
                    }
                    metadataSources {
                        artifact()
                    }
                }
            }
            filter {
                includeGroup("modtale")
            }
        }
    }

    dependencies {
        // Any external dependency you also want to include
        compileOnly("modtale:3e453e84-81af-46bf-9e7d-c7de99d9c4f0:0.1.0-alpha@jar")
    }

    manifest {
        Group = "TechTastic"
        Name = "HyComputeExtras"
        Main = "io.github.techtastic.hycomputeextras.HyComputeExtras"
        Version = "1.0.0"
        Description = "Allows third party mods to create APIs!"
        Authors = listOf(HytaleManifest.Author("TechTastic"))
        Website = "https://github.com/TechTastic/HyCompute-Extras"
    }
}
