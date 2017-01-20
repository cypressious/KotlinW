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

object Maven {

    val defaultVersion = System.getenv("KOTLIN_VERSION") ?: "1.1.0-beta-17"

    val repoSystem = newRepositorySystem()!!
    val session = newSession(repoSystem)
    val central = RemoteRepository.Builder("central", "default", "http://repo1.maven.org/maven2/").build()!!
    val eap = RemoteRepository.Builder("central", "default", """https://dl.bintray.com/kotlin/kotlin-eap""").build()!!
    val eap11 = RemoteRepository.Builder("central", "default", """https://dl.bintray.com/kotlin/kotlin-eap-1.1""").build()!!

    fun getClassPath(
            group: String = "org.jetbrains.kotlin",
            artifactName: String,
            version: String = defaultVersion
    ): String {
        val artifact = DefaultArtifact("$group:$artifactName:$version")
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
            root = dependency
            addRepository(central)
            addRepository(eap)
            addRepository(eap11)
        }

        val node = repoSystem.collectDependencies(session, collectRequest).root
        val dependencyRequest = DependencyRequest().apply {
            root = node
        }

        repoSystem.resolveDependencies(session, dependencyRequest)
        val nlg = PreorderNodeListGenerator()
        node.accept(nlg)

        val classPath = nlg.classPath
        return classPath
    }

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
        val localRepo = LocalRepository(File(dataDir, "local-repo"))
        session.localRepositoryManager = system.newLocalRepositoryManager(session, localRepo)
        return session
    }

}