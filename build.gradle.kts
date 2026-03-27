plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "Core"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.6"
val jomlVersion = "1.10.8"

val lwjglNatives = "natives-windows"
repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))

    implementation("org.lwjgl:lwjgl")
    implementation("org.lwjgl:lwjgl-glfw")
    implementation("org.lwjgl:lwjgl-opengl")
    implementation("org.lwjgl:lwjgl-stb")
    implementation ("org.lwjgl:lwjgl::$lwjglNatives")
    implementation ("org.lwjgl:lwjgl-glfw::$lwjglNatives")
    implementation ("org.lwjgl:lwjgl-opengl::$lwjglNatives")
    implementation ("org.lwjgl:lwjgl-stb::$lwjglNatives")
    implementation("org.joml:joml:$jomlVersion")
}

tasks.test {
    useJUnitPlatform()
}