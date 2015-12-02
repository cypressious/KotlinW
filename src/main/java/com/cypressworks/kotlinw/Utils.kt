package com.cypressworks.kotlinw

import com.sun.javafx.PlatformUtil
import java.io.File

val userDir: File
    get() = if (PlatformUtil.isWindows()) {
        File(System.getenv("APPDATA"))
    } else {
        File(System.getProperty("user.home"))
    }

val dataDir: File
    get() = File(userDir, getDotIfLinux() + "kotlinw")

private fun getDotIfLinux() = if (PlatformUtil.isLinux()) "." else ""