package me.fornever.todosaurus.services

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.serviceAsync
import com.intellij.openapi.editor.RangeMarker
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import git4idea.repo.GitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import me.fornever.todosaurus.models.CreateIssueModel
import me.fornever.todosaurus.models.RepositoryModel
import org.jetbrains.plugins.github.api.GithubApiRequestExecutor
import org.jetbrains.plugins.github.api.GithubApiRequests
import org.jetbrains.plugins.github.api.GithubServerPath
import org.jetbrains.plugins.github.api.data.GithubIssue
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import org.jetbrains.plugins.github.util.GHCompatibilityUtil

@Service(Service.Level.PROJECT)
class GitHubService(private val project: Project) {

    companion object {
        const val GITHUB_CODE_URL_REPLACEMENT = "\${GITHUB_CODE_URL}"

        suspend fun getInstance(project: Project): GitHubService = project.serviceAsync()
    }

    suspend fun createIssue(model: CreateIssueModel): GithubIssue {
        val repository = model.selectedRepository ?: error("Repository is not selected.")
        val account = model.selectedAccount ?: error("Account is not selected.")

        val token = getApiToken(account)
        val executor = GithubApiRequestExecutor.Factory.getInstance().create(token)

        val issueBody = replacePatterns(model.description, repository, model.textRangeMarker)
        val request = GithubApiRequests.Repos.Issues.create(
            GithubServerPath.DEFAULT_SERVER,
            repository.owner,
            repository.name,
            model.title,
            issueBody
        )

        return withContext(Dispatchers.IO) {
            executor.execute(request)
        }
    }

    private fun getApiToken(account: GithubAccount): String {
        return GHCompatibilityUtil.getOrRequestToken(account, project) ?: error("Token is not found.")
    }

    private fun replacePatterns(description: String, repository: RepositoryModel, rangeMarker: RangeMarker): String {
        val rootPath = repository.rootPath
        val filePath = FileDocumentManager.getInstance().getFile(rangeMarker.document)?.toNioPath()
            ?: error("Cannot find file for the requested document.")
        val path = FileUtil.getRelativePath(rootPath.toFile(), filePath.toFile())?.replace('\\', '/')
            ?: error("Cannot calculate relative path between \"${repository.rootPath}\" and \"${filePath}\".")

        val currentCommit = getCurrentCommitHash(repository)
        val startLineNumber = rangeMarker.document.getLineNumber(rangeMarker.startOffset) + 1
        val endLineNumber = rangeMarker.document.getLineNumber(rangeMarker.endOffset) + 1
        val lineDesignator = if (startLineNumber == endLineNumber) "L$startLineNumber" else "L$startLineNumber-L$endLineNumber"
        val linkText =
            "https://github.com/${repository.owner}/${repository.name}/blob/$currentCommit/$path#$lineDesignator"

        return description.replace(GITHUB_CODE_URL_REPLACEMENT, linkText)
    }

    private fun getCurrentCommitHash(model: RepositoryModel): String {
        val manager = VcsRepositoryManager.getInstance(project)
        val virtualRoot = LocalFileSystem.getInstance().findFileByNioFile(model.rootPath)
            ?: error("Cannot find virtual file for \"${model.rootPath}\".")

        val repository = manager.repositories
            .asSequence()
            .filterIsInstance<GitRepository>()
            .filter { it.root == virtualRoot }
            .singleOrNull()
            ?: error("Cannot find a Git repository for \"${model.rootPath}\".")
        return repository.info.currentRevision
            ?: error("Cannot determine the current revision for \"${model.rootPath}\".")
    }
}
