/**
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this file,
 * You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.mifosplatform.portfolio.client.domain;

/**
 * Enum representation of client status states.
 */
public enum ClientStatus {

    INVALID(0, "clientStatusType.invalid"), //
    NEW(100, "clientStatusType.pending"), //
    ACTIVE(300, "clientStatusType.active"), //
    DEACTIVE(600, "clientStatusType.closed"),
    CLOSED(400, "clientStatusType.closed");

    private final Integer value;
    private final String code;

    public static ClientStatus fromInt(final Integer statusValue) {

        ClientStatus enumeration = ClientStatus.INVALID;
        switch (statusValue) {
            case 100:
                enumeration = ClientStatus.NEW;
            break;
            case 300:
                enumeration = ClientStatus.ACTIVE;
            break;
            case 400:
                enumeration = ClientStatus.CLOSED;
            break;
            case 600:
                enumeration = ClientStatus.DEACTIVE;
            break;
        }
        return enumeration;
    }

    private ClientStatus(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public boolean hasStateOf(final ClientStatus state) {
        return this.value.equals(state.getValue());
    }

    public Integer getValue() {
        return this.value;
    }

    public String getCode() {
        return code;
    }

    public boolean isNew() {
        return this.value.equals(ClientStatus.NEW.getValue());
    }

    public boolean isActive() {
        return this.value.equals(ClientStatus.ACTIVE.getValue());
    }

    public boolean isDeactive() {
        return this.value.equals(ClientStatus.DEACTIVE.getValue());
    }
    public boolean isClosed() {
        return this.value.equals(ClientStatus.CLOSED.getValue());
    }
}