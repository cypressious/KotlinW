package com.cypressworks.kotlinw


fun main(args: Array<String>) {

    val cpCompiler = Maven.getClassPath(artifactName = "kotlin-compiler", version = "[0,)")
    val cpStdLib = Maven.getClassPath(artifactName = "kotlin-stdlib", version = "[0,)")

    val pb = ProcessBuilder(listOf("java", "-jar", cpCompiler,
            "-classpath", cpStdLib, "-nowarn", "-script")
            + args)

    pb.inheritIO()
    //    println(pb.command().joinToString(separator = " "))
    pb.start().waitFor()
}
