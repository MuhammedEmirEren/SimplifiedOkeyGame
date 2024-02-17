import java.util.ArrayList;
import java.util.Arrays;

public class SimplifiedOkeyGame {

    Player[] players;
    Tile[] tiles;
    int tileCount;

    Tile lastDiscardedTile;

    int currentPlayerIndex = 0;

    public SimplifiedOkeyGame() {
        players = new Player[4];
    }

    public void createTiles() {
        tiles = new Tile[104];
        int currentTile = 0;

        // four copies of each value, no jokers
        for (int i = 1; i <= 26; i++) {
            for (int j = 0; j < 4; j++) {
                tiles[currentTile++] = new Tile(i);
            }
        }

        tileCount = 104;
    }

    /*
     * TODO: distributes the starting tiles to the players
     * player at index 0 gets 15 tiles and starts first
     * other players get 14 tiles, this method assumes the tiles are already shuffled
     */
    public void distributeTilesToPlayers() {
        int distributionNumber = 0;
        int numberOfTiles;
        for (int i = 0; i < this.players.length; i++)
        {
            if (i == 0)
            {
                numberOfTiles = 15;
            }
            else
            {
                numberOfTiles = 14;
            }

            for (int k = 0; k < numberOfTiles; k++)
            {
                this.players[i].addTile(this.tiles[distributionNumber]);
                distributionNumber++;
            }
        }



        // distributed tileas are removed.
        for (int i = 0; i < this.tileCount; i++)
        {

            if (i <= distributionNumber)
            {
                if (i + distributionNumber < this.tileCount)
                {
                    this.tiles[i] = this.tiles[i + distributionNumber];
                }
            }
            else
            {
                this.tiles[i] = new Tile(0);
            }
        } 
        tileCount = 104 - 57;
    }

    /*
     * TODO: get the last discarded tile for the current player
     * (this simulates picking up the tile discarded by the previous player)
     * it should return the toString method of the tile so that we can print what we picked
     */
    public String getLastDiscardedTile() {
        if(lastDiscardedTile!=null){
            players[currentPlayerIndex].addTile(lastDiscardedTile);
            return lastDiscardedTile.toString();
        }
        return "There is no tile in the table yet.";
    }

    /*
     * TODO: get the top tile from tiles array for the current player
     * that tile is no longer in the tiles array (this simulates picking up the top tile)
     * and it will be given to the current player
     * returns the toString method of the tile so that we can print what we picked
     */
    public String getTopTile() {
        tileCount--;
        Tile topTile = tiles[tileCount];
        tiles[tileCount] = null;
        players[getCurrentPlayerIndex()].addTile(topTile);
        return topTile.toString();
    }

    /*
     * TODO: should randomly shuffle the tiles array before game starts
     */
    public void shuffleTiles() {
        Tile[] helperList = new Tile[104];
        int currentLength = 104;
        for(int i = 0; i < tiles.length; i++)
        {
            int indexOfChosen = (int)(Math.random() * (currentLength));
            helperList[i] = tiles[indexOfChosen];

            // Swapping
            Tile temp = tiles[currentLength - 1];
            tiles[currentLength - 1] = tiles[indexOfChosen];
            tiles[indexOfChosen] = temp;

            currentLength--;
        }
        tiles = helperList;
    }

    /*
     * TODO: check if game still continues, should return true if current player
     * finished the game. use checkWinning method of the player class to determine
     */
    public boolean didGameFinish() {
        return players[currentPlayerIndex].checkWinning();
    }

    /* TODO: finds the player who has the highest number for the longest chain
     * if multiple players have the same length may return multiple players
     */
    public Player[] getPlayerWithHighestLongestChain() {

        ArrayList<Player> winners=new ArrayList<>();
        int maxChain=0;
        for(Player p:players){
            if(p.findLongestChain()>maxChain){
                winners.clear();
                winners.add(p);
                maxChain=p.findLongestChain();
            }
            else if(p.findLongestChain()==maxChain){
                winners.add(p);
            }
        }
        return winners.toArray(new Player[0]);
    }
    
    /*
     * checks if there are more tiles on the stack to continue the game
     */
    public boolean hasMoreTileInStack() {
        return tileCount != 0;
    }

    /*
     * TODO: pick a tile for the current computer player using one of the following:
     * - picking from the tiles array using getTopTile()
     * - picking from the lastDiscardedTile using getLastDiscardedTile()
     * you should check if getting the discarded tile is useful for the computer
     * by checking if it increases the longest chain length, if not get the top tile
     */
    public void pickTileForComputer() {

        int longestChain = players[currentPlayerIndex].findLongestChain();
        Tile controllerTile = lastDiscardedTile;
        
        players[currentPlayerIndex].addTile(controllerTile);
        int newLongestChain = players[currentPlayerIndex].findLongestChain();

        int position = players[currentPlayerIndex].findPositionOfTile(controllerTile);
        players[currentPlayerIndex].getAndRemoveTile(position);
        
        if(newLongestChain > longestChain)
        {
            getLastDiscardedTile();
        }
        else
        {
            getTopTile();
        }
    }
    /*
     * TODO: Current computer player will discard the least useful tile.
     * you may choose based on how useful each tile is
     */
    public void discardTileForComputer() {
        boolean duplicateFound = false;

        int leastUsefulIndex = 0;

        for(int i = 0; i < players[currentPlayerIndex].numberOfTiles - 1 && !duplicateFound; i++)
        {
            if(players[currentPlayerIndex].getTiles()[i].matchingTiles(players[currentPlayerIndex].getTiles()[i+1]))
            {
                leastUsefulIndex = i; 
                duplicateFound = true; 
            }
        }

        if(!duplicateFound)
        {
            int longestChain = 0;

            for (int i = 0; i < players[currentPlayerIndex].numberOfTiles; i++)
            {
                Tile controllerTile = players[currentPlayerIndex].getTiles()[i];

                players[currentPlayerIndex].getAndRemoveTile(i);
                int tempLongest = players[currentPlayerIndex].findLongestChain();
                players[currentPlayerIndex].addTile(controllerTile);

                if(tempLongest > longestChain)
                {
                    leastUsefulIndex = i;
                    longestChain = tempLongest;
                }
            }
        }
        
        this.discardTile(leastUsefulIndex);
    }

    /*
     * TODO: discards the current player's tile at given index
     * this should set lastDiscardedTile variable and remove that tile from
     * that player's tiles
     */
    public void discardTile(int tileIndex) {
        lastDiscardedTile = players[currentPlayerIndex].getAndRemoveTile(tileIndex);
    }

    public void displayDiscardInformation() {
        if(lastDiscardedTile != null) {
            System.out.println("Last Discarded: " + lastDiscardedTile.toString());
        }
    }

    public void displayCurrentPlayersTiles() {
        players[currentPlayerIndex].displayTiles();
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

      public String getCurrentPlayerName() {
        return players[currentPlayerIndex].getName();
    }

    public void passTurnToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % 4;
    }

    public void setPlayerName(int index, String name) {
        if(index >= 0 && index <= 3) {
            players[index] = new Player(name);
        }
    }

}
