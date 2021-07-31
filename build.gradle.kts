plugins {
    java
}

group = "com.jaspervanmerle.ptp2021"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}

task<JavaExec>("runAll") {
    group = "run"

    classpath = java.sourceSets["main"].runtimeClasspath
    mainClass.set("${project.group}.Runner")
}

for (i in 6..30) {
    task<JavaExec>("run${i.toString().padStart(2, '0')}") {
        group = "run"

        classpath = java.sourceSets["main"].runtimeClasspath
        mainClass.set("${project.group}.Runner")

        args = listOf("$i")
    }
}
