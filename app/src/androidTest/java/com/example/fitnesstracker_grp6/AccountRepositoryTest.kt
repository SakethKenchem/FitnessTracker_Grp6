package com.example.fitnesstracker_grp6

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.fitnesstracker_grp6.data.local.FitnessDatabase
import com.example.fitnesstracker_grp6.data.repository.AccountRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountRepositoryTest {
    @Test
    fun registersAndSignsInWithUsernameOrEmail() = runBlocking {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val database = Room.inMemoryDatabaseBuilder(context, FitnessDatabase::class.java).build()
        try {
            val accounts = AccountRepository(database.userDao())
            assertTrue(accounts.register("Runner_6", "Runner@example.com", "sample-password"))
            assertFalse(accounts.register("RUNNER_6", "other@example.com", "other-password"))
            assertFalse(accounts.register("OtherRunner", "RUNNER@example.com", "other-password"))
            assertNull(accounts.signIn("runner_6", "other-password"))

            val account = accounts.signIn("RUNNER_6", "sample-password")
            assertNotNull(account)
            assertEquals("Runner_6", account?.username)
            assertEquals(account, accounts.signIn("RUNNER@EXAMPLE.COM", "sample-password"))
        } finally {
            database.close()
        }
    }
}
