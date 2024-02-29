package me.fornever.todosaurus.testFramework

import me.fornever.todosaurus.services.env.GitHubTokenStorage
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount

class MockGitHubTokenStorage : GitHubTokenStorage {
    private val tokens = mutableMapOf<GithubAccount, String>()
    fun putToken(account: GithubAccount, token: String) {
        tokens[account] = token
    }
    override fun getOrRequestToken(account: GithubAccount): String? = tokens[account]
}
