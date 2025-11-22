package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Train;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainService {
    private final BackendRepository repo = BackendRepository.getInstance();
    private final ScheduleService scheduleService = new ScheduleService();

    public ObservableList<Train> getTrains() {
        return repo.getTrains();
    }

    public List<Train> searchTrains(String from, String to) {
        String normalizedFrom = from.toLowerCase();
        String normalizedTo = to.toLowerCase();
        return repo.getTrains().stream()
                .filter(train -> train.getRoute() != null && train.getRoute().toLowerCase().contains(normalizedFrom))
                .filter(train -> train.getRoute() != null && train.getRoute().toLowerCase().contains(normalizedTo))
                .collect(Collectors.toList());
    }

    public Optional<Train> getTrainByNumber(String trainNumber) {
        return repo.findTrainByNumber(trainNumber);
    }

    public Train createTrain(String trainNumber, String trainName, String type, String route, String status) {
        // Decoupled: Creating a train no longer automatically creates a schedule.
        return repo.addTrain(new Train(repo.nextTrainId(), trainNumber, trainName, type, route, status));
    }

    public void deleteTrain(Train train) {
        // Also remove associated schedule if it exists
        repo.getSchedules().stream()
                .filter(s -> s.getTrainNumber().equals(train.getTrainNumber()))
                .findFirst()
                .ifPresent(scheduleService::removeSchedule);
        repo.removeTrain(train.getId());
    }

    public Optional<Train> updateTrain(Train train) {
        if (repo.updateTrain(train)) {
            return Optional.of(train);
        }
        return Optional.empty();
    }

    public String nextTrainId() {
        return repo.nextTrainId();
    }
}
