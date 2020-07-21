plugins {
	java
	kotlin("jvm")
}

group = "ir.mipsim"
version = "1.0-SNAPSHOT"

repositories {
	jcenter()
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(project(":lib:SimKT", configuration = "default"))

	testImplementation(kotlin("test"))
	testImplementation(kotlin("test-junit"))
}


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
