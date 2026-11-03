package com.gymtracker.app.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ScreenTest {

    @Test
    fun `bottom nav items are initialized and not null`() {
        val items = bottomNavItems

        assertEquals(5, items.size)
        items.forEach { assertNotNull(it) }
        items.forEach { assertNotNull(it.route) }
    }
}
