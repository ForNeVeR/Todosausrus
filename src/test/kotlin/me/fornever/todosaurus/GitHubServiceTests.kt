package me.fornever.todosaurus

import com.intellij.mock.MockProject
import com.intellij.openapi.project.Project
import com.intellij.openapi.rd.createNestedDisposable
import com.jetbrains.rd.util.lifetime.Lifetime
import com.jetbrains.rd.util.lifetime.LifetimeDefinition
import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import me.fornever.todosaurus.models.CreateIssueModel
import me.fornever.todosaurus.models.RepositoryModel
import me.fornever.todosaurus.services.GitHubService
import me.fornever.todosaurus.testFramework.MockGitHubTokenStorage
import me.fornever.todosaurus.testFramework.StubRangeMarker
import me.fornever.todosaurus.testFramework.assertThrows
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import java.net.URI
import kotlin.io.path.Path

class GitHubServiceTests : TestCase() {

    private lateinit var ld: LifetimeDefinition
    private val lifetime: Lifetime
        get() = ld.lifetime

    private lateinit var project: Project
    private val tokenStorage = MockGitHubTokenStorage()
    private lateinit var service: GitHubService

    override fun setUp() {
        super.setUp()
        ld = LifetimeDefinition()
        project = MockProject(null, lifetime.createNestedDisposable("GitHubServiceTests"))
        service = GitHubService(project, tokenStorage)
    }
    override fun tearDown() {
        ld.terminate()
        super.tearDown()
    }

    fun testRepositoryNotSelected() {
        val model = stubModel()
        runBlocking {
            assertThrows<IllegalStateException>("Repository is not selected.") {
                service.createIssue(model)
            }
        }
    }

    fun testAccountNotSelected() {
        val model = stubModel().copy(selectedRepository = RepositoryModel(URI("https://example.com"), Path("/")))
        runBlocking {
            assertThrows<IllegalStateException>("Account is not selected.") {
                service.createIssue(model)
            }
        }
    }

    fun testTokenNotFound() {
        val account = GithubAccount()
        val model = stubModel().copy(
            selectedRepository = RepositoryModel(URI("https://example.com"), Path("/")),
            selectedAccount = account
        )
        runBlocking {
            assertThrows<IllegalStateException>("Token is not found.") {
                service.createIssue(model)
            }
        }
    }

    fun testCreateIssueNoFileForDocument() {
        fail()
    }

    fun testCreateIssueNoRelativePath() {
        fail()
    }

    fun testCreateIssueNoRootFile() {
        fail()
    }

    fun testCreateIssueNoRepository() {
        fail()
    }

    fun testCreateIssueNoRevision() {
        fail()
    }

    fun testCreateIssueOneLine() {
        fail()
    }

    fun testCreateIssueSeveralLines() {
        fail()
    }

    fun testCreateIssueTemplateReplacement() {
        fail()
    }

    private fun stubModel() = CreateIssueModel(
        selectedRepository = null,
        selectedAccount = null,
        title = "title",
        description = "description",
        textRangeMarker = StubRangeMarker()
    )
}
