/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.sitepermissions

import android.os.Parcel
import android.os.Parcelable
import mozilla.components.feature.sitepermissions.SitePermissions.Status.NO_DECISION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission
import mozilla.components.feature.sitepermissions.db.StatusConverter

/**
 * A site permissions and its state.
 */
data class SitePermissions(
    val origin: String,
    val permissions: List<Status> = Permission.values().map { NO_DECISION },
    val savedAt: Long
) : Parcelable {
    constructor(
            origin: String,
            permissionsMap: Map<Permission, Status>,
            savedAt: Long) : this(
            origin,
            Permission.values().map { permission ->
                permissionsMap.getOrElse(permission) { NO_DECISION }
            },
            savedAt)

    constructor(parcel: Parcel) :
        this(
            requireNotNull(parcel.readString()),
                requireNotNull(
                    parcel.createIntArray()?.map{
                        requireNotNull(converter.toStatus(it))
                    }),
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
        parcel.writeIntArray(permissions.map { converter.toInt(it) }.toIntArray())
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

    internal operator fun get(permission: Permission): Status {
        return permissions[permission.id]
    }
}
