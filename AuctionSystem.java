package HW6;

/**
* This <code>AuctionTable</code> class allows the user to interact with the
* database by listing open auctions, make bids on open auctions, and create
* new auctions for different items. In addition, the class should provide
* the functionality to load a saved (serialized) AuctionTable or create a
* new one if a saved table does not exist.
*
* @author Minqi Shi
* email: minqi.shi@stonybrook.edu
* Stony Brook ID: 111548035
**/
import big.data.*;
import java.io.*;
import java.util.InputMismatchException;
import java.util.Scanner;

public class AuctionSystem implements Serializable
{
    private static AuctionTable auctionTable;
    private static String username;

    /**
    * The method should first prompt the user for a username. This should be
    * stored in username The rest of the program will be executed on behalf
    * of this user. 
    **/
    public static void main(String[] args) 
    {
        Scanner stdin = new Scanner(System.in);
        String command = "";
        boolean isCont = true;
        System.out.println("Starting...");
        System.out.println();
        File tempFile = new File("auction.obj");
        try
        {
            if (tempFile.exists()) 
            {
                FileInputStream file = new FileInputStream("auction.obj");
                ObjectInputStream inStream = new ObjectInputStream(file);
                auctionTable = (AuctionTable) inStream.readObject();
                System.out.println("Loading previous Auction Table... \n");
            }
            else
            {
                System.out.println("No previous auction table detected.\n" +
                  "Creating new table... ");
                auctionTable = new AuctionTable();
                System.out.println();
            }
        }
        catch(IOException | ClassNotFoundException fnfe)
        {
            System.out.println("Exception throwed in the beginning");
        }
        System.out.print("Please select a username: ");
        username = stdin.nextLine();
        username = username.trim();
        System.out.println();
        while (isCont)
        {
            try
            {
                System.out.println("Menu:"
                  +"\n    (D) - Import Data from URL"
                  +"\n    (A) - Create a New Auction"
                  +"\n    (B) - Bid on an Item"
                  +"\n    (I) - Get Info on Auction"
                  +"\n    (P) - Print All Auctions"
                  +"\n    (R) - Remove Expired Auctions"
                  +"\n    (T) - Let Time Pass"
                  +"\n    (Q) - Quit\n");
                System.out.print("Please select an option: ");
                command = stdin.nextLine();
                System.out.println();
                switch(command)
                {
                    case("D"): //Import Data from URL
                        System.out.print("Please enter a URL: ");
                        String url = stdin.nextLine();
                        System.out.println("\nLoading...");
                        url = url.trim();
                        auctionTable = AuctionTable.buildFromURL(url);
                        System.out.println("Auction data loaded successfully!");
                    break;
                    case("A"): //Create a New Auction
                        System.out.println("Creating new Auction as "
                         + username);
                        System.out.print("Please enter an Auction ID: ");
                        String idA = stdin.nextLine();
                        System.out.print("Please enter an Auction "
                          + "time (hours): ");
                        int timeRemaining = stdin.nextInt();
                        stdin.nextLine();
                        System.out.print("Please enter some Item Info: ");
                        // Core i5 2.7GHz - 4GB DDR3 - 750GB HDD
                        String info = stdin.nextLine();
                        Auction temp = new Auction(timeRemaining, 0, idA,
                          username, null, info);
                        auctionTable.putAuction(idA, temp);
                        auctionTable.putIntAuction
                          (auctionTable.getIntTableSize(), temp);
                        System.out.println("\nAuction "+ idA
                          + " inserted into table.");
                    break; 
                    case("B"): //Bid on an Item
                        System.out.print("Please enter an Auction ID: ");
                        String biddingId = stdin.nextLine();
                        Auction aucBidding = auctionTable
                          .getAuction(biddingId);
                        if (aucBidding == null) 
                        {
                            System.out.println("Can't find the auction "
                              + "with such id");
                        }
                        else if(aucBidding.getTimeRemaining()==0)
                        {
                            System.out.println("Auction " + biddingId
                              + " is CLOSED");
                            System.out.printf("    Current Bid: $ %.2f\n\n" 
                              , aucBidding.getCurrentBid());
                            throw new ClosedAuctionException();
                        }
                        else
                        {
                            System.out.println("Auction "+biddingId+" is OPEN");
                            if(aucBidding.getCurrentBid()!=0)
                                System.out.printf("    Current Bid: $ %.2f\n\n" 
                                  , aucBidding.getCurrentBid());
                            else
                                System.out.println("    Current Bid: None" ); 
                            System.out.print("What would you like to bid?: ");
                            double bidPrice = stdin.nextDouble();
                            stdin.nextLine();
                            if(bidPrice > aucBidding.getCurrentBid())
                            {
                                aucBidding.newBid(username,bidPrice);
                                System.out.println("Bid accepted.");
                            }
                            else 
                            {
                                System.out.println("Bid declined.");
                            }
                        }
                    break;
                    case("I"): // Get Info on Auction
                        System.out.print("Please enter an Auction ID: ");
                        String idI = stdin.nextLine();
                        Auction temAuc = auctionTable.getAuction(idI);
                        System.out.println();
                        if (temAuc==null) 
                        {
                            System.out.println("Can't find the auction "
                              + "with such id");
                        }
                        else
                        {
                        	System.out.println("Auction "+idI+":");
                        	System.out.println("    Seller: " 
                        	  + temAuc.getSellerName());
                        	System.out.println("    Buyer: "
                        	  + temAuc.getBuyerName());
                        	System.out.println("    Time: " 
                        	  + temAuc.getTimeRemaining() + " hours");
                        	System.out.println("    Info: "
                        	  + temAuc.getItemInfo());
                        }
                    break;
                    case("P"): // Print All Auctions
                        auctionTable.printTable();
                    break;
                    case("R"): // Remove Expired Auctions
                        auctionTable.removeExpiredAuctions();
                        System.out.println("Removing expired auctions...\n"
                          +"All expired auctions removed.");
                    break;
                    case("T"): // Let Time Pass
                        System.out.print("How many hours should pass: ");
                        int numTime = stdin.nextInt();
                        stdin.nextLine();
                        auctionTable.letTimePass(numTime);
                        System.out.println("Time passing...\n"
                          +"Auction times updated.");
                    break;
                    case("Q"):
                        try
                        {
                            FileOutputStream file = new FileOutputStream
                              ("auction.obj");
                            ObjectOutputStream outStream = new 
                              ObjectOutputStream(file);
                            outStream.writeObject(auctionTable);
                            System.out.println("Writing Auction Table to "
                              + "file... ");
                            System.out.println("Done!");
                            isCont = false;
                        }
                        catch(IOException e)
                        {
                            System.out.println("Exception throwed in Q");
                        }
                        
                    break;
                    default:
                        System.out.println("Unrecognized option."
                          +" Please choose another one.");
                }    
            }
            catch(IllegalArgumentException|InputMismatchException iae)
            {
                System.out.println("Invalid input. Please try again.");
            }
            catch (ClosedAuctionException cae) 
            {
                System.out.println("You can no longer bid on this item.");
            }
            catch (DataSourceException dse)
            {
                System.out.println("Can't find any information from the input"
                +"URL.");
            }
            finally
            {
                System.out.println();
            }
        }
        System.out.println("GoodBye.");
        
    }

}