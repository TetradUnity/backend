package com.tetradunity.server.models.calendars;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CalendarFilter {
    private boolean withGrade = false;
    private long[] subjects_id;
}
