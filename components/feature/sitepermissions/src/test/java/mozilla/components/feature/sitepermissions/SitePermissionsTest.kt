/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.sitepermissions

import androidx.test.ext.junit.runners.AndroidJUnit4
import mozilla.components.feature.sitepermissions.SitePermissions.Status.ALLOWED
import mozilla.components.feature.sitepermissions.SitePermissions.Status.NO_DECISION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.NOTIFICATION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.LOCATION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.MICROPHONE
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.CAMERA
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.BLUETOOTH
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SitePermissionsTest {

    @Test
    fun `get operator will provide the same values as the properties`() {
        var sitePermissions = SitePermissions(origin = "mozilla.dev", savedAt = 0)

        assertEquals(NO_DECISION, sitePermissions[NOTIFICATION])
        assertEquals(NO_DECISION, sitePermissions[LOCATION])
        assertEquals(NO_DECISION, sitePermissions[MICROPHONE])
        assertEquals(NO_DECISION, sitePermissions[BLUETOOTH])
        assertEquals(NO_DECISION, sitePermissions[CAMERA])

        sitePermissions = sitePermissions.copy(
            location = ALLOWED,
            notification = ALLOWED,
            microphone = ALLOWED,
            bluetooth = ALLOWED,
            camera = ALLOWED
        )

        assertEquals(ALLOWED, sitePermissions[NOTIFICATION])
        assertEquals(ALLOWED, sitePermissions[LOCATION])
        assertEquals(ALLOWED, sitePermissions[MICROPHONE])
        assertEquals(ALLOWED, sitePermissions[BLUETOOTH])
        assertEquals(ALLOWED, sitePermissions[CAMERA])
    }
}
