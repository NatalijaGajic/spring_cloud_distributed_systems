package com.distributed.systems.coursecompositeservice;

import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.event.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static com.distributed.systems.api.event.Event.Type.CREATE;
import static com.distributed.systems.api.event.Event.Type.DELETE;
import static com.distributed.systems.coursecompositeservice.IsSameEvent.sameEventExceptCreatedAt;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class IsSameEventTest {
    ObjectMapper mapper = new ObjectMapper();
    // Event #1 and #2 are the same event, but occurs as different times
    // Event #3 and #4 are different events

    @Test
    public void testEventObjectCompare() throws JsonProcessingException {

        // Event #1 and #2 are the same event, but occurs as different times
        // Event #3 and #4 are different events
        Event<Integer, Course> event1 = new Event<>(CREATE, 1, new Course(1, "name", "details", "eng"));
        Event<Integer, Course> event2 = new Event<>(CREATE, 1, new Course(1, "name", "details", "eng"));
        Event<Integer, Course> event3 = new Event<>(DELETE, 1, null);
        Event<Integer, Course> event4 = new Event<>(CREATE, 1, new Course(2, "name", "details", "eng"));

        String event1JSon = mapper.writeValueAsString(event1);

        assertThat(event1JSon, is(sameEventExceptCreatedAt(event2)));
        assertThat(event1JSon, not(sameEventExceptCreatedAt(event3)));
        assertThat(event1JSon, not(sameEventExceptCreatedAt(event4)));
    }
}
