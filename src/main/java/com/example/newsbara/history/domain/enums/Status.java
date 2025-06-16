package com.example.newsbara.history.domain.enums;

public enum Status {
    WATCHED(1),
    SHADOWING(2),
    TEST(3),
    WORD(4),
    COMPLETED(5);

    private final int order;

    Status(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}