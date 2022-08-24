package be.mkfin.messandcantine.model;

import be.mkfin.messandcantine.entity.Article;

import java.util.IntSummaryStatistics;

public class CommandStatistics {
    public long successCommandesCount;
    public IntSummaryStatistics successCommandesQuantity;
    public long failedCommandesCount;
   public  IntSummaryStatistics failedCommandesQuantity;
    public double totalCommandesIncoming;
    public long commandesUsersCount;

    public Article article ;
}
