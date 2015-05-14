package com.github.hippoom.springtestdbunittemplate.sample;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="t_order")
public class Order {
    @Id
    @Column(name="tracking_id")
    private String trackingId;
    private String status;

    @ElementCollection
    @AttributeOverrides({
            @AttributeOverride(name = "name", column = @Column(name = "name")),
            @AttributeOverride(name = "quantity", column = @Column(name = "quantity")) })
    @CollectionTable(name = "t_order_item", joinColumns = @JoinColumn(name = "tracking_id"))
    @OrderBy("name")
    private List<Item> items = new ArrayList<Item>();

    public Order(String trackingId) {
        this.trackingId = trackingId;
        this.status = Status.WAIT_PAYMENT.code;
    }


    public enum Status {
        WAIT_PAYMENT("WAIT_PAYMENT"), UNKNOWN("UNKNOWN");

        private String code;

        Status(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Status of(String code) {
            for (Status s: values()) {
                if (s.code.equals(code)) {
                    return s;
                }
            }
            return UNKNOWN;
        }

    }
    public static class Item {

        private String name;
        private int quantity;
        public Item(String name, int quantity) {
            this.name = name;
            this.quantity = quantity;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        /**
         * for frameworks only
         */
        private Item() {

        }

    }
    public String getTrackingId() {
        return trackingId;
    }

    public Status getStatus() {
        return Status.of(status);
    }

    public void append(String name, int quantity) {
        this.items.add(new Item(name, quantity));
    }

    public List<Item> getItems() {
        return items;
    }

    /**
     * for frameworks only
     */
    private Order() {

    }
}
