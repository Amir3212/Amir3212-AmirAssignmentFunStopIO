package com.example.amirassignment.data.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.amirassignment.domain.model.InteractionType
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class InteractionLogDaoTest {

    private lateinit var database: MarketplaceDatabase
    private lateinit var dao: InteractionLogDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MarketplaceDatabase::class.java,
        ).allowMainThreadQueries().build()
        dao = database.interactionLogDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun insertAndMarkSynced() = runTest {
        dao.insert(
            InteractionLogEntity(
                type = InteractionType.JOIN_POOL.name,
                productId = 1,
                timestamp = 100L,
                synced = false,
            ),
        )
        assertEquals(1, dao.countUnsynced())
        val pending = dao.getUnsynced()
        dao.markSynced(pending.map { it.id })
        assertEquals(0, dao.countUnsynced())
        assertTrue(dao.getUnsynced().isEmpty())
    }
}
