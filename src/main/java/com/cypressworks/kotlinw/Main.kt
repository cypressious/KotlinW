package com.cypressworks.kotlinw

val cp = listOf("kotlin-stdlib", "kotlin-script-util")

fun main(args: Array<String>) {

    val cpCompiler = Maven.getClassPath(artifactName = "kotlin-compiler")
    val cp = cp.map { Maven.getClassPath(artifactName = it) }

    val pb = ProcessBuilder(listOf("java", "-jar", cpCompiler,
            "-classpath", cp.joinToString(","), "-nowarn", "-script")
            + args)

    pb.inheritIO()
    //    println(pb.command().joinToString(separator = " "))
    pb.start().waitFor()
}
