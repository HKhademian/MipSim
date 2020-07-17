rootProject.name = "MipSim"
include(":lib:SimKT")

pluginManagement {
	repositories {
		gradlePluginPortal()
	}
	plugins {
		kotlin("jvm") version "1.3.72"
	}
}

