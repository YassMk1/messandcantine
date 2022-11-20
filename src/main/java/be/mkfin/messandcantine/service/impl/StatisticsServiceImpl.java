package be.mkfin.messandcantine.service.impl;

import be.mkfin.messandcantine.entity.*;
import be.mkfin.messandcantine.model.CommandStatistics;
import be.mkfin.messandcantine.repository.PayementRepository;
import be.mkfin.messandcantine.service.StatisticsService;
import be.mkfin.messandcantine.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        UserRegistered connectedCooker = userService.getConnectedCooker();
        Map<Article, List<Payement>> collect = new HashMap<>();
        for( Payement pay : allPayements){
           for( Commande cmd : pay.getBasket().getCommandes()){
               if( cmd.getAvailability() != null && cmd.getAvailability().getArticle() != null){
                   Article art = cmd.getAvailability().getArticle() ;
                   if (!art.getCooker().getUsername().equals(connectedCooker.getUsername())){
                       continue;
                   }
                   List<Payement> payements = collect.get(art);
                   if (payements == null){
                       payements = new ArrayList<>();
                       collect.put(art , payements);
                   }
                   payements.add(pay);
               }
           }
        }
        for(final Map.Entry<Article,List<Payement>> entry : collect.entrySet()){

            CommandStatistics stat = new CommandStatistics();
            result.add(stat);
            stat. successCommandesCount = entry.getValue().stream().filter(pay -> pay.getStatus() == PayementStatus.CONFIRMED ).count();
            stat. successCommandesQuantity = entry.getValue().stream().filter(pay -> pay.getStatus() == PayementStatus.CONFIRMED )
                    .flatMap(pay -> pay.getBasket().getCommandes().stream().filter(cmd -> cmd.getAvailability().getArticle().equals(entry.getKey())))
                    .mapToInt(cmd -> cmd.getQuantity()).summaryStatistics();
            stat. failedCommandesCount  = entry.getValue().stream().filter(pay -> pay.getStatus().isFailed() ).count();
            stat. failedCommandesQuantity =entry.getValue().stream().filter(pay -> pay.getStatus().isFailed() )
                    .flatMap(pay -> pay.getBasket().getCommandes().stream().filter(cmd -> cmd.getAvailability().getArticle().equals(entry.getKey())))
                    .mapToInt(cmd -> cmd.getQuantity()).summaryStatistics() ;
            stat. totalCommandesIncoming = entry.getValue().stream().filter(pay -> pay.getStatus() == PayementStatus.CONFIRMED ).mapToDouble(pay -> Double.valueOf(pay.getAmount()) ).sum();
            stat. commandesUsersCount =  entry.getValue().stream()
                    .flatMap(pay -> pay.getBasket().getCommandes().stream().filter(cmd -> cmd.getAvailability().getArticle().equals(entry.getKey())))
                    .collect(Collectors.toSet()).size();

            stat. article = entry.getKey();
        }


        return result;
    }

    @Override
    public List<CommandStatistics> getStatisticsOfMyCommands() {
       final UserRegistered connectedCooker = userService.getConnectedCooker();
        List<Payement> allPayements = payementRepository.findAll()
                .stream()
                .filter( pay -> pay.getBasket().getCommandes().stream().map(cmd -> cmd.getAvailability().getArticle())
                        .anyMatch(art -> art.getCooker().getUsername().equals(connectedCooker.getUsername())))
                .collect(Collectors.toList());
        return getCommandStatistics(allPayements);
    }
}
