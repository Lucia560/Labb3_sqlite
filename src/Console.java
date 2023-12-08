import java.sql.*;
import java.util.Scanner;



public class Console {
    public static void main(String[] args) {
        Console console = new Console();
        console.visaMenu();
    }

    private static Scanner scanner = new Scanner(System.in);

    // connection to database
    private static Connection connect() {
        String url = "jdbc:sqlite:/Users/lucia/Downloads/sqlite-tools-win-x64-3440200/identifier.sqlite/";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    // huvudmeny
    private static void visaMenu() {
        while (true) {
            System.out.println("""

                     Välj:\s
                    1. Recept \s
                    2. Hantera recept\s
                    3. Handera ingredienser
                    4. Sök efter recept\s
                    5. Visa statistik\s
                    6. Stäng av programmet\s""");

            int usersChoice = Integer.parseInt(scanner.nextLine());
            switch (usersChoice) {
                case 1:
                    visaReceptSubMenu(); // done
                    break;
                case 2:
                    visaHandRecSubMenu(); // done
                    break;
                case 3:
                    visaHandIngSubMenu(); // done
                    break;
                case 4: findRecept();  // done
                    break;
                case 5:
                    visaStatistikSubMenu(); // done
                    break;
                case 6:
                    System.out.println(" Stänger programmet! ");
                    return;
                default:
                    System.out.println(" Ogiltigt val. Försök igen");
                    break;

            }
        }
    }




    // Visa recept undermeny
    private static void visaReceptSubMenu() {
        while (true) {
            System.out.println("""

                    Visa recept undermeny:\s
                    1. Visa alla recept:\s
                    2. Visa recept och deras ingredienser:\s
                    3. Visa alla favorit recept:\s
                    4. Återvänd till huvudmenyn.\s
                    """
            );

            int usersChoice = Integer.parseInt(scanner.nextLine());
            switch (usersChoice) {
                case 1:
                    visaAllaRecept(); // done
                    break;
                case 2:
                    visaReceptOchIngred();// done
                    break;
                case 3:
                    visaFavoritRecept(); // done
                    break;
                case 4: System.out.println(" Du återvänder till huvudmenyn.");// done
                    return;
                default:
                    System.out.println("Ogiltigt val.Forsok igen"); // done
                    break;
            }

        }


    }

    // visa recept, submenu 1.
    public static void visaAllaRecept() {
        String sql = "SELECT * FROM recept";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop
            while (rs.next()) {
                System.out.println(rs.getInt("receptId") + "\t" +
                        rs.getString("receptNamn") + "\t" +
                        rs.getInt("receptRating"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // visa recept, submenu 2.
    private static void visaReceptOchIngred() {
        String sql = "SELECT recept.receptId, recept.receptNamn, recept.receptRating, ingredienser.ingredienserNamn, ingredienser.ingredienserVikt " +
                "FROM recept " +
                "JOIN ingredienser ON recept.receptId = ingredienser.ingredienserReceptId " +
                "ORDER BY recept.receptId";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // Kolumner
            System.out.printf("%-10s %-30s %-10s %-20s %s%n", "ReceptId", "ReceptNamn", "Rating", "Ingrediens", "Vikt");


            while (rs.next()) {
                // Formaterad utskrift
                System.out.printf("%-10d %-30s %-10d %-20s %dg%n",
                        rs.getInt("receptId"),
                        rs.getString("receptNamn"),
                        rs.getInt("receptRating"),
                        rs.getString("ingredienserNamn"),
                        rs.getInt("ingredienserVikt"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    // visa recept,submenu 3.
    private static void visaFavoritRecept() {
        String sql = "SELECT * FROM recept WHERE isFavorite = TRUE";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            //  kolumnerna med justerat format
            System.out.printf("%-10s %-30s %-6s%n", "ReceptId", "ReceptNamn", "Rating");

            while (rs.next()) {
                // Formaterad för varje rad
                System.out.printf("%-10d %-30s %-6d%n",
                        rs.getInt("receptId"),
                        rs.getString("receptNamn"),
                        rs.getInt("receptRating"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }




    //handera recept submenu.
    private static void visaHandRecSubMenu() {
        while(true){
            System.out.println("""
                    Handera recept undermeny:\s
                    1.Lägg till ett nytt recept\s
                    2.Uppdatera ett recept\s
                    3.Sätt recept till favoriter\s
                    4.Radera recept\s 
                    5.Återvänd till huvudmenyn.\s
                    """
            );

        int userChoice = Integer.parseInt(scanner.nextLine());
        switch(userChoice) {
            case 1: adderaRecept(); // done
              break;
            case 2: uppdateraRecept();  // done
              break;
            case 3: adderaFavorit();  // done
              break;
            case 4: raderaRecept(); // done
              break;
            case 5: System.out.println("Du återvänder till huvudmenyn");  // done
              return;
            default:
                System.out.println("Ogiltigt val.Forsok igen");     //done
                break;
        }
        }

    }

    // handera recept, submenu 1.
    private static void adderaRecept() {
        // ta emot nya info
        System.out.print("Ange recept ID: ");
        int receptId = Integer.parseInt(scanner.nextLine());
        System.out.print("Ange receptnamn: ");
        String receptNamn = scanner.nextLine();
        System.out.print("Ange recept rating (1-5): ");
        int receptRating = Integer.parseInt(scanner.nextLine());

        String sql = "INSERT INTO recept(receptId, receptNamn, receptRating) VALUES(?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, receptId);
            pstmt.setString(2, receptNamn);
            pstmt.setInt(3, receptRating);
            int affectedRows = pstmt.executeUpdate();

            // Kontrollera uppdate
            if (affectedRows > 0) {
                System.out.println("Ett nytt recept har lagts till.\n");
            } else {
                System.out.println("Något gick fel och receptet lades inte till.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // handera recept, submenu 2:
    private static void uppdateraRecept() {
        // ta emot nya info
        System.out.print("Ange ID för det recept som du vill uppdatera: ");
        int receptId = Integer.parseInt(scanner.nextLine());
        System.out.print("Ange det nya namnet på receptet: ");
        String receptNamn = scanner.nextLine();
        System.out.print("Ange den nya ratingen för receptet (1-5): ");
        int receptRating = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE recept SET receptNamn = ?, receptRating = ? WHERE receptId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, receptNamn);
            pstmt.setInt(2, receptRating);
            pstmt.setInt(3, receptId);

            // update
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Receptet med ID " + receptId + " har uppdaterats. \n");
            } else {
                System.out.println("Inget recept med angivet ID hittades.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //handera  recept, submenu 3
    private static void adderaFavorit() {
        System.out.print("Ange ID för det recept som du vill markera som favorit: ");
        int receptId = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE recept SET isFavorite = ? WHERE receptId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setBoolean(1, true);
            pstmt.setInt(2, receptId);

            //  uppdate
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Receptet med ID " + receptId + " har markerats som favorit. \n");
            } else {
                System.out.println("Inget recept med angivet ID hittades.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    //handera recept, submenu 4
    private static void raderaRecept() {
        System.out.println("Skriv in receptId för receptet som ska tas bort: ");
        int inputId = Integer.parseInt(scanner.nextLine());
        delete(inputId);
    }

    private static void delete(int receptid) {
        String sql = "DELETE FROM recept WHERE receptId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, receptid);
            // uppdate
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort receptet. \n");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    // handera ingredienser submenu.
    private static void visaHandIngSubMenu() {
        while (true) {
            System.out.println("""
                    Hantera ingredienser undermeny\s
                    1. Lägg till en ingrediens\s
                    2. Uppdatera en ingrediens\s
                    3. Radera en ingrediens\s  
                    4. Återvänd till huvudmeny\s
                    """
            );
         int userChoice = Integer.parseInt(scanner.nextLine());

         switch(userChoice){
             case 1: adderaIngrediens(); // done
               break;
             case 2: uppdateraIngrediens(); // done
               break;
             case 3: raderaIngrediens(); // done
               break;
             case 4: System.out.println("Du återvänder till huvudmeny");
               return;
             default:System.out.println("Ogiltingt val. Försök igen");
               break;
         }
        }
    }



    // handera ingredienser, submenu 1.
    private static void adderaIngrediens() {
        System.out.print("Ange ingrediensernas ID: ");
        int ingredienserId = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ange ingrediensernas namn: ");
        String ingredienserNamn = scanner.nextLine().trim();
        System.out.print("Ange ingrediensernas vikt: ");
        int ingredienserVikt = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Ange receptets ID som denna ingrediens hör till: ");
        int ingredienserReceptId = Integer.parseInt(scanner.nextLine().trim()); // se till att ha denna korekt

        String sql = "INSERT INTO ingredienser(ingredienserId, ingredienserNamn, ingredienserVikt, ingredienserReceptId) VALUES(?,?,?,?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredienserId);
            pstmt.setString(2, ingredienserNamn);
            pstmt.setInt(3, ingredienserVikt);
            pstmt.setInt(4, ingredienserReceptId); // Lägg till detta värde i din PreparedStatement
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("En ny ingrediens har lagts till.\n");
            } else {
                System.out.println("Något gick fel och ingrediensen lades inte till.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    //handera ingredienser, submeny 2.
    private static void uppdateraIngrediens() {
        // ta emot nya info
        System.out.print("Ange ID för det ingrediensen som du vill uppdatera: ");
        int ingredienserId = Integer.parseInt(scanner.nextLine());
        System.out.print("Ange namn för ingredienser: ");
        String ingredienserNamn = scanner.nextLine();
        System.out.print("Ange vikt for denna ingredient: ");
        int ingredienserVikt = Integer.parseInt(scanner.nextLine());

        String sql = "UPDATE ingredienser SET ingredienserNamn = ?, ingredienserVikt= ? WHERE ingredienserId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {


            pstmt.setString(1, ingredienserNamn);
            pstmt.setInt(2, ingredienserVikt);
            pstmt.setInt(3, ingredienserId);

            // update
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Ingredienserna  med ID " + ingredienserId + " har uppdaterats.\n");
            } else {
                System.out.println("Inget ingrediens med angivet ID hittades.");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //handera ingredienserna, submenu 3.
    private static void raderaIngrediens() {
        System.out.println("Skriv in ingredienserId för den som ska tas bort: ");
        int inputId = Integer.parseInt(scanner.nextLine());
        deleteIng(inputId);
    }

    private static void deleteIng(int ingredienserid) {
        String sql = "DELETE FROM ingredienser WHERE ingredienserId = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, ingredienserid);
            // uppdate
            pstmt.executeUpdate();
            System.out.println("Du har tagit bort denna ingredienter.\n ");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



   // sök efter ett recept
   private static void findRecept() {
       System.out.print("Ange namnet på det recept du vill söka efter: ");
       String receptAttHitta = scanner.nextLine();

       String sql = "SELECT * FROM recept WHERE receptNamn LIKE ?";

       try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

           pstmt.setString(1, "%" + receptAttHitta + "%");

           ResultSet rs = pstmt.executeQuery();

           // Formaterade kolumner
           System.out.printf("%-10s %-30s %-6s%n", "ReceptId", "ReceptNamn", "Rating");

           while (rs.next()) {
               System.out.printf("%-10d %-30s %-6d%n",
                       rs.getInt("receptId"),
                       rs.getString("receptNamn"),
                       rs.getInt("receptRating"));
           }
       } catch (SQLException e) {
           System.out.println(e.getMessage());
       }
   }




    // visa statistic submenu.
    private static void visaStatistikSubMenu() {
        while(true){
            System.out.println("""
                    1.Visa antal recept \s
                    2.Visa antal Ingredienser\s
                    3.Återvänd till huvudmeny
                    """
            );
        int userChoice = Integer.parseInt(scanner.nextLine());
        switch(userChoice){
            case 1: visaAntalRecept();
              break;
            case 2: visaAntalIngredienser();
              break;
            case 3: System.out.println("Du återvänder till huvudmeny");
              return;
            default: System.out.println(" Ogiltigt val. Försök igen.");
        }

        }
    }
    //visa statistik, submenu 1.
    private static void visaAntalRecept() {
        String sql = "SELECT COUNT(*) AS total FROM recept";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("Totalt antal recept: " + count+"\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    //visa statistik, submenu 2.
    private static void visaAntalIngredienser() {
        String sql = "SELECT COUNT(*) AS total FROM ingredienser";

        try (Connection conn = connect();
             Statement stmt  = conn.createStatement();
             ResultSet rs    = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int count = rs.getInt("total");
                System.out.println("Totalt antal ingredienser: " + count +"\n");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



}




















