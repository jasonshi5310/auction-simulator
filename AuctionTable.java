package HW6;

/**
* This <code>AuctionTable</code> class contains a hash table to provide
* constant time insertion and deletion.
*
* @author Minqi Shi
* email: minqi.shi@stonybrook.edu
* Stony Brook ID: 111548035
**/

import java.util.*;
import java.io.Serializable;
import big.data.*;

public class AuctionTable implements Serializable
{
    // The hash table storing the information.
    private Hashtable<String, Auction> table; 
    private Hashtable<Integer, Auction> intTable;
    private Integer intTableSize;
    public AuctionTable()
    {
        this.table = new Hashtable<>(97);
        this.intTable = new Hashtable<>(97);
        this.intTableSize = 0;
    }

    /**
    * Uses the BigData library to construct an AuctionTable from
    * a remote data source.
    *
    * @param URL
    *    String representing the URL fo the remote data source.
    *
    * <dd>Preconditions:
    *    <dt>URL represents a data source which can be connected to using 
    *    the BigData library. The data source has proper syntax.
    *
    * @return
    *    The AuctionTable constructed from the remote data source.
    *
    * @throws IllegalArgumentException
    *    Thrown if the URL does not represent a valid data source (can't
    *    connect or invalid syntax).
    *
    **/
    public static AuctionTable buildFromURL(String URL) 
      throws IllegalArgumentException
    {
        if (!URL.contains("http://")) 
        {
            throw new IllegalArgumentException();
        }
        DataSource ds = DataSource.connect(URL).load();
        /**
        * listing/seller_info/seller_name
          listing/auction_info/current_bid
          listing/auction_info/time_left
          listing/auction_info/id_num
          listing/auction_info/high_bidder/bidder_name
          //the following should be combined to get the information of the item
          listing/item_info/memory
          listing/item_info/hard_drive
          listing/item_info/cpu
        **/
        String[] sellerList = ds.fetchStringArray("listing/seller_info"
          +"/seller_name");
        String[] currentBidList = ds.fetchStringArray("listing/auction_info"
          +"/current_bid");
        String[] timeList = ds.fetchStringArray("listing/auction_info"
          +"/time_left");
        String[] idList = ds.fetchStringArray("listing/auction_info/id_num");
        String[] bidderList = ds.fetchStringArray("listing/auction_info"
          +"/high_bidder/bidder_name");
        String[] memoryList = ds.fetchStringArray("listing/item_info/memory");
        String[] hDList = ds.fetchStringArray("listing/item_info/hard_drive");
        String[] cpuList = ds.fetchStringArray("listing/item_info/cpu");
        //(int timeRemaining, double currentBid, String auctionID,
        // String sellerName, String buyerName, String itemInfo)
        AuctionTable table = new AuctionTable();
        for (int i = 0;i<idList.length;i++)
        {
            String itemInfo = cpuList[i]+" - "+memoryList[i]+" - "+hDList[i];
            String bidCur = currentBidList[i].substring(1);
            bidCur = bidCur.replaceAll(",","");
            double bid = Double.valueOf(bidCur);
            String timeStr = timeList[i];
            int time = 0;
            if(timeStr.contains("day"))
            {
                String timeDays = timeStr.substring(0,timeStr.indexOf("day")-1);
                time = time + 24 * Integer.valueOf(timeDays);
            }
            if(timeStr.contains("hour")&&timeStr.contains(","))
            {
                String timeHours = timeStr.substring(timeStr.indexOf(",")+2
                  , timeStr.indexOf("hour")-1);
                time = time + Integer.valueOf(timeHours);
            }
            else if(timeStr.contains("hour"))
            {
                String timeHours = timeStr.substring(0,timeStr.indexOf(" "));
                time = time + Integer.valueOf(timeHours);
            }
            sellerList[i] = sellerList[i].replaceAll("\n","");
            bidderList[i] = bidderList[i].replaceAll("\n","");
            sellerList[i] = sellerList[i].replaceAll(" ","");
            bidderList[i] = bidderList[i].replaceAll(" ","");
            Auction temp = new Auction(time, bid,
              idList[i].trim(), sellerList[i].trim(), 
              bidderList[i].trim(), itemInfo);
            table.putAuction(idList[i],temp);
            Integer iInt = i;
            table.putIntAuction(iInt,temp);
            table.setIntTableSize(table.getIntTableSize()+ 1);
        }
        return table;
    }

