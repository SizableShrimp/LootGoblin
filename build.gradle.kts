import com.vanniktech.maven.publish.MavenPublishPluginExtension
import com.vanniktech.maven.publish.SonatypeHost
import net.minecraftforge.gradle.userdev.UserDevExtension
import org.gradle.jvm.toolchain.JvmVendorSpec.ADOPTOPENJDK
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.time.Instant.now
import java.time.format.DateTimeFormatter.ISO_INSTANT

plugins {
	idea
	`java-library`
	kotlin("jvm") version "1.5.30"
	id("net.minecraftforge.gradle")
	id("com.vanniktech.maven.publish")
}

// Config -> Minecraft
val forgeVersion: String by extra
val minecraftVersion: String by extra
val projectVersion = findProperty("VERSION_NAME") as String
val projectGroup = findProperty("GROUP") as String
val modId = findProperty("POM_ARTIFACT_ID") as String
val projectName = findProperty("POM_NAME") as String
val projectAuthor = findProperty("POM_DEVELOPER_NAME") as String

val testModId = "lootgoblin_test"

// JVM Info
println(
	"""
		Java: ${System.getProperty("java.version")} 
		JVM: ${System.getProperty("java.vm.version")} (${System.getProperty("java.vendor")}) 
		Arch: ${System.getProperty("os.arch")}
	""".trimIndent()
)

dependencies {
	"minecraft"(group = "net.minecraftforge", name = "forge", version = "$minecraftVersion-$forgeVersion")
}

// Minecraft
configure<UserDevExtension> {
	mappings("official", minecraftVersion)

	// @Suppress("SpellCheckingInspection")
	// accessTransformer(file("src/main/resources/META-INF/accesstransformer.cfg"))

	runs {
		create("data") {
			workingDirectory(file("run"))

			taskName = "datagen"

			// Recommended logging data for a userdev environment
			property("forge.logging.markers", "SCAN,REGISTRIES,REGISTRYDUMP")

			// Recommended logging level for the console
			property("forge.logging.console.level", "debug")

			// Specify the mod id for data generation, where to output the resulting resource, and where to look for existing resources.
			args(
				"--mod",
				testModId,
				"--all",
				"--output",
				file("src/generated/resources/"),
				"--existing",
				file("src/main/resources/")
			)

			mods {
				create(testModId) {
					source(sourceSets["test"])
				}
			}
		}
	}
}

repositories {
	maven {
		name = "kotlinforforge"
		url = uri("https://thedarkcolour.github.io/KotlinForForge/")
	}
	mavenCentral()
}

// Setup
project.group = projectGroup
project.version = projectVersion
base.archivesName.set(modId)

// Sets the toolchain to compile against OpenJDK 16
java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(16))
		vendor.set(ADOPTOPENJDK)
	}
}

tasks.withType<KotlinCompile>().configureEach {
	kotlinOptions.jvmTarget = "16"
}

// Finalize the jar by re-obfuscating
tasks.named<Jar>("jar") {
	// Manifest
	manifest {
		attributes(
			"Specification-Title" to projectName,
			"Specification-Vendor" to projectAuthor,
			"Specification-Version" to "1",
			"Implementation-Title" to projectName,
			"Implementation-Version" to project.version,
			"Implementation-Vendor" to projectName,
			"Implementation-Timestamp" to ISO_INSTANT.format(now()),
			"FMLModType" to "LIBRARY"
		)
	}

	finalizedBy("reobfJar")
}

// Publishing to maven central
extensions.getByType<MavenPublishPluginExtension>().sonatypeHost = SonatypeHost.S01