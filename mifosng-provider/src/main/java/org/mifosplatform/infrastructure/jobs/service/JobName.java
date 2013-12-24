package org.mifosplatform.infrastructure.jobs.service;

public enum JobName {

    INVOICE("Invoice"),REQUESTOR("Requestor"),RESPONSOR("Responser"),SIMULATOR("Simulator"),PUSH_NOTIFICATION("PushNotification"),
    GENERATE_STATMENT("Generate Statment"),MESSAGE_MERGE("Messanger"),AUTO_EXIPIRY("Auto Exipiry"),ALL("All"), Middleware("Middleware");

    private final String name;

    JobName(final String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
