import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.DatabasePlayer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_1_8
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile


plugins {
    java
    id("io.izzel.taboolib") version "2.0.27"
    id("org.jetbrains.kotlin.jvm") version "2.2.0"
}

taboolib {
    env {
        install(Basic)
        install(CommandHelper)
        install(MinecraftChat)
        install(MinecraftEffect)
        install(Bukkit)
        install(Database)
        install(DatabasePlayer)
        install(BukkitUI)
        install(BukkitHook)
        install(BukkitFakeOp)

    }
    description {
        name = "DewCore"
        contributors {
            name("Administrator")
        }
    }
    version { taboolib = "6.2.3" }
}

repositories {
    mavenCentral()
    maven("https://repo.tabooproject.org/repository/releases/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("ink.ptms.core:v12004:12004:mapped")
    compileOnly("ink.ptms.core:v12004:12004:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JVM_1_8)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}


// 服务器插件目录
val serverPluginsDir = "C:/Users/Administrator/Desktop/DewTestServer/plugins"

// 构建完成后自动复制到服务器
tasks.build {
    doLast {
        val jarFile = tasks.jar.get().archiveFile.get().asFile
        val targetDir = file(serverPluginsDir)

        if (targetDir.exists()) {
            // 删除旧版本（可选，防止多版本共存）
            targetDir.listFiles()?.filter {
                it.name.startsWith("DewCore") && it.extension == "jar"
            }?.forEach { it.delete() }

            // 复制新版本
            jarFile.copyTo(File(targetDir, jarFile.name), overwrite = true)
            println("✓ 已部署到: ${targetDir.absolutePath}/${jarFile.name}")
        } else {
            println("✗ 目标目录不存在: $serverPluginsDir")
        }
    }
}