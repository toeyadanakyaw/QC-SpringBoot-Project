package com.announce.AcknowledgeHub_SpringBoot.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
@Entity
@Table(name = "noted")
public class Noted {

    @EmbeddedId
    private NotedId id;

    @Column(name = "noted_time")
    private Date noted_time;

    @Column(name = "is_noted")
    private boolean is_noted;

}
    @Embeddable
    class NotedId implements Serializable {
        @Column(name = "staff_id")
        private int staffId;

        @Column(name = "announcement_id")
        private int announcementId;


    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        NotedId notedId = (NotedId) obj;
        return staffId == notedId.staffId && announcementId == notedId.announcementId;
    }

    public int hashCode(){
        return Objects.hash(staffId, announcementId);
    }
}
