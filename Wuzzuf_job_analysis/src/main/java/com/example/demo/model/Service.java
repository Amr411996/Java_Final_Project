package com.example.demo.model;

import org.apache.commons.csv.CSVFormat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import smile.data.DataFrame;
import smile.data.vector.StringVector;
import smile.io.Read;


public class Service {

    public static DataFrame originalDF;
    public static DataFrame cleanedDF;
    public static List<Wazzaf_Data> charData;
    public static Wazzaf_DAO Wazzaf = new Wazzaf_DAO();
    public static String path = "C:\\Users\\Amr Alaa\\Downloads\\demo\\demo\\src\\main\\resources\\Wuzzuf_Jobs.csv";

    public static Object[] showOriginalDF(){
        originalDF = Wazzaf.readCSV(path);
        return originalDF.stream().limit(10).toArray();
    }

    public static List<String> showDFInfo(){
        List<String> dfInfo = new ArrayList<String>();

        dfInfo.add(originalDF.summary().toString());
        dfInfo.add(originalDF.schema().toString());
        dfInfo.add(String.valueOf(originalDF.size()));
        cleanedDF = Wazzaf.dfClean(originalDF);

        return dfInfo;
    }

    public static Object[] Task3(){
        return cleanedDF.stream().limit(10).toArray();
    }



    public static Object[] Task4(){

        List<Wazzaf_Data> titlesAndcompany = Wazzaf.readDatatoListFromCSV(path);
        charData = titlesAndcompany;
        Map<String ,Long> CompanyandTitles = Wazzaf.MapOfCompanieswithNumberOfJobs(cleanedDF, charData);
        return new Map[]{CompanyandTitles};
    }

    public static Object[] Task6(){
        Map<String, Long> mostTitles = Wazzaf.MapOfMostTitles(cleanedDF,charData);
        return new Map[]{mostTitles};
    }

    public static Object[] Task8(){
        Map<String, Long> mapAreas = Wazzaf.MapOfMostAreas(cleanedDF,charData);
        return new Map[]{mapAreas};
    }
    public static Object[] Task10(){
        Map<String, Long> mapSkills = Wazzaf.skillsMap(cleanedDF);
        return new Map[]{mapSkills};
    }
    public static Object[] Task12(){
        Object[] kmean = Wazzaf.myKmean(cleanedDF);
        return kmean;
    }



}
