package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Schedule;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ScheduleService {
    private final BackendRepository repo = BackendRepository.getInstance();

    public List<Schedule> getSchedules() {
        return repo.getSchedules().stream().collect(Collectors.toList());
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
}
