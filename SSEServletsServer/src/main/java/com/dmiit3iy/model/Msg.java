package com.dmiit3iy.model;

import lombok.*;

import javax.persistence.*;

@Entity

@Data
@Table(name = "msg")
@NoArgsConstructor
@RequiredArgsConstructor
public class Msg {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false, unique = false)
    @NonNull
    private String message;
    @NonNull
    @ManyToOne

    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

}
