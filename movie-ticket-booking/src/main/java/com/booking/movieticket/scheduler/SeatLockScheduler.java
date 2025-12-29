package com.booking.movieticket.scheduler;

import com.booking.movieticket.service.ShowSeatService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class SeatLockScheduler {

    private final ShowSeatService showSeatService;

    @Scheduled(cron = "0 */1 * * * ?")
    public  void releaseExpiredLocks(){
        showSeatService.releaseExpiredLocks();
    }
}
