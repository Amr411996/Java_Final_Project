/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.demo.model;

/**
 *
 * @author lenovo
 */

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.knowm.xchart.*;
import org.knowm.xchart.style.Styler;
import smile.clustering.KMeans;
import smile.clustering.PartitionClustering;
import smile.data.DataFrame;
import smile.data.measure.NominalScale;
import smile.data.vector.StringVector;
import smile.io.Read;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class Wazzaf_DAO {

    List<Wazzaf_Data> titlesAndcompany;
    Map<String, List<Wazzaf_Data>> CompanyandTitles = new HashMap<>();

    Map<String, Long> CompanyandTitlesNumber = new HashMap<>();

    Map<String, Long> TitlesNumber = new HashMap<>();
    Map<String, Long> LocationNumber = new HashMap<>();

    public Wazzaf_DAO() {
        titlesAndcompany = new ArrayList<>();
    }

    public DataFrame readCSV(String path) {
        List<Wazzaf_Data> titlesAndcompany = new ArrayList<Wazzaf_Data>();
        DataFrame df = null;
        try {

            CSVFormat format = CSVFormat.DEFAULT.withFirstRecordAsHeader();

            df = Read.csv(path, format);
            //originalDF = df;

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        return df;
    }
    public DataFrame dfClean(DataFrame df){


        df = DataFrame.of(df.stream().distinct());
        //System.out.println(df.size());
        String[] values = df.stringVector("YearsExp").toArray();
        String[] minYearsExp = new String[values.length];
        //System.out.println(values.length);
        Pattern pattern = Pattern.compile("\\d+");

        for (int i=0;i<values.length;i++){
            values[i].replace("null","0");
            Matcher matcher = pattern.matcher(values[i]);
            if (matcher.find()) {
                String s = matcher.group(0);
                minYearsExp[i] = s;

            }
        }
        df = df.drop("YearsExp");
        //System.out.println(df.summary());
        //System.out.println(df.schema());

        df= df.merge(StringVector.of("YearsExp", values));
        df= df.merge(StringVector.of("MinYearsExp", minYearsExp));
        System.out.println(df.summary());
        System.out.println(df.schema());

        return df;
    }
    public static double[] encodeCategory(DataFrame df, String columnName1) {
        String[] values = df.stringVector(columnName1).distinct ().toArray(new String[]{});
//String[] values1 = df.stringVector(columnName2).distinct ().toArray(new String[]{});
        double[] pclassValues1= df.stringVector(columnName1).factorize (new NominalScale(values)).toDoubleArray();
//int[] pclassValues2= df.stringVector(columnName2).factorize (new NominalScale(values1)).toIntArray();

        return pclassValues1;
    }
    public Object[] myKmean(DataFrame df){
        double [] Title1 = encodeCategory(df, "Title");
        double [] Company1 = encodeCategory(df, "Company");
        double [][] myDataFinal = new double[Title1.length][2];
        for(int i=0; i<Title1.length;i++){
            for(int j=0;j<2;j++){
                if (j==0)
                {
                    myDataFinal[i][j] = Title1[i];
                }
                else {
                    myDataFinal[i][j] = Company1[i];
                }}
        }
        //System.out.println(myDataFinal);
        KMeans clusters = PartitionClustering.run(20, () -> KMeans.fit(myDataFinal, 5));

        System.out.println(clusters);
        return new KMeans[]{clusters};
    }
    public List<Wazzaf_Data> readDatatoListFromCSV(String fileName) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            String record;
            String[] recordLst;

            do {
                record = br.readLine();
                if (record != null) {
                    recordLst = record.split(",");
                    Wazzaf_Data p = new Wazzaf_Data(recordLst[0], recordLst[1], recordLst[2].trim(), recordLst[3], recordLst[4], recordLst[5], recordLst[6], recordLst[7].trim());
                    titlesAndcompany.add(p);
                }
            } while (record != null);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return titlesAndcompany;
    }

    // Method to return Map of each company and jobs belong to it
    public Map<String, List<Wazzaf_Data>> MapOfCompanieswithJobs(DataFrame d, List<Wazzaf_Data> titlesAndcompany) {
        String[] Companies = d.stringVector("Company").distinct().toArray(new String[]{});
        for (String company : Companies) {

            List<Wazzaf_Data> data = titlesAndcompany.stream().filter(c -> c.getCompany().equals(company)).collect(Collectors.toList());

            CompanyandTitles.put(company, data);

        }
        return CompanyandTitles;
    }

    // Map to return the Company and number of jobs inside it 
    public Map<String, Long> MapOfCompanieswithNumberOfJobs(DataFrame d, List<Wazzaf_Data> titlesAndcompany) {
        String[] Companies = d.stringVector("Company").distinct().toArray(new String[]{});
        System.out.println(Companies.length);
        for (String company : Companies) {

            Long data1 = titlesAndcompany.stream().filter(c -> c.getCompany().equals(company)).count();

            CompanyandTitlesNumber.put(company, data1);
        }
        Map<String, Long> CompanyandTitlesNumberArranged = new HashMap<>();
        CompanyandTitlesNumberArranged = CompanyandTitlesNumber
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        System.out.println("Most Demanding Companies: " + CompanyandTitlesNumberArranged);



        return CompanyandTitlesNumberArranged;
    }
    public Map<String, Long> skillsMap(DataFrame df){
        String[] skill = df.stringVector("Skills").distinct().toArray(new String[]{});
        Map<String, Long> skillsCount = new LinkedHashMap<>();
        for(String s:skill){
            String[] s2 = s.split(",");

            for(String sk:s2) {
                sk = sk.trim();
                sk = sk.toLowerCase();

                Long temp = skillsCount.get(sk);

                if (temp == null)
                    skillsCount.put(sk, 1L);
                else
                    skillsCount.put(sk, temp + 1);
            }


        }
        skillsCount = skillsCount
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        System.out.println(skillsCount);
        return skillsCount;
    }

    public Map<String, Long> MapOfMostTitles(DataFrame d, List<Wazzaf_Data> titlesAndcompany) {
        String[] Titles = d.stringVector("Title").distinct().toArray(new String[]{});
        for (String title : Titles) {
            Long data2 = titlesAndcompany.stream().filter(c -> c.getTitle().equals(title)).count();

            TitlesNumber.put(title, data2);
        }
        Map<String, Long> TitlesNumberArranged = new HashMap<>();
        TitlesNumberArranged = TitlesNumber
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        System.out.println("Most Demanding Job: " + TitlesNumberArranged);
        return TitlesNumberArranged;
    }

    public Map<String, Long> MapOfMostAreas(DataFrame d, List<Wazzaf_Data> titlesAndcompany) {
        String[] locations = d.stringVector("Location").distinct().toArray(new String[]{});
        for (String location : locations) {

            Long data3 = titlesAndcompany.stream().filter(c -> c.getLocation().equals(location)).count();

            LocationNumber.put(location, data3);
        }
        Map<String, Long> LocationNumberArranged = new HashMap<>();
        LocationNumberArranged = LocationNumber
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2, LinkedHashMap::new));
        System.out.println("Popular Areas : " + LocationNumberArranged);
        return LocationNumberArranged;
    }

    public void FirstGraph(Map<String, Long> Company) throws IOException {

        PieChart chart = new PieChartBuilder().width(800).height(600).title(getClass().getSimpleName()).build();
        Color[] sliceColors = new Color[]{new Color(180, 68, 50), new Color(130, 105, 120), new Color(80, 110, 45), new Color(80, 143, 160)};
        chart.getStyler().setSeriesColors(sliceColors);
        chart.getStyler().setSeriesColors(sliceColors);

        List<String> keyList = new ArrayList(Company.keySet());
        List<Long> valueList = new ArrayList(Company.values());
        for (int i = 0; i < 4; i++) {

            chart.addSeries(keyList.get(i), valueList.get(i));

        }

        new SwingWrapper(chart).displayChart();
        //BitmapEncoder.saveBitmap(chart, "C:\\Users\\Amr Alaa\\Downloads\\demo\\demo\\src\\main\\resources\\templates\\Companies", BitmapEncoder.BitmapFormat.PNG);
    }

    public void graphJobs(Map<String, Long> TitlesNumber) {
        CategoryChart chart = new CategoryChartBuilder().width(1024).height(768).title("JobsDemand").xAxisTitle("Jobs").yAxisTitle("NumberOfJobs").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setStacked(true);
        //chart.addSeries("Passenger's Ages", pNames, pAges);

        List<String> keyList = new ArrayList(TitlesNumber.keySet());
        List<Long> valueList = new ArrayList(TitlesNumber.values());

        List<String> arrlist1 = keyList.subList(0, 10);
        List<Long> arrlist2 = valueList.subList(0, 10);

        chart.addSeries("Most Job", arrlist1, arrlist2);

        new SwingWrapper(chart).displayChart();
    }

    public void graphAreas(Map<String, Long> Area) {
        CategoryChart chart = new CategoryChartBuilder().width(1024).height(768).title("Most Popular Area").xAxisTitle("Area").yAxisTitle("Count of Areas").build();
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
        chart.getStyler().setHasAnnotations(true);
        chart.getStyler().setStacked(true);
        //chart.addSeries("Passenger's Ages", pNames, pAges);

        List<String> keyList = new ArrayList(Area.keySet());
        List<Long> valueList = new ArrayList(Area.values());

        List<String> arrlist1 = keyList.subList(0, 10);
        List<Long> arrlist2 = valueList.subList(0, 10);

        chart.addSeries("Most Areas", arrlist1, arrlist2);

        new SwingWrapper(chart).displayChart();
    }

    static public List<Map.Entry> Skills(String filepath) {

        Logger.getLogger("org").setLevel(Level.ERROR);
        // CREATE SPARK CONTEXT
        SparkConf conf = new SparkConf().setAppName("wordCounts").setMaster("local[3]");
        JavaSparkContext sparkContext = new JavaSparkContext(conf);
        // LOAD DATASETS
        org.apache.spark.api.java.JavaRDD<String> videos = sparkContext.textFile(filepath);
        org.apache.spark.api.java.JavaRDD<String> tags = videos
                .map(Wazzaf_DAO::extractTag)
                .filter(StringUtils::isNotBlank);
        // JavaRDD<String>
        org.apache.spark.api.java.JavaRDD<String> words = tags.flatMap(tag -> Arrays.asList(tag
              .toLowerCase().trim().split(",")).iterator());
        System.out.println(words.toString());
        // COUNTING
        Map<String, Long> wordCounts = words.countByValue();
        List<Map.Entry> sorted = wordCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue()).collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : sorted) {
            System.out.println(entry.getKey() + " : " + entry.getValue());

        }
        return sorted;
    }

    public static String extractTag(String videoLine) {
        try {
            String[] splitline = videoLine.split(",");
            splitline = Arrays.copyOfRange(splitline, 7, splitline.length);
            videoLine = String.join(",", splitline).trim();
            String result = videoLine.replaceAll("^\"+|\"+$", "");
            String resultfinal = result.replaceAll(" ","");
            
            //System.out.println(resultfinal);
            return resultfinal;
            
            
        } catch (ArrayIndexOutOfBoundsException e) {
            return "";
        }

    }

 
    
}
