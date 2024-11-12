package net.media.keywordlearning.servlets;

import net.media.database.DatabaseException;
import net.media.keywordlearning.Utils.ComparatorFactory;
import net.media.keywordlearning.Utils.Utils;
import net.media.keywordlearning.beans.Performance;
import net.media.keywordlearning.beans.PirateDebugResult;
import net.media.keywordlearning.dal.KeywordMasterDAL;

import java.util.*;

/**
 * Created by autoopt/rohit.aga.
 */
public class PirateDebugController {
    private List<PirateDebugResult> pirateDebugResultList;
    private List<PirateDebugResult> finalResult;
    private Map<String, Performance> urlPerformanceMap = new HashMap<>();
    private Double curScore;
    private static PirateDebugController pirateDebugController;

    //for debugging
//    public static PirateDebugController getInstance(String siteName, Integer keywordType, String basis, Integer urlCount, String canonHash, int actualData, int learnerId, int bucketId, int adTagId) throws DatabaseException {
//        if(pirateDebugController == null){
//            pirateDebugController = new PirateDebugController(siteName, keywordType, basis, urlCount, canonHash, actualData, learnerId, bucketId, adTagId);
//        }
//        return pirateDebugController;
//    }

    public static PirateDebugController getInstance(String siteName, Integer keywordType, String basis, Integer urlCount, String canonHash, int actualData, int learnerId, int bucketId, int adTagId) throws DatabaseException {
        return new PirateDebugController(siteName, keywordType, basis, urlCount, canonHash, actualData, learnerId, bucketId, adTagId);
    }

    public PirateDebugController(String siteName, Integer keywordType, String basis, Integer urlCount, String canonHash, int actualData, int learnerId, int bucketId, int adTagId) throws DatabaseException {
        pirateDebugResultList = KeywordMasterDAL.getPirateDataForBestUrls(siteName, keywordType, basis, urlCount, canonHash, actualData, learnerId, bucketId, adTagId);
        addPirateDebugScores(keywordType, basis);
    }

    private void addPirateDebugScores(Integer keywordType, String basis) {
        List<PirateDebugResult> finalResult = new ArrayList<>();
        if(this.pirateDebugResultList.size() == 0) return;

        String preUrl = this.pirateDebugResultList.get(0).getUrl();
        List<PirateDebugResult> curPirateDebugResult = new ArrayList<>();

        for(PirateDebugResult pirateDebugResult : this.pirateDebugResultList){

            Performance perf = new Performance(
                    pirateDebugResult.getImpression(),
                    pirateDebugResult.getConversion(),
                    pirateDebugResult.getRevenue(),
                    pirateDebugResult.getScaledImpression()
            );

            Performance newPerf = urlPerformanceMap.getOrDefault(pirateDebugResult.getUrl(), new Performance());
            newPerf.addPerformance(perf);
            urlPerformanceMap.put(pirateDebugResult.getUrl(), newPerf);

            if(pirateDebugResult.getUrl().equals(preUrl)){
                curPirateDebugResult.add(pirateDebugResult);
            }else{
                finalResult.addAll(getSortedAndFilteredUrl(curPirateDebugResult, keywordType, basis));
                preUrl = pirateDebugResult.getUrl();
                curPirateDebugResult = new ArrayList<>();
                curPirateDebugResult.add(pirateDebugResult);
            }
        }
        this.finalResult = finalResult;
    }

    private List<PirateDebugResult> getSortedAndFilteredUrl(List<PirateDebugResult> curPirateDebugResult, Integer keywordType, String basis) {
        curPirateDebugResult.sort(ComparatorFactory.cmpPirateDebugResult(PirateDebugResult::getImpression));
        int count = 0;
        int flag = 0;
        PirateDebugResult requiredPirateDebugResult = null;
        for(PirateDebugResult pirateDebugResult : curPirateDebugResult){
            count++;
            int isCurrentQuery = 0;
            if(Utils.isEmpty(basis)) {
                isCurrentQuery = Objects.equals(keywordType, pirateDebugResult.getKwdTypeId()) ? 1 : 0;
            } else {
                isCurrentQuery = Objects.equals(keywordType, pirateDebugResult.getKwdTypeId()) && Objects.equals(basis, pirateDebugResult.getBasis()) ? 1 : 0;
            }
            if (isCurrentQuery == 1) {
                curScore = pirateDebugResult.getPirateScore();
                requiredPirateDebugResult = new PirateDebugResult(pirateDebugResult);
                if(count <= 10){
                    flag = 1;
                }else{
                    break;
                }
            }
        }

        List<PirateDebugResult> shortenedList = new ArrayList<>();

        if(requiredPirateDebugResult == null){
            return shortenedList;
        }

        if(flag == 1){
            shortenedList = curPirateDebugResult.subList(0, Math.min(10, curPirateDebugResult.size()));
        }else{
            shortenedList = curPirateDebugResult.subList(0, Math.min(9, curPirateDebugResult.size()));
            shortenedList.add(requiredPirateDebugResult);
        }

        Performance performance = urlPerformanceMap.get(curPirateDebugResult.get(0).getUrl());
        System.out.println(performance.getRevenue() + " " + performance.getConversion());
        for(PirateDebugResult pirateDebugResult : shortenedList){
                pirateDebugResult.setScaledConversion(
                        (Utils.getRpc(pirateDebugResult.getRevenue(), pirateDebugResult.getConversion())
                                /
                                Utils.getRpc(performance.getRevenue(), performance.getConversion())) * pirateDebugResult.getConversion());

                System.out.println(pirateDebugResult.getScaledConversion() + " " + pirateDebugResult.getScaledImpression());
        }

        shortenedList.sort(ComparatorFactory.cmpPirateDebugResult(PirateDebugResult::scaledRPM));

        return shortenedList;

    }


    public List<PirateDebugResult> getFinalResult() {
        return finalResult;
    }

    public Performance getUrlPerformance(String url){
        return urlPerformanceMap.get(url);
    }

    public Double getCurScore() {
        return curScore;
    }
}
