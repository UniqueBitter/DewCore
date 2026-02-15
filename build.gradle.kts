import io.izzel.taboolib.gradle.*
import io.izzel.taboolib.gradle.DatabasePlayer
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
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
            name("Unique_Bitter")
        }
    }
    version { taboolib = "6.2.3" }
}


repositories {
    mavenCentral()
    maven("https://repo.tabooproject.org/repository/releases/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://nexus.maplex.top/repository/maven-public/")
    maven("https://repo.purpurmc.org/snapshots")
    // Purpur 官方仓库 (针对 1.21.1 必须包含)
    maven("https://repo.purpurmc.org/snapshots")
    // Paper 官方仓库
    maven("https://repo.papermc.io/repository/maven-public/")
    // TabooLib 仓库
    maven("https://repo.ptms.ink/repository/maven-releases/")
    // 阿里云镜像 (加速一些基础库的下载)
    maven("https://maven.aliyun.com/repository/public")
}

dependencies {
    compileOnly("org.purpurmc.purpur:purpur-api:1.21.1-R0.1-SNAPSHOT")
    compileOnly("ink.ptms.core:v12100:12100:universal")
    compileOnly(kotlin("stdlib"))
    compileOnly(fileTree("libs"))
    compileOnly("com.google.code.gson:gson:2.10.1")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}


// 服务器插件目录
val serverPluginsDir = "C:/Users/Administrator/Desktop/DewTestServerold/plugins"

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