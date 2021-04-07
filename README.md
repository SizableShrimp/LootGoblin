[![](https://jitpack.io/v/TheOnlyTails/LootTables.svg)](https://jitpack.io/#TheOnlyTails/LootTables)

# LootTables

A Kotlin DSL for creating loot tables in Minecraft Forge mods.

For documentation and usage instructions, please take a look at the [wiki](https://github.com/TheOnlyTails/LootTables/wiki).

## Installation

###### Don't forget to replace the VERSION key with the version in the JitPack badge at the top!

#### Gradle/Groovy

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation fg.deobf(project.dependencies.create(group: "com.github.TheOnlyTails", name: "LootTables", version: VERSION) {
	transitive = false
    })
}
```

#### Gradle/Kotlin
```kotlin
repositories {
    maven(url = "https://jitpack.io")
}

dependencies {
    implementation(project.the<DependencyManagementExtension>()
	.deobf(project.dependencies.create(group = "com.github.TheOnlyTails", name = "LootTables", version = VERSION)
		.apply {
			isTransitive = false
		}
	))
}
```
