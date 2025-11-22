package com.example.railwaymanagementsystem.services;

import com.example.railwaymanagementsystem.models.Train;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TrainService {
    private final BackendRepository repo = BackendRepository.getInstance();

    public List<Train> getTrains() {
        return repo.getTrains().stream().collect(Collectors.toList());
    }

    public List<Train> searchTrains(String from, String to) {
        String normalizedFrom = from.toLowerCase();
        String normalizedTo = to.toLowerCase();
        return repo.getTrains().stream()
                .filter(train -> train.getRoute().toLowerCase().contains(normalizedFrom))
                .filter(train -> train.getRoute().toLowerCase().contains(normalizedTo))
                .collect(Collectors.toList());
    }

    public Optional<Train> getTrainByNumber(String trainNumber) {
        return repo.findTrainByNumber(trainNumber);
    }

    public Train createTrain(String trainNumber, String trainName, String type, String route, String status) {
        return repo.addTrain(new Train(repo.nextTrainId(), trainNumber, trainName, type, route, status));
    }

    public void deleteTrain(Train train) {
        repo.removeTrain(train.getId());
    }
}
