package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.Article;
import be.mkfin.messandcantine.entity.Payement;
import be.mkfin.messandcantine.entity.PayementStatus;
import be.mkfin.messandcantine.model.CommandStatistics;
import be.mkfin.messandcantine.repository.PayementRepository;
import be.mkfin.messandcantine.service.StatisticsService;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
public class StatisticsServiceImpl implements StatisticsService {
    @Autowired
   private  PayementRepository payementRepository ;

    @Autowired
  private   UserService userService ;

    @Override
    public List<CommandStatistics> getStatisticsOfAllCommands() {
        List<Payement> allPayements = payementRepository.findAll();
        return getCommandStatistics(allPayements);
    }

    private List<CommandStatistics> getCommandStatistics(List<Payement> allPayements) {
        List<CommandStatistics> result = new ArrayList<>();


        Map<Article, List<Payement>> collect = allPayements.stream()
                .collect(groupingBy(pay -> pay.getCommande().getAvailability().getArticle(), toList()));

        for(Map.Entry<Article,List<Payement>> entry : collect.entrySet()){

            CommandStatistics stat = new CommandStatistics();
            result.add(stat);
            stat. successCommandesCount = entry.getValue().stream().filter(pay -> pay.getStatus() == PayementStatus.CONFIRMED ).count();
            stat. successCommandesQuantity = entry.getValue().stream().filter(pay -> pay.getStatus() == PayementStatus.CONFIRMED ).mapToInt(pay -> pay.getCommande().getQuantity()).summaryStatistics();
            stat. failedCommandesCount  = entry.getValue().stream().filter(pay -> pay.getStatus().isFailed() ).count();
            stat. failedCommandesQuantity =entry.getValue().stream().filter(pay -> pay.getStatus().isFailed() ).mapToInt(pay -> pay.getCommande().getQuantity()).summaryStatistics() ;
            stat. totalCommandesIncoming = entry.getValue().stream().filter(pay -> pay.getStatus() == PayementStatus.CONFIRMED ).mapToDouble(pay -> Double.valueOf(pay.getAmount()) ).sum();
            stat. commandesUsersCount =  entry.getValue().stream().map(pay -> pay.getCommande().getCommander()).collect(Collectors.toSet()).size();

            stat. article = entry.getKey();
        }


        return result;
    }

    @Override
    public List<CommandStatistics> getStatisticsOfMyCommands() {
        List<Payement> allPayements = payementRepository.findAllByCommandeAvailabilityArticleCooker(userService.getConnectedCooker());
        return getCommandStatistics(allPayements);
    }
}
