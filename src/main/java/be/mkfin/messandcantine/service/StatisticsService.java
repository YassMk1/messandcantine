package be.mkfin.messandcantine.service;

import be.mkfin.messandcantine.model.CommandStatistics;

import java.util.List;

public interface StatisticsService {
    List<CommandStatistics> getStatisticsOfAllCommands();

    List<CommandStatistics> getStatisticsOfMyCommands();

}
