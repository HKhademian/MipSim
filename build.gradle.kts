plugins {
	java
	kotlin("jvm") // version "1.3.72"
}

group = "ir.mipsim"
version = "1.0-SNAPSHOT"

repositories {
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(project(":lib:SimKT", configuration = "default"))
}

//configure<JavaPluginConvention> {
//	sourceCompatibility = JavaVersion.VERSION_1_8
//}

java {
	sourceCompatibility = JavaVersion.VERSION_14
	targetCompatibility = JavaVersion.VERSION_14
}

tasks {
	compileKotlin {
		kotlinOptions.jvmTarget = "1.8"
	}
	compileTestKotlin {
		kotlinOptions.jvmTarget = "1.8"
	}
}
