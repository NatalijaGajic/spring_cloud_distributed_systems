package com.distributed.systems.coursecompositeservice.services;

import com.distributed.systems.api.core.author.Author;
import com.distributed.systems.api.core.author.AuthorService;
import com.distributed.systems.api.core.course.Course;
import com.distributed.systems.api.core.course.CourseService;
import com.distributed.systems.api.core.lecture.Lecture;
import com.distributed.systems.api.core.lecture.LectureService;
import com.distributed.systems.api.core.rating.Rating;
import com.distributed.systems.api.core.rating.RatingService;
import com.distributed.systems.api.core.user.User;
import com.distributed.systems.api.core.user.UserService;
import com.distributed.systems.api.event.Event;
import com.distributed.systems.util.exceptions.InvalidInputException;
import com.distributed.systems.util.exceptions.NotFoundException;
import com.distributed.systems.util.http.HttpErrorInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.time.Duration;

import static com.distributed.systems.api.event.Event.Type.CREATE;
import static com.distributed.systems.api.event.Event.Type.DELETE;
import static reactor.core.publisher.Flux.empty;

@Component
@EnableBinding(CourseCompositeIntegration.MessageSources.class)
public class CourseCompositeIntegration implements CourseService, LectureService, RatingService, AuthorService {

    private static final Logger LOG = LoggerFactory.getLogger(CourseCompositeIntegration.class);
    private final ObjectMapper objectMapper;

    //Eureka server finds instances; host:port is replaced with spring.application.name value from app.yml
    private final String courseServiceUrl = "http://course";
    private final String lectureServiceUrl = "http://lecture";
    private final String ratingServiceUrl = "http://rating";
    private final String authorServiceUrl = "http://author";
    private final WebClient.Builder webClientBuilder;
    private  WebClient webClient;
    private MessageSources messageSources;
    private final int courseServiceTimeoutSec;



    public interface MessageSources {

        String OUTPUT_COURSES= "output-courses";
        String OUTPUT_LECTURES= "output-lectures";
        String OUTPUT_RATINGS = "output-ratings";
        String OUTPUT_AUTHORS = "output-authors";

        @Output(OUTPUT_COURSES)
        MessageChannel outputCourses();

        @Output(OUTPUT_LECTURES)
        MessageChannel outputLectures();

        @Output(OUTPUT_RATINGS)
        MessageChannel outputRatings();

        @Output(OUTPUT_AUTHORS)
        MessageChannel outputAuthors();
    }

    @Autowired
    public CourseCompositeIntegration(
            ObjectMapper objectMapper,
            MessageSources messageSources,
            WebClient.Builder webClientBuilder,
            @Value("${app.course-service.timeoutSec}") int courseServiceTimeoutSec) {

        this.objectMapper = objectMapper;
        this.messageSources = messageSources;
        this.webClientBuilder = webClientBuilder;

        this.courseServiceTimeoutSec = courseServiceTimeoutSec;
    }

    @Override
    public Course createCourse(Course body) {
        messageSources.outputCourses().send(MessageBuilder.withPayload(new Event(CREATE, body.getCourseId(), body)).build());
        return body;
    }

    /**
     * The circuit breaker is triggered by an exception, not a timeout; using timeout method
     *
     * @param courseId
     * @param delay - number of seconds; causes the service to delay the response
     * @param faultPercent - probability; causes the service to throw an exc
     * @return
     */
    @Retry(name = "course")
    @CircuitBreaker(name = "course")
    @Override
    public Mono<Course> getCourse(int courseId, int delay, int faultPercent) {
        URI url = UriComponentsBuilder.fromUriString(courseServiceUrl + "/course/{courseId}?delay={delay}&faultPercent={faultPercent}").build(courseId, delay, faultPercent);
        LOG.debug("Will call the getCourse API on URL: {}", url);

        return getWebClient().get().uri(url)
                .retrieve().bodyToMono(Course.class).log()
                .onErrorMap(WebClientResponseException.class, ex -> handleException(ex))
                .timeout(Duration.ofSeconds(courseServiceTimeoutSec));
    }

    @Override
    public void deleteCourse(int courseId) {
        messageSources.outputCourses().send(MessageBuilder.withPayload(new Event(DELETE, courseId, null)).build());
    }

    @Override
    public Lecture createLecture(Lecture body) {

        messageSources.outputLectures().send(MessageBuilder.withPayload(new Event(CREATE, body.getCourseId(), body)).build());
        return body;
    }

    @Override
    public Flux<Lecture> getLectures(int courseId) {

        String url = lectureServiceUrl + "/lecture?courseId=" + courseId;

        LOG.debug("Will call the getLectures API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Lecture.class).log().onErrorResume(error -> empty());
    }

    @Override
    public void deleteLectures(int courseId) {
        messageSources.outputLectures().send(MessageBuilder.withPayload(new Event(DELETE, courseId, null)).build());
    }

    @Override
    public Rating createRating(Rating body) {

        messageSources.outputRatings().send(MessageBuilder.withPayload(new Event(CREATE, body.getCourseId(), body)).build());
        return body;
    }

    @Override
    public Flux<Rating> getRatings(int courseId) {
        String url = ratingServiceUrl + "/rating?courseId=" + courseId;

        LOG.debug("Will call the getRatings API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Rating.class).log().onErrorResume(error -> empty());


    }

    @Override
    public void deleteRatings(int courseId) {
        messageSources.outputRatings().send(MessageBuilder.withPayload(new Event(DELETE, courseId, null)).build());
    }

    @Override
    public Author createAuthor(Author body) {
        messageSources.outputAuthors().send(MessageBuilder.withPayload(new Event(CREATE, body.getCourseId(), body)).build());
        return body;
    }

    @Override
    public Flux<Author> getAuthors(int courseId) {
        String url = authorServiceUrl + "/author?courseId=" + courseId;

        LOG.debug("Will call the getAuthors API on URL: {}", url);

        // Return an empty result if something goes wrong to make it possible for the composite service to return partial responses
        return getWebClient().get().uri(url).retrieve().bodyToFlux(Author.class).log().onErrorResume(error -> empty());
    }

    @Override
    public void deleteAuthors(int courseId) {
        messageSources.outputAuthors().send(MessageBuilder.withPayload(new Event(DELETE, courseId, null)).build());
    }


    private WebClient getWebClient() {
        if (webClient == null) {
            webClient = webClientBuilder.build();
        }
        return webClient;
    }

    private String getErrorMessage(HttpClientErrorException ex){
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }

    private String getErrorMessage(WebClientResponseException ex) {
        try {
            return objectMapper.readValue(ex.getResponseBodyAsString(), HttpErrorInfo.class).getMessage();
        } catch (IOException ioex) {
            return ex.getMessage();
        }
    }


    private Throwable handleException(Throwable ex) {

        if (!(ex instanceof WebClientResponseException)) {
            LOG.warn("Got a unexpected error: {}, will rethrow it", ex.toString());
            return ex;
        }

        WebClientResponseException wcre = (WebClientResponseException)ex;

        switch (wcre.getStatusCode()) {

            case NOT_FOUND:
                return new NotFoundException(getErrorMessage(wcre));

            case UNPROCESSABLE_ENTITY :
                return new InvalidInputException(getErrorMessage(wcre));

            default:
                LOG.warn("Got a unexpected HTTP error: {}, will rethrow it", wcre.getStatusCode());
                LOG.warn("Error body: {}", wcre.getResponseBodyAsString());
                return ex;
        }
    }


}
