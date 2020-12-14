package com.skillbox.mongodemo;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {


    public static void main(String[] args) {

        TestMongoDB first = new TestMongoDB();

        while (true) {

            Scanner scanner = new Scanner(System.in);
            String text = scanner.nextLine();
            if (!text.matches("ДОБАВИТЬ_МАГАЗИН\\s[[a-zA-Z\\s][А-я][а-я\\s]]*") && !text.matches("ДОБАВИТЬ_ТОВАР\\s[[a-zA-Z\\s][А-я][а-я\\s]]*\\s\\w*")
                    && !text.matches("ВЫСТАВИТЬ_ТОВАР\\s[[a-zA-Z\\s][А-я][а-я\\s]]*[[a-zA-Z\\s][А-я][а-я\\s]]*") && !text.matches("СТАТИСТИКА_ТОВАРОВ")) {
                try {
                    throw new Exception("не верный формат ввода");
                } catch (Exception e) {
                    System.out.println("не верный формат ввода");
                }
            }

            Pattern pattern2 = Pattern.compile("ДОБАВИТЬ_МАГАЗИН\\s[[a-zA-Z\\s][А-я][а-я\\s]]*");
            Matcher matcher2 = pattern2.matcher(text);

            Pattern pattern5 = Pattern.compile("ДОБАВИТЬ_ТОВАР\\s[[a-zA-Z\\s][А-я][а-я\\s]]*\\s\\w*");
            Matcher matcher5 = pattern5.matcher(text);


            Pattern pattern3 = Pattern.compile("ВЫСТАВИТЬ_ТОВАР\\s[[a-zA-Z\\s][А-я][а-я\\s]]*[[a-zA-Z\\s][А-я][а-я\\s]]*");
            Matcher matcher3 = pattern3.matcher(text);

            Pattern pattern4 = Pattern.compile("СТАТИСТИКА_ТОВАРОВ");
            Matcher matcher4 = pattern4.matcher(text);


            if (matcher2.find()) {
                first.addShop(text);
            } else if (matcher5.find()) {
                first.addGood(text);

            } else if (matcher3.find()) {
                first.putGoodInShop(text);

            } else if (matcher4.find()) {
                System.out.println("статистика товаров: ");
                first.minMaxSum();
                first.avgPrice();
                first.priceLess();

            }

        }

    }


}
