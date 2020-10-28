package HW6;

/**
* This <code>Auction</code> class  represents an active
* auction currently in the database. 
*
* @author Minqi Shi
* email: minqi.shi@stonybrook.edu
* Stony Brook ID: 111548035
**/
import java.io.Serializable;

public class Auction implements Serializable
{
    private int timeRemaining; // The remaining time for the auction.
    private double currentBid; // The current highest bid.
    private String auctionID; // The id of the auction.
    private String sellerName; // The name of the seller.
    private String buyerName; // The name of the buyer.
    // The info of the item, including memory, hard drive and cpu.
    private String itemInfo; 

    /**
    * Returns a instance of the Auction class.
    * 
    * @param timeRemaining
    *    the time remained.
    * 
    * @param currentBid
    *    the highest bid price.
    * 
    * @param auctionID
    *    the auction id.
    * 
    * @param sellerName
    *    the name of the seller.
    * 
    * @param buyerName
    *    the name of the buyerName.
    * 
    * @param itemInfo
    *    the information of the item.
    **/
    public Auction(int timeRemaining, double currentBid, String auctionID,
      String sellerName, String buyerName, String itemInfo)
    {
    	this.timeRemaining = timeRemaining;
    	this.currentBid = currentBid;
    	this.auctionID = auctionID;
    	this.sellerName =sellerName;
    	this.buyerName = buyerName;
    	this.itemInfo = itemInfo;
    }

    /**
    * Returns the time remaining.
    *
    * @return
    *    The time remaining.
    **/
    public int getTimeRemaining()
    {
        return timeRemaining;
    }

    /**
    * Returns the current bid.
    *
    * @return
    *    The current bid.
    **/
    public double getCurrentBid()
    {
        return currentBid;
    }

    /**
    * Returns the auction id.
    *
    * @return
    *    The auction id.
    **/
    public String getAuctionId()
    {
        return auctionID;
    }

    /**
    * Returns the buyer's name.
    *
    * @return
    *    The buyer's name.
    **/
    public String getBuyerName()
    {
        return buyerName;
    }


    /**
    * Returns the seller's name.
    *
    * @return
    *    The seller's name.
    **/
    public String getSellerName()
    {
        return sellerName;
    }


    /**
    * Returns the information of the item.
    *
    * @return
    *    The information of the item.
    **/
    public String getItemInfo()
    {
        return itemInfo;
    }

    /**
    * Decreases the time remaining for this auction by the specified amount.
    * If time is greater than the current remaining time for the auction, 
    * then the time remaining is set to 0 (i.e. no negative times).
    *
    * <dt>Postcondition:
    *    <dd> Variable timeRemaining has been decremented by the indicated
    *    amount and is greater than or equal to 0.
    *
    * @param time
    *    The amount of time to decrease.
    **/
    public void decrementTimeRemaining(int time)
    {
        timeRemaining = timeRemaining - time;
        if (timeRemaining < 0)
        {
            timeRemaining = 0;
        }
    }

    /**
    * Makes a new bid on this auction. If bidAmt is larger than currentBid,
    * then the value of currentBid is replaced by bidAmt and buyerName is
    * replaced by bidderName.
    *
    * <dt>Preconditions:
    *    <dd>The auction is not closed (i.e. timeRemaining > 0).
    *
    * <dd>Postconditions:
    *    <dt>currentBid Reflects the largest bid placed on this object.
    *    If the auction is closed, throw a ClosedAuctionException.
    *
    * @throws ClosedAuctionException
    *    Thrown if the auction is closed and no more bids can be placed 
    *    (i.e. timeRemaining == 0).
    **/
    public void newBid(String bidderName, double bidAmt) 
      throws ClosedAuctionException
    {
        if (timeRemaining<=0)
            throw new ClosedAuctionException();
        if (currentBid < bidAmt)
        {
            currentBid = bidAmt;
            buyerName = bidderName;
        }
    }

    /**
    * Returns a string of data members in tabular form.
    **/
    public String toString()
    {
        // Auction ID |      Bid   |        Seller         
        //|          Buyer          |    Time   |  Item Info
        String temp = "";
        if (currentBid !=0)
            temp = String.format(" %s  | $%9.2f | %-22s| %-24s|%4d hours | %s",
              auctionID, currentBid, sellerName, buyerName, timeRemaining,
              itemInfo );
        else
            temp = String.format(" %s  |            | %-22s"
              + "|                         |%4d hours | %s",
              auctionID, sellerName, timeRemaining, itemInfo );
        return temp;
    }

}

class ClosedAuctionException extends Exception
{

}