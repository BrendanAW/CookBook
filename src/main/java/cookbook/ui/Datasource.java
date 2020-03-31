package cookbook.ui;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Datasource {
    private static final String DB_NAME = "recipes.db";
    private static final String CONNECTION_STRING = "jdbc:sqlite:" + DB_NAME;
    private static final int INDEX_RECIPE_NAME = 1;
    private static final int INDEX_RECIPE_INGREDIENTS = 2;
    private static final int INDEX_RECIPE_INSTRUCTIONS = 3;

    private static final String ENGLISH_TABLE = "recipes";
    private static final String POLISH_TABLE = "przepisy";
    private static final String NAME = "name";
    private static final String INGREDIENTS = "ingredients";
    private static final String INSTRUCTIONS = "instructions";

    private static final String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS \"" + ENGLISH_TABLE +
            "\" (\"" + NAME + "\" TEXT, \"" + INGREDIENTS +
            "\" TEXT, \"" + INSTRUCTIONS + "\")";
    private static final String CREATE_POLISH_TABLE = "CREATE TABLE IF NOT EXISTS \"" + POLISH_TABLE +
            "\" (\"" + NAME + "\" TEXT, \"" + INGREDIENTS +
            "\" TEXT, \"" + INSTRUCTIONS + "\")";

    private static final String QUERY_INGREDIENTS_BY_NAME = "SELECT " + INGREDIENTS + " FROM " + ENGLISH_TABLE + " WHERE " +
            NAME + " = ? COLLATE NOCASE";
    private static final String QUERY_INGREDIENTS_BY_NAME_POLISH = "SELECT " + INGREDIENTS + " FROM " + POLISH_TABLE + " WHERE " +
            NAME + " = ? COLLATE NOCASE";

    private static final String QUERY_INSTRUCTIONS_BY_NAME = "SELECT " + INSTRUCTIONS + " FROM " + ENGLISH_TABLE + " WHERE " +
            NAME + " = ? COLLATE NOCASE";
    private static final String QUERY_INSTRUCTIONS_BY_NAME_POLISH = "SELECT " + INSTRUCTIONS + " FROM " + POLISH_TABLE + " WHERE " +
            NAME + " = ? COLLATE NOCASE";

    private static final String INSERT_INTO_TABLE = "INSERT INTO " + ENGLISH_TABLE + " ( " + NAME + ", " +
            INGREDIENTS + ", " + INSTRUCTIONS + ") VALUES(?, ?, ? )";
    private static final String INSERT_INTO_TABLE_POLISH = "INSERT INTO " + POLISH_TABLE + " ( " + NAME + ", " +
            INGREDIENTS + ", " + INSTRUCTIONS + ") VALUES(?, ?, ? )";

    private static final String UPDATE_TABLE = "UPDATE " + ENGLISH_TABLE + " SET " + NAME + " = ?, " +
            INGREDIENTS + " = ?, " + INSTRUCTIONS + " = ? WHERE " + NAME + " = ?";
    private static final String UPDATE_TABLE_POLISH = "UPDATE " + POLISH_TABLE + " SET " + NAME + " = ?, " +
            INGREDIENTS + " = ?, " + INSTRUCTIONS + " = ? WHERE " + NAME + " = ?";

    private static final String DELETE_FROM_TABLE = "DELETE FROM " + ENGLISH_TABLE + " WHERE " +
            NAME + " = ?";
    private static final String DELETE_FROM_TABLE_POLISH = "DELETE FROM " + POLISH_TABLE + " WHERE " +
            NAME + " = ?";

    private PreparedStatement queryIngredientsByName;
    private PreparedStatement queryIngredientsByNamePolish;

    private PreparedStatement queryInstructionsByName;
    private PreparedStatement queryInstructionsByNamePolish;

    private PreparedStatement insertIntoTable;
    private PreparedStatement insertIntoTablePolish;

    private PreparedStatement updateTable;
    private PreparedStatement updateTablePolish;

    private PreparedStatement deleteFromTable;
    private PreparedStatement deleteFromTablePolish;


    private Connection conn;
    private static final String ENGLISH = "english";
    private static final String POLISH = "polish";
    private String tableName = ENGLISH_TABLE;
    private String tableLanguage = ENGLISH;

    private static Datasource instance = new Datasource();

    private Datasource() {
    }

    void switchTableName() {
        if (tableLanguage.equals(ENGLISH)) {
            tableName = POLISH_TABLE;
            tableLanguage = POLISH;
        } else {
            tableName = ENGLISH_TABLE;
            tableLanguage = ENGLISH;
        }

    }

    String getTableName() {
        return tableName;
    }

    public static Datasource returnInstance() {
        return instance;
    }

    boolean open() {
        try {
            conn = DriverManager.getConnection(CONNECTION_STRING);
            try {
                Statement statement = conn.createStatement();
                statement.execute(CREATE_TABLE);
                statement = conn.createStatement();
                statement.execute(CREATE_POLISH_TABLE);
            } catch (SQLException e) {
                System.out.println("Error creating table: " + e.getMessage());
            }
            queryIngredientsByName = conn.prepareStatement(QUERY_INGREDIENTS_BY_NAME);
            queryIngredientsByNamePolish = conn.prepareStatement(QUERY_INGREDIENTS_BY_NAME_POLISH);
            queryInstructionsByName = conn.prepareStatement(QUERY_INSTRUCTIONS_BY_NAME);
            queryInstructionsByNamePolish = conn.prepareStatement(QUERY_INSTRUCTIONS_BY_NAME_POLISH);
            insertIntoTable = conn.prepareStatement(INSERT_INTO_TABLE);
            insertIntoTablePolish = conn.prepareStatement(INSERT_INTO_TABLE_POLISH);
            updateTable = conn.prepareStatement(UPDATE_TABLE);
            updateTablePolish = conn.prepareStatement(UPDATE_TABLE_POLISH);
            deleteFromTable = conn.prepareStatement(DELETE_FROM_TABLE);
            deleteFromTablePolish = conn.prepareStatement(DELETE_FROM_TABLE_POLISH);
            return true;
        } catch (SQLException e) {
            System.out.println("Failure opening connection " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    void close() {
        try {
            if (queryIngredientsByName != null)
                queryIngredientsByName.close();
            if (queryIngredientsByNamePolish != null)
                queryIngredientsByNamePolish.close();
            if (queryInstructionsByName != null)
                queryInstructionsByName.close();
            if (queryInstructionsByNamePolish != null)
                queryInstructionsByNamePolish.close();
            if (insertIntoTable != null)
                insertIntoTable.close();
            if (insertIntoTablePolish != null)
                insertIntoTablePolish.close();
            if (updateTable != null)
                updateTable.close();
            if (updateTablePolish != null)
                updateTablePolish.close();
            if (deleteFromTable != null)
                deleteFromTable.close();
            if (deleteFromTablePolish != null)
                deleteFromTablePolish.close();

            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("Failed to close connection: " + e.getMessage());
        }
    }

    List<Recipes> queryRecipes() {
        String s = "SELECT * FROM " + tableName;
        List<Recipes> recipes = new ArrayList<>();

        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(s)) {
            while (resultSet.next()) {
                Recipes recipe = new Recipes();
                recipe.setName(resultSet.getString(INDEX_RECIPE_NAME));
                recipe.setIngredients(resultSet.getString(INDEX_RECIPE_INGREDIENTS));
                recipe.setInstructions(resultSet.getString(INDEX_RECIPE_INSTRUCTIONS));
                recipes.add(recipe);
            }
            return recipes;

        } catch (SQLException e) {
            System.out.println("Failed to query names of recipes!");
            return null;
        }
    }

    String queryIngredientsByName(String name) {
        try {
            queryIngredientsByName.setString(1, name);
            queryIngredientsByNamePolish.setString(1, name);
            ResultSet resultSet;
            if (tableLanguage.equals(ENGLISH))
                resultSet = queryIngredientsByName.executeQuery();
            else
                resultSet = queryIngredientsByNamePolish.executeQuery();
            String s = resultSet.getString(1);
            String[] s1 = s.split(",");
            StringBuilder temp = new StringBuilder();
            for (int i = 0; i < s1.length; i++) {
                if (i == 0) {
                    if (s1[0].startsWith("-"))
                        temp.append(s1[0]).append("\n");
                    else temp.append("-").append(s1[0]).append("\n");
                } else temp.append("-").append(s1[i]).append("\n");
            }
            s = temp.toString();
            return s;
        } catch (SQLException e) {
            System.out.println("Failure to query for ingredients: " + e.getMessage());
            return null;
        }
    }

    String queryInstructionsByName(String name) {
        try {
            queryInstructionsByName.setString(1, name);
            queryInstructionsByNamePolish.setString(1, name);

            ResultSet resultSet;
            if (tableLanguage.equals(ENGLISH))
                resultSet = queryInstructionsByName.executeQuery();
            else
                resultSet = queryInstructionsByNamePolish.executeQuery();
            String s = resultSet.getString(1);
            s = s.replaceAll("\\. ", "\n");
            return s;
        } catch (SQLException e) {
            System.out.println("Failed to query instructions: " + e.getMessage());
            return null;
        }
    }

    boolean insertIntoTable(String name, String ingredients, String instructions) {
        try {
            if (tableLanguage.equals(ENGLISH)) {
                insertIntoTable.setString(1, name);
                insertIntoTable.setString(2, ingredients);
                insertIntoTable.setString(3, instructions);
                insertIntoTable.executeUpdate();
            } else {
                insertIntoTablePolish.setString(1, name);
                insertIntoTablePolish.setString(2, ingredients);
                insertIntoTablePolish.setString(3, instructions);
                insertIntoTablePolish.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Failed to insert into table: " + e.getMessage());
            return false;
        }
    }

    boolean updateQuery(String name, String newName, String newIngredients, String newInstructions) {
        try {
            if (tableLanguage.equals(ENGLISH)) {
                updateTable.setString(1, newName);
                updateTable.setString(2, newIngredients);
                updateTable.setString(3, newInstructions);
                updateTable.setString(4, name);
                updateTable.executeUpdate();
            } else {
                updateTablePolish.setString(1, newName);
                updateTablePolish.setString(2, newIngredients);
                updateTablePolish.setString(3, newInstructions);
                updateTablePolish.setString(4, name);
                updateTablePolish.executeUpdate();
            }
            return true;
        } catch (SQLException e) {
            System.out.println("Failure updating table: " + e.getMessage());
            return false;
        }
    }

    boolean deleteFromTable(String name) {
        try {
            if (tableLanguage.equals(ENGLISH)) {
                deleteFromTable.setString(1, name);
                deleteFromTable.executeUpdate();
            } else {
                deleteFromTablePolish.setString(1, name);
                deleteFromTablePolish.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            System.out.println("Failed to delete query: " + e.getMessage());
            return false;
        }
    }

    boolean insertRecipeToOtherTable(Recipes recipe) {

        return true;
    }

}
