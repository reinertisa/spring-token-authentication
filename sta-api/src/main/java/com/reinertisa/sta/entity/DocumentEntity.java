package com.reinertisa.sta.entity;


import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "documents")
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class DocumentEntity extends Auditable {
    @Column(updatable = false, unique = true, nullable = false)
    private String documentId;
    private String name;
    private String description;
    private String uri;
    private long size;
    private String formattedSize;
    private String icon;
    private String extension;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk_documents_owner",
                    foreignKeyDefinition = "foreign key /* FK */ (user_id) references UserEntity",
                    value = ConstraintMode.CONSTRAINT)
    )
    private UserEntity owner;
}
