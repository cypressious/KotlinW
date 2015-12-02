package com.cypressworks.kotlinw

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator
import java.io.File

private fun newRepositorySystem() = MavenRepositorySystemUtils.newServiceLocator().apply {
    addService(RepositoryConnectorFactory::class.java,
            BasicRepositoryConnectorFactory::class.java)
    addService(TransporterFactory::class.java,
            FileTransporterFactory::class.java)
    addService(TransporterFactory::class.java,
            HttpTransporterFactory::class.java)
}.getService(RepositorySystem::class.java)

private fun newSession(system: RepositorySystem): RepositorySystemSession {
    val session = MavenRepositorySystemUtils.newSession()
    val localRepo = LocalRepository(File("target/local-repo"))
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo))
    return session
}

val central = RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/").build()

val repoSystem = newRepositorySystem()
val session = newSession(repoSystem)

fun main(args: Array<String>) {

    val cpCompiler = getClassPath("kotlin-compiler")
    val cpStdLib = getClassPath("kotlin-stdlib")

    val pb = ProcessBuilder(listOf("java", "-jar", cpCompiler,
            "-classpath", cpStdLib,
            "-nowarn", "-script") + args)

    pb.inheritIO()
    println(pb.command().joinToString(separator = " "))
    pb.start().waitFor()
}

private fun getClassPath(artifactName: String): String {
    val artifact = DefaultArtifact("org.jetbrains.kotlin:$artifactName:[0,)")
    val classPath = getClassPath(artifact, repoSystem, session)
    return classPath
}

private fun getClassPath(
        artifact: DefaultArtifact,
        repoSystem: RepositorySystem,
        session: RepositorySystemSession
): String {
    val dependency = Dependency(artifact, "compile")
    val collectRequest = CollectRequest().apply {
        setRoot(dependency)
        addRepository(central)
    }

    val node = repoSystem.collectDependencies(session, collectRequest).root
    val dependencyRequest = DependencyRequest().apply {
        setRoot(node)
    }

    repoSystem.resolveDependencies(session, dependencyRequest)
    val nlg = PreorderNodeListGenerator()
    node.accept(nlg)

    val classPath = nlg.classPath
    return classPath
}
