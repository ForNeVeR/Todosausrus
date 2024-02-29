package me.fornever.todosaurus.testFramework

import org.junit.Assert

inline fun <reified Exception> assertThrows(message: String? = null, block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        if (e !is Exception) {
            throw e
        }

        if (message != null) {
            Assert.assertEquals(message, e.message)
        }

        return
    }

    Assert.fail("Expected an exception to be thrown.")
}
