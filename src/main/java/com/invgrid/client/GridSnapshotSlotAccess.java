package com.invgrid.client;

/**
 * Marker slot representing one entry of the aggregated grid snapshot.
 * Carries the snapshot total so the screen can render Ender IO panel-style
 * count labels, and exposes the slot index for the extract packet.
 */
public interface GridSnapshotSlotAccess {

    /** Total aggregated count of this item across all connected cells. */
    long getSnapshotTotal();

    /** Index of this entry in the client-side snapshot list. */
    int getSnapshotIndex();
}
