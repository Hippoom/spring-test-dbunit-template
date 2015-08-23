package com.github.hippoom.springtestdbunittemplate.sample;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "t_event")
public class Event {
    @Id
    private String id;

    private String name;

    private String status;


    public enum Status {
        ACTIVE("A"), DONE("D"), UNKNOWN("U");

        private String code;

        Status(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public static Status of(String code) {
            for (Status s : values()) {
                if (s.code.equals(code)) {
                    return s;
                }
            }
            return UNKNOWN;
        }
    }


    public String getId() {
        return id;
    }

    public Status status() {
        return Status.of(status);
    }

    public String getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * for frameworks only
     */
    public Event() {

    }
}
