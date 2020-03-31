package cookbook.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BookClient extends Thread {
    private static int SOCLOC;
    private static String HOST;
    Recipes recipe;
    List<Recipes> newRecipes = new ArrayList<>();
    List<Recipes> oldRecipes = Datasource.returnInstance().queryRecipes();
    public BookClient() {
        SOCLOC = 5000;
        HOST = "localhost   ";
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(HOST, SOCLOC)) {
            BufferedReader echoes = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String response;
            String[] recipesSplit;
            int x = 0;
            StringBuilder tempResponse = new StringBuilder();

            do {
                if (x == 0) {
                    if (!echoes.readLine().equals(Datasource.returnInstance().getTableName()))
                        return;
                    x++;
                }
                response = echoes.readLine();
                tempResponse.append(response);
            } while (response != null);
            recipesSplit = tempResponse.toString().split("\\|");
            for (int i = 0; i < recipesSplit.length - 1; i += 3) {
                recipe = new Recipes();
                recipe.setName(recipesSplit[i]);
                recipe.setIngredients(recipesSplit[i + 1]);
                recipe.setInstructions(recipesSplit[i + 2]);
                newRecipes.add(recipe);
            }
            newRecipes.forEach(System.out::println);
            newRecipes.removeAll(oldRecipes);
            newRecipes.forEach(System.out::println);
            for (Recipes updatedRecipe : newRecipes) {
                Datasource.returnInstance().insertIntoTable(updatedRecipe.getName(), updatedRecipe.getIngredients(), updatedRecipe.getInstructions());
            }
            System.out.println("Successfully updated client recipe book!");

        } catch (IOException e) {
            System.out.println("Client Error: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}