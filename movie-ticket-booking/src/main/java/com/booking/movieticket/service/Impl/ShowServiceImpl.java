package com.booking.movieticket.service.Impl;

import com.booking.movieticket.dto.request.ShowRequest;
import com.booking.movieticket.dto.response.ShowResponse;
import com.booking.movieticket.entity.Movie;
import com.booking.movieticket.entity.Screen;
import com.booking.movieticket.entity.Show;
import com.booking.movieticket.entity.enums.ShowStatus;
import com.booking.movieticket.exception.BusinessException;
import com.booking.movieticket.exception.DuplicateResourceException;
import com.booking.movieticket.exception.ResourceNotFoundException;
import com.booking.movieticket.repository.MovieRepository;
import com.booking.movieticket.repository.ScreenRepository;
import com.booking.movieticket.repository.ShowRepository;
import com.booking.movieticket.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@Transactional
public class ShowServiceImpl implements ShowService {

    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ScreenRepository screenRepository;

    @Override
    public ShowResponse createShow(ShowRequest request) {

        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (request.getShowDate().isBefore(today)) {
            throw new BusinessException("Cannot create show for past date");
        }

        if (request.getShowDate().isEqual(today) && request.getShowTime().isBefore(now)) {
            throw new IllegalArgumentException("Cannot create show for past time today");
        }


        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found"));
        if (request.getShowDate().isBefore(movie.getReleaseDate())) {
            throw new BusinessException("Cannot schedule show before movie release date: " + movie.getReleaseDate());
        }

        Screen screen = screenRepository.findById(request.getScreenId())
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found"));

        if (showRepository.existsByScreen_IdAndShowDateAndShowTimeAndActiveTrue(
                request.getScreenId(),
                request.getShowDate(),
                request.getShowTime())) {

            throw new DuplicateResourceException("Show already exists for this screen, date and time");
        }

        LocalDateTime newShowStart = LocalDateTime.of(request.getShowDate(), request.getShowTime());
        LocalDateTime newShowEnd = newShowStart.plusMinutes(movie.getDurationMinutes());

        List<Show> existingShows = showRepository.findActiveShowsByScreen(screen.getId());

        for (Show existing : existingShows) {

            LocalDateTime existingStart = LocalDateTime.of(existing.getShowDate(), existing.getShowTime());
            LocalDateTime existingEnd = existingStart.plusMinutes(existing.getMovie().getDurationMinutes());

            boolean overlap = newShowStart.isBefore(existingEnd) && newShowEnd.isAfter(existingStart);

            if (overlap) {
                throw new BusinessException("Screen already has a show scheduled or running during this time");
            }
        }
        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setShowDate(request.getShowDate());
        show.setShowTime(request.getShowTime());
        show.setBasePrice(request.getBasePrice());
        show.setStatus(ShowStatus.ACTIVE);
        show.setActive(true);

        return mapToShowResponse(showRepository.save(show));
    }

    @Override
    public ShowResponse getShowById(Long id) {

        return mapToShowResponse(getShow(id));
    }

    @Override
    public List<ShowResponse> getAllShows() {
        return showRepository.findByActiveTrue()
                .stream()
                .map(this::mapToShowResponse)
                .toList();
    }

    @Override
    public List<ShowResponse> getNowAndUpcomingShows() {
        return  showRepository.findUpcomingAndRunningShows(
                        List.of(ShowStatus.ACTIVE, ShowStatus.RUNNING),
                        LocalDate.now(),
                        LocalTime.now()
                ).stream()
                .map(this::mapToShowResponse)
                .toList();
    }

    @Override
    public List<ShowResponse> getNowShowingShows() {
        return showRepository.findNowShowingShows()
                .stream()
                .map(this::mapToShowResponse)
                .toList();
    }


    @Override public List<ShowResponse> getShowsByMovieNameAndDates(String movieName, LocalDate showDate) {
        List<Movie> movies = movieRepository.findByTitleContainingIgnoreCaseAndActiveTrue(movieName);
        if (movies.isEmpty()) {
            throw new ResourceNotFoundException("No movies found"); }
        return movies.stream() .flatMap(movie ->
                showRepository.findByMovie_IdAndShowDateAndActiveTrue(movie.getId(), showDate)
                        . stream())
                .map(this::mapToShowResponse)
                .toList();
    }

    @Override
    public List<ShowResponse> getShowsByMovieAndDate(Long movieId, LocalDate showDate) {
        return showRepository
                .findByMovie_IdAndShowDateAndActiveTrue(movieId, showDate)
                .stream()
                .map(this::mapToShowResponse)
                .toList();
    }

    @Override
    public ShowResponse cancelShow(Long id) {
        Show show = getShow(id);
        if (show.getStatus() == ShowStatus.COMPLETED) {
            throw new BusinessException("Completed show cannot be cancelled");
        }
        show.setStatus(ShowStatus.CANCELLED);
        show.setActive(false);
        return mapToShowResponse(showRepository.save(show));
    }

    @Override
    public ShowResponse completeShow(Long id) {
        Show show = getShow(id);
        if (show.getStatus() == ShowStatus.CANCELLED) {
            throw new BusinessException("Cancelled show cannot be completed");
        }
        show.setStatus(ShowStatus.COMPLETED);
        show.setActive(false);
        return mapToShowResponse(showRepository.save(show));
    }



    private Show getShow(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with id: " + id));
    }

    private ShowResponse mapToShowResponse(Show show) {

        ShowResponse response = new ShowResponse();

        response.setId(show.getId());

        response.setMovieId(show.getMovie().getId());
        response.setMovieTitle(show.getMovie().getTitle());


        response.setScreenName(show.getScreen().getName());
        response.setTheatreName(show.getScreen().getTheatre().getName());

        response.setShowDate(show.getShowDate());
        response.setShowTime(show.getShowTime());

        response.setBasePrice(show.getBasePrice());
        response.setStatus(show.getStatus());
        response.setActive(show.getActive());

        response.setCreatedAt(show.getCreatedAt());
        response.setUpdatedAt(show.getUpdatedAt());

        return response;
    }
}
