package org.dmiit3iy.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.time.LocalDateTime;

@Data

@NoArgsConstructor
@RequiredArgsConstructor
public class Msg {

    private long id;

    @NonNull
    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH-mm-ss")
    private LocalDateTime localDateTime = LocalDateTime.now();

    @NonNull
    @ToString.Exclude
    private User user;
}
