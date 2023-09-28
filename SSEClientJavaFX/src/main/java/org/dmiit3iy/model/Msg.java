package org.dmiit3iy.model;

import lombok.*;

@Data

@NoArgsConstructor
@RequiredArgsConstructor
public class Msg {

    private long id;

    @NonNull
    private String message;
    @NonNull
    @ToString.Exclude
    private User user;
}