    /**
    * Manually posts an auction, and add it into the table.
    *
    * @param auctionID
    *    The unique key for this object
    *
    * @param auction 
    *    The auction to insert into the table with the corresponding auctionID
    *
    * <dd> Postcondition:
    *    <dt>The item will be added to the table if all given parameters
    *    are correct.
    *
    * @throws IllegalArgumentException
    *    If the given auctionID is already stored in the table.
    **/
    public void putAuction(String auctionID, Auction auction) 
      throws IllegalArgumentException
    {
        if(table.containsKey(auctionID))
        {
            throw new IllegalArgumentException();
        }
        table.put(auctionID, auction);
    }
    
    /**
    * Manually posts an auction, and add it into the table.
    *
    * @param i
    *    The position for this object 
    *
    * @param auction 
    *    The auction to insert into the table with the corresponding auctionID
    *
    * <dd> Postcondition:
    *    <dt>The item will be added to the table if all given parameters
    *    are correct.
    *
    * @throws IllegalArgumentException
    *    If the given auctionID is already stored in the table.
    **/
    public void putIntAuction(Integer i, Auction auction) 
      throws IllegalArgumentException
    {
        if(intTable.containsKey(i))
        {
            throw new IllegalArgumentException();
        }
        intTableSize++;
        intTable.put(i,auction);
    }

    /**
    * Get the information of an Auction that contains the given ID as key.
    *
    * @param auctionID
    *    The unique key for this object.
    *
    * @return 
    *    An Auction object with the given key, null otherwise.
    **/
    public Auction getAuction(String auctionID)
    {
        return table.get(auctionID);
    }

    /**
    * Simulates the passing of time. Decrease the timeRemaining of all Auction
    * objects by the amount specified. The value cannot go below 0.
    *
    * @param numHours
    *    the number of hours to decrease the timeRemaining value by.
    *
    * <dd>Postcondition
    *    <dt>All Auctions in the table have their timeRemaining timer
    *    decreased. If the original value is less than the decreased value, 
    *    set the value to 0.
    *
    * @throws IllegalArgumentException
    *    If the given numHours is non positive.
    **/
    public void letTimePass(int numHours) throws IllegalArgumentException
    {
        if (numHours<0)
            throw new IllegalArgumentException();
        Set<String> keySet = table.keySet();
        Integer i = 0;
        for(String key: keySet)
        {
            table.get(key).decrementTimeRemaining(numHours);
         //   intTable.get(i++).decrementTimeRemaining(numHours);
        }
    }

    /**
    * Iterates over all Auction objects in the table and removes them if they
    * are expired (timeRemaining == 0).
    *
    * <dd>Postcondition
    *    <dt>Only open Auction remain in the table.
    **/
    public void removeExpiredAuctions()
    {
        int[] removedInt = new int[intTableSize+1];
        for(int i = 0;i<intTableSize+1;i++)
        {
            if(intTable.get(i)!=null && intTable.get(i).getTimeRemaining() == 0)
            {
                removedInt[i] = i;
            }
        }
        for(int j = 0;j<removedInt.length;j++)
        {
            if(intTable.get(j)!= null
              &&intTable.get(j).getTimeRemaining() ==0)
            {
                table.remove(intTable.get(j).getAuctionId());
                intTable.remove(j);                
            }
        }
    }

    /**
    * Prints the AuctionTable in tabular form.
    **/
    public void printTable()
    {
        System.out.println(" Auction ID |      Bid   |        Seller         "
          +"|          Buyer          |    Time   |  Item Info");
        System.out.println("==============================================="
          +"==============================================================="
          +"=====================");
        for(Integer i = 0;i<=intTableSize;i++)
        {
            if(intTable.get(i)!=null)
                System.out.println(intTable.get(i));
        }
    }
    
    
    /**
     * Returns the size of the intTable.
     * 
     * @return
     *    the size of the intTable
     */
    public Integer getIntTableSize()
    {
        return intTableSize;
    }
      
    /**
     * Set the size of the intTable
     * 
     * @param i 
     *    the new size of the intTable
     */
    public void setIntTableSize(Integer i)
    {
        intTableSize = i;
    }
}