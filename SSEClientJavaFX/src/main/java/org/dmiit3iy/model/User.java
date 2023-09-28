package org.dmiit3iy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@RequiredArgsConstructor

public class User {

    private long id;

    @NonNull
    private String login;

    @ToString.Exclude
    @JsonIgnore
    private List<Msg> msgs;


}
