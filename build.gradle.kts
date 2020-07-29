plugins {
	java
	kotlin("jvm")
}

group = "mipsim"
version = "1.0-SNAPSHOT"
val mainCLass = "mipsim.console.Main"

repositories {
	jcenter()
	mavenCentral()
}

dependencies {
	implementation(kotlin("stdlib-jdk8"))
	implementation(project(":lib:SimKT", configuration = "default"))
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

val jar by tasks.getting(Jar::class) {
	manifest {
		attributes["Main-Class"] = mainCLass
	}
}

try {
	task("fatJar", type = Jar::class) {
		archiveBaseName.set("${project.name}-fat")
		manifest {
			attributes["Implementation-Title"] = "Gradle Fat Jar File"
			attributes["Implementation-Version"] = archiveVersion
			attributes["Main-Class"] = mainCLass
		}
//		from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
//		// from(main.output.classesDirs, main.compileDependencyFiles)
		with(tasks.jar.get() as CopySpec)
	}
} catch (_: Throwable) {
}
