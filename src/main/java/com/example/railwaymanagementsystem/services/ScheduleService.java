package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Schedule;
import javafx.collections.ObservableList;

import java.util.Optional;

public class ScheduleService {
    private final BackendRepository repo = BackendRepository.getInstance();

    public ObservableList<Schedule> getSchedules() {
        return repo.getSchedules();
    }

    public Optional<Schedule> getScheduleForTrain(String trainNumber) {
        return repo.findScheduleByTrainNumber(trainNumber);
    }

    public Schedule createSchedule(String trainNumber, String trainName, String departureTime,
                                   String arrivalTime, String route, String days, String status) {
        return repo.addSchedule(new Schedule(repo.nextScheduleId(), trainNumber, trainName,
                departureTime, arrivalTime, route, days, status));
    }

    public void removeSchedule(Schedule schedule) {
        repo.removeSchedule(schedule);
    }

    public boolean updateSchedule(Schedule schedule) {
        return repo.updateSchedule(schedule);
    }
}
