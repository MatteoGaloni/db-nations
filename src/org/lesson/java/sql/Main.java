package org.lesson.java.sql;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        String url = "jdbc:mysql://localhost:8889/nations";
        String user = "root";
        String password = "root";

        try(Connection connection = DriverManager.getConnection(url, user, password)){
            System.out.print("Enter a string: ");
            String choice = scan.nextLine();
            String formattedChoice = getString(choice);

            String query = "SELECT countries.name as countries_name, countries.country_id as countries_id, regions.name as regions_name, continents.name as continents_name "
                    + "FROM regions "
                    + "JOIN countries ON regions.region_id = countries.region_id "
                    + "JOIN continents ON regions.continent_id = continents.continent_id "
                    + "WHERE countries.name LIKE ? "
                    + "ORDER BY countries_name";
            try(PreparedStatement preparedStatement = connection.prepareStatement(query)){
                preparedStatement.setString(1, formattedChoice);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    System.out.printf("%-15s%-20s%-20s%-20s%n", "ID", "COUNTRY", "REGION", "CONTINENT");
                    while(resultSet.next()){
                        String countriesId = resultSet.getString("countries_id");
                        String countriesName = resultSet.getString("countries_name");
                        String regionsName = resultSet.getString("regions_name");
                        String continentsName = resultSet.getString("continents_name");

                        System.out.printf("%-15s%-30s%-20s%-20s%n", countriesId, countriesName, regionsName, continentsName);
                    }
                }
            } catch (SQLException e){
                System.out.println("unable to prepare statement");
                e.printStackTrace();
            }

            System.out.println("Enter valid id ");
            String selectedId = scan.nextLine();

            String languageQuery = "SELECT languages.language as language_name "
                    + "FROM country_languages "
                    + "JOIN languages ON country_languages.language_id = languages.language_id "
                    + "WHERE country_languages.country_id = ? ";
            try (PreparedStatement languageStatement = connection.prepareStatement(languageQuery)) {
                languageStatement.setString(1, selectedId);
                try (ResultSet languageResult = languageStatement.executeQuery()) {
                    System.out.print("Languages:");
                    while (languageResult.next()) {
                        String languageName = languageResult.getString("language_name");
                        System.out.print(" " + languageName + ",");
                    }
                }
            } catch (SQLException e) {
                System.out.println("Unable to prepare language statement");
                e.printStackTrace();
            }

            String statisticsQuery = "SELECT * FROM country_stats "
                    + "WHERE country_id = ? "
                    + "ORDER BY year DESC "
                    + "LIMIT 1";
            try (PreparedStatement statisticsStatement = connection.prepareStatement(statisticsQuery)) {
                statisticsStatement.setString(1, selectedId);
                try (ResultSet statisticsResult = statisticsStatement.executeQuery()) {
                    System.out.println("Most recent stats");
                    while (statisticsResult.next()) {
                         String year = statisticsResult.getString("year");
                         System.out.println("Year: " + year);
                         String population = statisticsResult.getString("population");
                         System.out.println("Population: " + population);
                         String gdp = statisticsResult.getString("gdp");
                         System.out.println("GDP: " + gdp);
                    }
                }
            } catch (SQLException e) {
                System.out.println("Unable to prepare statistics statement");
                e.printStackTrace();
            }



        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Unable to connect");
        }
        scan.close();
    }
    private static String getString(String choice) {
        return "%" + choice + "%";
    }
}
