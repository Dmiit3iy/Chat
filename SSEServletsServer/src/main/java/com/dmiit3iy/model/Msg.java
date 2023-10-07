package com.dmiit3iy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity

@Data
@Table(name = "msg")
@NoArgsConstructor
@RequiredArgsConstructor
public class Msg {
    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    @NonNull
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH-mm-ss")
    @Column(nullable = false)
    private LocalDateTime localDateTime = LocalDateTime.now();
    @NonNull
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
   // @JsonIgnore
    private User user;

}
