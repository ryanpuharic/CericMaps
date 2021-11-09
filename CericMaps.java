// package com.codebind;

import java.util.Scanner;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class CericMaps
{
    String key;
    int totalSeconds;

    public CericMaps(String apikey)
    {
        key = apikey;
        totalSeconds = 0;
    }

    public boolean isValidAddress(String address)
    {
        boolean returnVal = true;
        // {"hits":[],"locale":"en"}
        String urlString = "https://graphhopper.com/api/1/geocode?q=" + address.replaceAll(" ", "%20") + "&locale=en&debug=true&key=6e5798d3-d358-432d-b839-078d917be99b";

        try
        {
            URL url = new URL(urlString);
            // if the webscrape has NO hits, its marked invalid.
            // https://graphhopper.com/api/1/geocode?q=2%20robertsville%20rd%20freehold&locale=en&debug=true&key=6e5798d3-d358-432d-b839-078d917be99b
            // is an example of an invalid input.
            returnVal = !(webScrape(url).equals("{\"hits\":[],\"locale\":\"en\"}"));
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }

        if(address.equals(""))
            returnVal = false;
        return returnVal;
    }

    public String webScrape(URL urlObj)
    {
        String rawText = "";
        try{ //reading and storing file
            BufferedReader in = new BufferedReader(new InputStreamReader(urlObj.openStream()));
            String line = in.readLine();
            rawText = line;
            in.close();

        }
        catch (IOException e) {
            System.out.println("web scrape failed");
        }

        return rawText;
    }

    // This converts address into coordinates, puts them in a 2d array.
    public double[][] getCoords(ArrayList<String> arr)
    {
        double[][] coords = new double[arr.size()][2];


        // row longitude col latitude
        for (int i = 0; i < arr.size(); i++) // row
        {
            String addy = arr.get(i).replaceAll(" ", "%20");

            String webpage = "https://graphhopper.com/api/1/geocode?q=" + addy + "&locale=en&debug=true&key=6e5798d3-d358-432d-b839-078d917be99b";

            System.out.println(webpage);

            /*try {
                String out = new Scanner(new URL(webpage).openStream(), "UTF-8").useDelimiter("\\A").next();
                System.out.println(out);
            }
            catch (IOException e)
            {

            }*/

            String s = "";

            try
            {
                URL url = new URL(webpage);
                Scanner scan = new Scanner(url.openStream());
                /*coords[i][0] = scan.nextDouble();
                coords[i][1] = scan.nextDouble();*/

                s = webScrape(url);
                s = s.substring(25,s.indexOf('e'));
                coords[i][0] = Double.parseDouble(s.substring(0,s.indexOf(',')));
                coords[i][1] = Double.parseDouble(s.substring(s.indexOf(':') + 1,s.indexOf('}')));

            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }

        return coords;
    }

    public int[][] getMatrix(double[][] coords, ArrayList<String> addresses)
    {
        int[][] distances;
        int[][] error = {{0}};
        String urlString = "https://graphhopper.com/api/1/matrix?";
        for(int i = 0; i < addresses.size(); i++){
            urlString += "point=" + coords[i][0];
            urlString += "," + coords[i][1] + "&";
        }
        urlString += "type=json&vehicle=car&debug=true&out_array=times&key=6e5798d3-d358-432d-b839-078d917be99b";
        
        /*
        "https://graphhopper.com/api/1/matrix?point=49.932707,11.588051&point=50.241935,10.747375&point=50.118817,11.983337&type=json&vehicle=car&debug=true&out_array=times&key=6e5798d3-d358-432d-b839-078d917be99b"
        */
        String rawText = "";
        try{ //reading and storing file
            URL url = new URL(urlString);
            rawText = webScrape(url);
        }
        catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }

        try{
            rawText = rawText.substring(10, rawText.indexOf("]]") + 1); //only keep distances

            int numBrackets = 0;
            for(int i = 0; i < rawText.length(); i++){ //determinde size of array
                if(rawText.charAt(i) == '[')
                    numBrackets++;
            }

            distances = new int[numBrackets][numBrackets]; //create 2d array for distances

            String num = "";
            int count = 0;
            for(int i = 0; i < rawText.length(); i++){ //fill array with just ints
                if(rawText.charAt(i) != '[' && rawText.charAt(i) != ']' && rawText.charAt(i) != ',')
                    num+= rawText.charAt(i);
                else{
                    if(!num.equals("")){
                        distances[count/numBrackets][count%numBrackets] = Integer.parseInt(num);
                        num = "";
                        count++;
                    }
                }
            }

            /*for(int i = 0; i < numBrackets; i++){
                for(int j = 0; j < numBrackets; j++)
                    System.out.print(distances[i][j] + " ");
                System.out.println();
            }*/
            return distances;
        }
        catch(StringIndexOutOfBoundsException e){
            System.out.println("Error");
        }

        return error;
    }

    public ArrayList<String> findRoute(ArrayList<String> arr, int[][] matrix, boolean response)
    {
        // the actual program
		/*
		Planning:
		brute force all combinations until we find a valid combination which is the lowest. 
        
		3 addresses
		a->a a->b a->c
		b->a b->b b->c
		c->a c->b c->c

		4 addresses
		a->a a->b a->c a->d
		b->a b->b b->c b->d
		c->a c->b c->c c->d
		d->a d->b d->c d->d

		*/

        // so the program cant reuse the same row or column
        // cant use the next row if it == column it came from?
        // cant use row, col when row == col [obviously]
        // go down then right until u return to initial point

        int factorial = 1;
        for (int i = 1; i <= arr.size(); i++)
        {
            factorial *= factorial;
        }


        int min = 2147483647;
        int totalSecs = 0;
        int[] mindex = new int[2];


        // Algorithm:
		/*
			There are arr.size() * arr.size() available starting points.
				- For a 2x2 matrix there are two route. (row!)
				- For a 3x3 matrix there are six routes.
				- For a 4x4 matrix there are 24 routes.
				- For a 5x5 matrix there are 120 routes.
			All we have to do is cycle between all possible possibilities.
				- Do nothing if the route is invalid.
				- How do we know if route is valid?
					- If they choose column 1, they go to row 1 for the next one.
					- For subsequent they can't choose the value [pervious col, previous row]
					- Cant use row, col when row = col


		*/
        //This works for 3x3 array
        //Next time we need to do 4x4 and 5x5 and also store the fastest row and col outside the loops to find the order of names later
        int[] order = new int[arr.size()];
        if(arr.size() < 3)
        {
            System.out.println("Error: minimum of three points are required.");
        }
        else if(arr.size() == 3)
        { //3 addresses
            for (int row = 0; row < arr.size(); row++)
            {
                for (int col = 0; col < arr.size(); col++) // these loops are for choosing the starting point
                {
                    totalSecs = 0;
                    // this guarantees row,col doesnt work when row == col
                    if(matrix[row][col] != 0)
                    {
                        if((response && row == 0) || !response)
                        {
                            totalSecs += matrix[row][col];
                            for(int i = 0; i < arr.size(); i++){
                                if(i != row && i != col)
                                    totalSecs+= matrix[col][i];
                            }


                            if(totalSecs < min){
                                min = totalSecs;
                                mindex[0] = row;
                                mindex[1] = col;
                            }
                        }
                    }
                }
            }
            order[0] = mindex[0];
            order[1] = mindex[1];
            order[2] = 3 - mindex[0] - mindex[1];
            totalSeconds = min;
        }
        else if(arr.size() == 4) //4 addresses
        {
            for (int row = 0; row < arr.size(); row++)
            {
                for (int col = 0; col < arr.size(); col++) // these loops are for choosing the starting point
                {
                    if((response && row == 0) || !response)
                    {
                        totalSecs = 0;
                        int currMin[][] = new int[2][2];
                        int count = 0;
                        // this guarantees row,col doesnt work when row == col
                        if(matrix[row][col] != 0)
                        {
                            totalSecs += matrix[row][col];
                            for(int i = 0; i < arr.size(); i++){
                                if(i != row && i != col){
                                    currMin[0][count] = totalSecs + matrix[col][i];
                                    currMin[1][count] = i;
                                    count++;
                                }
                            }

                            if(currMin[0][0] < currMin[0][1]){
                                totalSecs = currMin[0][0];
                                totalSecs += matrix[currMin[1][0]][currMin[1][1]];

                                if(totalSecs < min){
                                    min = totalSecs;
                                    mindex[0] = row;
                                    mindex[1] = col;

                                    order[0] = mindex[0];
                                    order[1] = mindex[1];
                                    order[2] = currMin[1][0];
                                    order[3] = currMin[1][1];
                                }
                            }
                            else{
                                totalSecs = currMin[0][1];
                                totalSecs += matrix[currMin[1][1]][currMin[1][0]];

                                if(totalSecs < min){
                                    min = totalSecs;
                                    mindex[0] = row;
                                    mindex[1] = col;

                                    order[0] = mindex[0];
                                    order[1] = mindex[1];
                                    order[2] = currMin[1][1];
                                    order[3] = currMin[1][0];
                                }
                            }
                        }
                    }
                }
            }
            totalSeconds = min;
        }
        else //5 addresses
        {
			/*
			for a 4:
			if u start    AB
			two choices BC BD
			one choice  CD DC

			If u start 						     AB 
			you instantly have 3 points  BC      BD      BE
			then u have 2 points       CD CE   DC DE    EC ED
			then u have one choice	   DE ED   CE EC    CD DC

									0,1
					1,2				1,3				1,4
				2,3		2,4		3,2		3,4		4,2		4,3
				3,4		4,3		2,4		4,2		2,3		3,2		

			Col of previous is going to be the row of the next. 
			AB BC CD DE 
			01 12 23 34

			last digit is the column, but that number is never used in the rows of the previous indexes. 

			AB BD DE EC
			01 13 34 42

			*/

            int lowestSecs = 0;
            for (int row = 0; row < arr.size(); row++)
            {
                for (int col = 0; col < arr.size(); col++) // these loops are for choosing the starting point
                {
                    // if valid starting point
                    //  - not when row and col are the same. 
                    // if (response == true && row == 0 && row != col) then that is a valid starting point
                    // if (response == false && row != col)
                    if ((row != col && !response) || (response && row == 0 && col != 0))
                    {
                        // now we have starting position (AB for instance)
                        // then we have three points to branch off to.
                        for (int a = 0; a < 5; a++) // cycle through row, a 0-4
                        {
                            // first branch, starts on the row of the inital col. 
                            // matrix[col][a]

                            // if a (new col) is not the inital row or a = col
                            // this is because if it went A->B, it cant be B->A or B->B
                            if (a != row && a != col)
                            {
                                // now we have the first branch succ-essfully completed. 
                                // matrix[col][a]

                                // now we have to do the second branch.
                                // 

                                for (int b = 0; b < 5; b++)
                                {
                                    // starts off on matrix[a][b 0-5]
                                    // b must not be equal to a, row, or col
                                    if (b != a && b != row && b != col)
                                    {
                                        // now we have legal matrix[a][b] values
                                        // from these we can derive the last values.
                                        for (int c = 0; c < 5; c++)
                                        {
                                            if (c != a && c != b && c != row && c != col)
                                            {
                                                // we have the final order of routes.
                                                // row,col --> col,a --> a,b --> b,c
                                                if (lowestSecs == 0)
                                                {
                                                    // if it is the first time, fill order with current value
                                                    order[0] = row;
                                                    order[1] = col;
                                                    order[2] = a;
                                                    order[3] = b;
                                                    order[4] = c;
                                                    lowestSecs = matrix[row][col] + matrix[col][a] + matrix[a][b] + matrix[b][c];
                                                }
                                                else
                                                {
                                                    int challengerSecs = matrix[row][col] + matrix[col][a] + matrix[a][b] + matrix[b][c];
                                                    if (lowestSecs > challengerSecs)
                                                    {
                                                        order[0] = row;
                                                        order[1] = col;
                                                        order[2] = a;
                                                        order[3] = b;
                                                        order[4] = c;
                                                        lowestSecs = challengerSecs;
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            totalSeconds = lowestSecs;
        }


        ArrayList<String> output = new ArrayList<String>();


        //formatting to get rid of %20
        for(int i = 0; i < arr.size(); i++)
        {
            if(arr.get(order[i]).indexOf('%') > 0)
            {
                String space = arr.get(order[i]).replaceAll("%20", " ");
                output.add(space);
            }
            else
                output.add(arr.get(order[i]));
        }

        return output;
    }

    public String getTime(){
        String result = "";
        if(totalSeconds > 3600){
            result += totalSeconds/3600 + " hours, ";
            totalSeconds %= 3600;
        }
        result += totalSeconds/60 + " minutes, " + totalSeconds%60 + " seconds.";
        return result;
    }
}