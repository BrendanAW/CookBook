package cookbook.ui;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class BookClientSender extends Thread {
    private static String HOST;
    private static int SOCLOC;

    public BookClientSender() {
        SOCLOC = 5000;
        HOST = "localhost";
    }

    @Override
    public void run() {
        try (Socket socket = new Socket(HOST, SOCLOC)) {
            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            StringBuilder outputString;
            List<Recipes> allRecipes = new ArrayList<>(Datasource.returnInstance().queryRecipes());
            output.println(Datasource.returnInstance().getTableName());
            for (Recipes r : allRecipes) {
                outputString = new StringBuilder();
                outputString.append(r.getName()).append("|");
                outputString.append(r.getIngredients()).append("|");
                outputString.append(r.getInstructions()).append("|");
                output.println(outputString);
            }
        } catch (IOException e) {
            System.out.println("Client eror: " + e.getMessage());
            e.printStackTrace();
        }


    }
}
