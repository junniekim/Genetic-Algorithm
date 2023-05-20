import java.util.Random;

public class Genetic {

    // This function randomize generation 1
    public static void shuffle (City [] cities){
        Random rand = new Random();
        for (int i = 0; i < cities.length; i++) {
            int randomIndexToSwap = rand.nextInt(cities.length);
            City temp = cities[randomIndexToSwap];
            cities[randomIndexToSwap] = cities[i];
            cities[i] = temp;
        }
    }

    //This function calculates distance between one city to another
    public static double calculateDistance (int x1, int y1, int x2, int y2){
        return Math.sqrt(Math.pow((x2-x1),2)+Math.pow((y2-y1),2));
    }

    //This function adds up distance of all cities in a sequence
    public static double getTotalDistance (City[] cities){
        double total = 0;
        for (int i=0; i<cities.length-1;i++){
            total+=calculateDistance (cities[i].xCoor, cities[i].yCoor, cities[i+1].xCoor,cities[i+1].yCoor);
        }
        return total;
    }
    public static double findMin (double [] array){
        double min =array[0] ;
        for (int i =1 ; i <array.length; i++){
            if (min>=array[i]){
                min = array[i];
            }
        }
        return min;
    }
    //fitness test is based on the minimal distance in the set of sequence
    public static double[] fitnessTest (double[] array){
        double min = findMin(array);
        double [] returnArray = new double [array.length];
        for (int i =0; i<returnArray.length;i++){
            returnArray[i]= (min/(array[i]));
        }
        return returnArray;
    }
    public static double [] normalize (double[] array){
        double [] returnArray = new double [array.length];
        double total = 0 ;
        for (double i : array){
            total+=i;
        }
        for (int i = 0 ; i<array.length;i++){
            returnArray[i] = array[i]/total;
        }
        return returnArray;
    }
    public static double[] cumulate (double[] array){
        double counter =0;
        double [] returnArray = new double [array.length];
        for (int i =0; i<array.length;i++){
            counter+= array[i];
            returnArray[i] =counter;
        }
        return returnArray;
    }
    public static City [] merge (City [] array, int mergeBy){
        City[] returnCity = new City [array.length];
        //merge by can be 1 to 6
        for (int i = 0 ; i<mergeBy; i++){
            returnCity[i]= array[array.length-mergeBy+i];
        }
        for (int i = mergeBy ; i<array.length;i++){
            returnCity[i] = array[i-mergeBy];
        }
        return returnCity;
    }
    //This function helps with order cross over, ensuring that there will be no duplicate
    public static boolean dupCity (City[] array, City c){
        //return true if duplicate doesn't exist
        for (City ct: array){
            if (ct==null){
                continue;
            }
            else if (ct.representation==c.representation){
                return false;
            }
        }
        return true;
    }
    //cross over
    public static City [][] cross (City[][] array){
        Random rn = new Random();

        City [][] returnCity = new City [array.length][array[0].length] ;

        for (int i = 0 ; i<array.length ;i+=2){
            City [] valueHolder = new City [array[0].length];
            City [] valueHolder2 = new City [array[0].length];

            //get front and back to trim
            int front = rn.nextInt(array[0].length-2) + 1;
            int back = rn.nextInt(array[0].length-2) + 1;

            while (front == back ){
                back = rn.nextInt(array[0].length-2) + 1;
            }
            if (front>back){
                int t = front;
                front = back;
                back = t;
            }
            //System.out.println("FRONT "+front+" BACK "+back);

            //middle part is copied
            for (int j = front ; j<=back;j++){
                returnCity[i][j]= array[i][j];
                returnCity[i+1][j]= array[i+1][j];
            }

            //front and back bound = 1 to 7
            //store the sequence by merging
            int tracker1 =0;
            int tracker2 = 0;
            valueHolder = merge(array[i],array[i].length-back-1);
            valueHolder2= merge(array[i+1],array[i+1].length-back-1);


            for (int j = back+1 ;j<array[0].length;j++){
                while (returnCity[i][j]==null){
                   if (dupCity(returnCity[i], valueHolder2[tracker2])){
                       returnCity[i][j]=valueHolder2[tracker2];
                   }
                   tracker2++;
                }
                while (returnCity[i+1][j]==null){
                    if (dupCity(returnCity[i+1], valueHolder[tracker1])){
                        returnCity[i+1][j]=valueHolder[tracker1];
                    }
                    tracker1++;
                }
            }
            for (int j = 0 ;j<front;j++){
                while (returnCity[i][j]==null){
                    if (dupCity(returnCity[i], valueHolder2[tracker2])){
                        returnCity[i][j]=valueHolder2[tracker2];
                    }
                    tracker2++;
                }
                while (returnCity[i+1][j]==null){
                    if (dupCity(returnCity[i+1], valueHolder[tracker1])){
                        returnCity[i+1][j]=valueHolder[tracker1];
                    }
                    tracker1++;
                }
            }




        }
        return returnCity;
    }
    //mutate with 5 percent chance
    public static City [][] mutate (City[][] array){
        Random rn = new Random();
        City [][] returnCity = array;
        int mutateLottery = rn.nextInt(20) + 1;
        if (mutateLottery==10){
            int luckyNumber = rn.nextInt(array.length-1);


            int val1 = rn.nextInt(array[0].length-1) ;
            int val2 = rn.nextInt(array[0].length-1) ;
            while (val1 == val2 ){
                val2 = rn.nextInt(array[0].length-1) ;
            }
            City t = returnCity[luckyNumber][val1];
            returnCity[luckyNumber][val1]=returnCity[luckyNumber][val2];
            returnCity[luckyNumber][val2]= t;

        }
        return returnCity ;
    }
    public static void finalizeResult (City [][] array){
        double min = getTotalDistance(array[0]);
        int minIndex = 0;
        for (int i =1 ; i<array.length;i++){
            if (getTotalDistance(array[i])<min){
                min = getTotalDistance(array[i]);
                minIndex=i;
            }
        }
        System.out.print("The best route is ");
        for (int i = 0 ; i<array[minIndex].length;i++){
            System.out.print(array[minIndex][i].representation+" ");
        }
        System.out.println("with the distance of "+ min);
    }
    public static void main(String[] args) {

        // Each city with coordinates
        City one = new City (1,1, 1);
        City two = new City (4,5, 2);
        City three = new City (7,4, 3);
        City four = new City (5,4, 4);
        City five = new City (6,7, 5);
        City six = new City (8,7, 6);
        City seven = new City (4,8, 7);
        City eight = new City (2,4, 8);
        City nine = new City (9,2, 9);

        //Initialize gen1
        City [] citiesSeq1  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq2  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq3  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq4  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq5  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq6  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq7  = {one, two, three, four, five, six, seven, eight, nine};
        City [] citiesSeq8  = {one, two, three, four, five, six, seven, eight, nine};
        City [][] cityGeneration  = {citiesSeq1, citiesSeq2, citiesSeq3, citiesSeq4, citiesSeq5, citiesSeq6, citiesSeq7, citiesSeq8};
        //Each member in generation 1 holds different sequence
        for (City[] cityArray: cityGeneration){
            shuffle(cityArray);
        }

        //Loop will run 10000 times
        int loopCounter = 0 ;
        int loopMax = 100000;
        int numberOfSequences = 8;
        int numberOfCities =9;

        while (loopCounter<loopMax){

            double [] rawDistances = new double [numberOfSequences];
            double [] fitnessTested = new double [numberOfSequences];
            double [] normalized = new double [numberOfSequences];
            double [] cumulated = new double[numberOfSequences];
            int [] reproducingIndex= new int [numberOfSequences];
            City [][] newGeneration = new City [cityGeneration.length][cityGeneration[0].length];
            City[][] crossedNewGeneration = new City [cityGeneration.length][cityGeneration[0].length];
            City[][] mutatedNewGeneration = new City [cityGeneration.length][cityGeneration[0].length];



            //get raw distances for each sequences
            for (int i=0; i<cityGeneration.length;i++){
                rawDistances[i]= getTotalDistance(cityGeneration[i]);
            }

            //perform fitness testing
            fitnessTested= fitnessTest(rawDistances);

            //perform normalizing
            normalized = normalize(fitnessTested);

            //perform cumulating
            cumulated = cumulate(normalized);


            //Randomly select index of reproducing elements and store it in reproducingIndex
            for (int i=0 ; i<numberOfSequences;i++) {
                double random = Math.random();

                for (int j = 0; j < numberOfSequences; j++) {
                    if (random > cumulated[j]) {
                    }
                    else{
                        reproducingIndex[i]=j;
                        break;
                    }
                }
            }

            //New Generation candidates is chosen and stored in new Generation
            for (int i = 0; i<newGeneration.length;i++){
                newGeneration[i]=cityGeneration[reproducingIndex[i]];
            }

            //perform cross over
            crossedNewGeneration = cross(newGeneration);

            //mutate with 5 percent chance
            mutatedNewGeneration = mutate(crossedNewGeneration);

            //replace old generation
            cityGeneration = mutatedNewGeneration;

            loopCounter++;
        }//One cycle ends

        //Print answers based on 100000th generation
        finalizeResult (cityGeneration);
    }

}
