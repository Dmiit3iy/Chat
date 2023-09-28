package com.dmiit3iy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
@RequiredArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = true)
    @NonNull
    private String login;




    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    @Cascade(value = org.hibernate.annotations.CascadeType.DELETE)
    @ToString.Exclude
    @JsonIgnore
    private List<Msg> msgs;


}
