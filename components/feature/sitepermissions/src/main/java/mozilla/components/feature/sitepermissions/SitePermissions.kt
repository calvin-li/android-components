/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.sitepermissions

import android.os.Parcel
import android.os.Parcelable
import mozilla.components.feature.sitepermissions.SitePermissions.Status.*
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission
import mozilla.components.feature.sitepermissions.db.StatusConverter

/**
 * A site permissions and its state.
 */
data class SitePermissions(
    val origin: String,
    val permissions: Map<Permission, Status>,
    val savedAt: Long
) : Parcelable {

    constructor(
            origin: String,
            location: Status = NO_DECISION,
            notification: Status = NO_DECISION,
            microphone: Status = NO_DECISION,
            camera: Status = NO_DECISION,
            bluetooth: Status = NO_DECISION,
            localStorage: Status = NO_DECISION,
            savedAt: Long) : this(
            origin,
            mapOf(
                    Permission.LOCATION to location,
                    Permission.NOTIFICATION to notification,
                    Permission.MICROPHONE to microphone,
                    Permission.CAMERA to camera,
                    Permission.BLUETOOTH to bluetooth,
                    Permission.LOCAL_STORAGE to localStorage
            ),
            savedAt)

    constructor(parcel: Parcel) :
        this(
            requireNotNull(parcel.readString()),
            requireNotNull(converter.toStatus(parcel.readInt())),
            requireNotNull(converter.toStatus(parcel.readInt())),
            requireNotNull(converter.toStatus(parcel.readInt())),
            requireNotNull(converter.toStatus(parcel.readInt())),
            requireNotNull(converter.toStatus(parcel.readInt())),
            requireNotNull(converter.toStatus(parcel.readInt())),
            parcel.readLong()
        )

    enum class Status(
        internal val id: Int
    ) {
        BLOCKED(-1), NO_DECISION(0), ALLOWED(1);

        fun isAllowed() = this == ALLOWED

        fun doNotAskAgain() = this == ALLOWED || this == BLOCKED

        fun toggle(): Status = when (this) {
            BLOCKED, NO_DECISION -> ALLOWED
            ALLOWED -> BLOCKED
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(origin)
        permissions.values.forEach { parcel.writeInt(converter.toInt(it)) }
        parcel.writeLong(savedAt)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SitePermissions> {
        override fun createFromParcel(parcel: Parcel): SitePermissions {
            return SitePermissions(parcel)
        }

        override fun newArray(size: Int): Array<SitePermissions?> {
            return arrayOfNulls(size)
        }

        private val converter = StatusConverter()
    }

    private operator fun get(permission: Permission): Status? {
        return permissions[permission]
    }
}
