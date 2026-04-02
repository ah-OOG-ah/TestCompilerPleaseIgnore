plugins {
    id("java")
    id("application")
}

val grp = "klaxon.klaxon.jbest"
group = grp
version = "1.0-SNAPSHOT"

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    testImplementation("com.github.AharonSambol:PrettyPrintTreeJava:c357a94eb9")
}

application {
    mainClass = "$grp.Main"
}

tasks.withType<JavaExec> {
    args = listOf("test.c")
}

tasks.test {
    useJUnitPlatform()
}