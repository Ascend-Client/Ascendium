package io.github.betterclient.ascendium.util

import io.github.betterclient.ascendium.Logger
import kotlinx.io.IOException
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest

object SkiaRuntimeDownloader {
    const val version = "0.9.30"
    private val platforms = listOf(
        "linux-x64",
        "linux-arm64",
        "windows-x64",
        "windows-arm64",
        "macos-arm64",
        "macos-x64"
    )
    private fun buildUrl(platform: String, suffix: String): String {
        val artifact = "skiko-awt-runtime-$platform"
        return "https://repo1.maven.org/maven2/org/jetbrains/skiko/$artifact/$version/$artifact-$version$suffix"
    }

    private val runtimes: Map<String, String> = platforms.associateWith { platform ->
        buildUrl(platform, ".jar")
    }

    private val runtimeHashes: Map<String, String> = platforms.associateWith { platform ->
        buildUrl(platform, ".jar.sha256")
    }

    fun download() {
        val dir = File(".ascendium")
        val dir2 = File(dir, "skia")
        if (!dir.exists() || !dir2.exists()) {
            dir.mkdirs()
            dir2.mkdirs()
        }

        val runtime = getRuntimeForCurrentPlatform(dir2.toPath(), { s -> Logger.info(s) })
        val classLoader = SkiaRuntimeDownloader::class.java.classLoader
        val urlLoader = classLoader.javaClass.getDeclaredField("urlLoader").also { it.isAccessible = true }.get(classLoader)
        urlLoader.javaClass.getMethod("addURL", URL::class.java).also { it.isAccessible = true }.invoke(
            urlLoader, runtime.toUri().toURL()
        )
    }

    private fun getRuntimeForCurrentPlatform(cacheDirectory: Path, log: (String) -> Unit, n: Int = 0): Path {
        val currentPlatform = detectCurrentPlatform()
        log("Detected platform: $currentPlatform")

        val downloadUrl = runtimes[currentPlatform]
            ?: throw IllegalStateException("No runtime URL found for platform '$currentPlatform'")
        val hashUrl = runtimeHashes[currentPlatform]
            ?: throw IllegalStateException("No runtime hash URL found for platform '$currentPlatform'")

        val fileName = downloadUrl.substringAfterLast('/')
        val localFilePath = cacheDirectory.resolve(fileName)

        Files.createDirectories(cacheDirectory)

        try {
            val expectedHash = fetchRemoteText(hashUrl)
            log("Expected SHA256 hash: $expectedHash")

            if (Files.exists(localFilePath)) {
                val localFileHash = calculateSha256(localFilePath)
                if (localFileHash == expectedHash) {
                    log("Local file found and hash is valid: $localFilePath")
                    return localFilePath
                } else {
                    log("Local file hash mismatch (is: $localFileHash). Re-downloading...")
                    Files.delete(localFilePath)
                }
            }

            log("Downloading runtime from $downloadUrl...")
            downloadFile(downloadUrl, localFilePath)
            log("Download complete. Verifying file...")

            val downloadedFileHash = calculateSha256(localFilePath)
            if (downloadedFileHash != expectedHash) {
                Files.delete(localFilePath)
                if (n == 3) {
                    throw IOException("Downloaded runtime repeatedly didn't match expected hash.")
                }
                log("Downloaded file doesn't match expected hash, retrying n=$n")
                return getRuntimeForCurrentPlatform(cacheDirectory, log, n + 1)
            }

            log("File verified successfully. Runtime is ready at: $localFilePath")
            return localFilePath

        } catch (e: IOException) {
            log("No internet connection detected. Checking for cached runtime...")
            if (Files.exists(localFilePath)) {
                log("Found cached runtime. Using without online verification: $localFilePath")
                return localFilePath
            } else {
                throw IOException("No internet connection, and the required runtime is not cached.", e)
            }
        }
    }

    private fun detectCurrentPlatform(): String {
        val osName = System.getProperty("os.name").lowercase()
        val osArch = System.getProperty("os.arch").lowercase()

        val os = when {
            osName.contains("win") -> "windows"
            osName.contains("mac") -> "macos"
            osName.contains("nix") || osName.contains("nux") || osName.contains("aix") -> "linux"
            else -> throw IllegalStateException("Unsupported operating system: $osName")
        }

        val arch = when (osArch) {
            "amd64", "x86_64" -> "x64"
            "aarch64" -> "arm64"
            else -> throw IllegalStateException("Unsupported architecture: $osArch")
        }

        val platform = "$os-$arch"
        if (platform !in platforms) {
            throw IllegalStateException("The platform '$platform' is not supported by this application.")
        }
        return platform
    }

    private fun downloadFile(url: String, destination: Path) {
        println(url)
        URI(url).toURL().openStream().use { input ->
            Files.newOutputStream(destination).use { output ->
                input.copyTo(output)
            }
        }
    }

    private fun fetchRemoteText(url: String): String {
        println(url)
        return URI(url).toURL().readText().trim()
    }

    private fun calculateSha256(filePath: Path): String {
        val digest = MessageDigest.getInstance("SHA-256")
        Files.newInputStream(filePath).use { fis ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (fis.read(buffer).also { bytesRead = it } != -1) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
