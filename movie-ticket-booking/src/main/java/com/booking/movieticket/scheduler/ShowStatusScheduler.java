package com.booking.movieticket.scheduler;

import com.booking.movieticket.entity.Show;
import com.booking.movieticket.entity.enums.ShowStatus;
import com.booking.movieticket.repository.ShowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
@Transactional
public class ShowStatusScheduler {

    private final ShowRepository showRepository;

    @Scheduled(fixedRate = 60000) // every 1 minute
    public void updateShowStatuses() {

        LocalDateTime now = LocalDateTime.now();
        List<Show> shows = showRepository.findByActiveTrue();

        for(Show show :shows){

            if (show.getStatus() == ShowStatus.CANCELLED ||
                    show.getStatus() == ShowStatus.COMPLETED) {
                continue;
            }

            LocalDateTime start = LocalDateTime.of(show.getShowDate(),show.getShowTime());
            LocalDateTime end = start.plusMinutes(show.getMovie().getDurationMinutes());


            if (now.isAfter(start) && now.isBefore(end)) {
                show.setStatus(ShowStatus.RUNNING);
            }

            if (now.isAfter(end)) {
                show.setStatus(ShowStatus.COMPLETED);
                show.setActive(false);
            }
        }

        showRepository.saveAll(shows);
    }

}
