/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package mozilla.components.feature.sitepermissions

import android.os.Parcel
import android.os.Parcelable
import mozilla.components.feature.sitepermissions.SitePermissions.Status.NO_DECISION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.BLUETOOTH
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.CAMERA
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.MICROPHONE
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.NOTIFICATION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.LOCATION
import mozilla.components.feature.sitepermissions.SitePermissionsStorage.Permission.LOCAL_STORAGE
import mozilla.components.feature.sitepermissions.db.StatusConverter

/**
 * A site permissions and its state.
 */
data class SitePermissions(
    val origin: String,
    val permissions: Map<Permission, Status> = emptyMap(),
    val savedAt: Long
) : Parcelable {

    // TODO: Remove, and replace all usages
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
                LOCATION to location,
                NOTIFICATION to notification,
                MICROPHONE to microphone,
                CAMERA to camera,
                BLUETOOTH to bluetooth,
                LOCAL_STORAGE to localStorage),
            savedAt)

    constructor(parcel: Parcel) :
        this(
            requireNotNull(parcel.readString()),
            requireNotNull(parcel.createIntArray()).zip(requireNotNull(parcel.createIntArray()))
                .map {(permission, status) ->
                    requireNotNull(Permission.values().find { it.id == permission }) to
                    requireNotNull(converter.toStatus(status))
                }.toMap(),
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
        val (permissionList, statusList) = permissions.toList().unzip()
        parcel.writeIntArray(permissionList.map { it.id }.toIntArray())
        parcel.writeIntArray(statusList.map { converter.toInt(it) }.toIntArray())
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
        return permissions.getOrElse(permission) {NO_DECISION}
    }

    val bluetooth: Status
        get() = this[BLUETOOTH]
    val camera: Status
        get() = this[CAMERA]
    val microphone: Status
        get() = this[MICROPHONE]
    val location: Status
        get() = this[LOCATION]
    val localStorage: Status
        get() = this[LOCAL_STORAGE]
    val notification: Status
        get() = this[NOTIFICATION]
}
