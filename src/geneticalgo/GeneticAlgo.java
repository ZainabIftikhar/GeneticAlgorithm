/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package geneticalgo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;
/**
 *
 * @author Acer
 */
public class GeneticAlgo {

    /**
     * @param args the command line arguments
     */
    
    static int examDays; 
    static int slotsPerDay; 
    
    static int noOfRooms = 48;
    static int Courses = 258;
    
    static int [][] Registeration = new int[Courses][Courses]; 
    static int [] Capacity =  new int [noOfRooms];
    static double [] fitness;
    
    static Vector Clashes = new Vector(Courses);
    static Vector chromosomes; //generation of chromosomes
    static Vector NewChromosomes;
    
    static String r = "C:\\Users\\Acer\\Documents\\NetBeansProjects\\GeneticAlgo\\src\\registration.data";
    static String c = "C:\\Users\\Acer\\Documents\\NetBeansProjects\\GeneticAlgo\\src\\capacity.room";
    static String g = "C:\\Users\\Acer\\Documents\\NetBeansProjects\\GeneticAlgo\\src\\general.info";
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        
        
        loadFiles();
        chromosomes = new Vector(examDays*slotsPerDay*noOfRooms);
        
        generateClashes();
        
        
        generateUniquePopulation();
        computeFitness(chromosomes);
        boolean fitness_check=TerminationCriteria();
        while(!fitness_check){
            CrossOver(chromosomes);
            computeFitness(chromosomes);
            fitness_check=TerminationCriteria();
            System.out.println((fitness_check));
            
        }
    }
    
    public static void loadFiles() throws FileNotFoundException, IOException{
        String sCurrentLine;
        int row = 0;
        String[] tokens;
        
        BufferedReader br = new BufferedReader(new FileReader(r));
        while ((sCurrentLine = br.readLine()) != null) {
            tokens = sCurrentLine.split(" ");
            for (int i = 0; i < tokens.length; i++){
                Registeration[row][i] = Integer.valueOf(tokens[i]);
            }
            ++row;
        }
        br.close();
        
        br = new BufferedReader(new FileReader(c));
        sCurrentLine = br.readLine();
        tokens = sCurrentLine.split(" ");
        for (int i = 0; i < tokens.length; i++){
            Capacity[i] = Integer.valueOf(tokens[i]);
        }
        br.close();
        
        br = new BufferedReader(new FileReader(g));
        sCurrentLine = br.readLine();
        tokens = sCurrentLine.split(" ");
        examDays = Integer.valueOf(tokens[0]);
        slotsPerDay  = Integer.valueOf(tokens[1]);
        br.close();
    }
    
    public static void generateClashes(){
        for (int i = 0; i < Courses; i++){
            Vector temp = new Vector(258);
            for (int j = 0; j < Courses; j++){
                if (Registeration[i][j] != 0 && i != j){
                    temp.add(j);
                }
            }
            Clashes.add(temp);
        }
    }
    
    public static void generateUniquePopulation(){
        int [][] temp = Registeration;
        int courseIndex = 0;
        
        for (int d = 1; d <= examDays && courseIndex < 257; d++){
            String day = Integer.toBinaryString(d);
            for (int k = day.length(); k < 2 ; k++)
                day = "0" + day; //connect empty 0s to make 2 bits
            
            for (int s = 1; s <= slotsPerDay && courseIndex < 257; s++){
                String slot = Integer.toBinaryString(s);
                for (int k = slot.length(); k < 3 ; k++)
                    slot = "0" + slot; //connect empty 0s to make 3 bits
            
                for (int r = 1; r < Capacity.length && courseIndex < 257; r++){
                    String room = Integer.toBinaryString(r);
                    for (int k = room.length(); k < 6 ; k++)
                        room = "0" + room; //connect empty 0s to make 6 bits
             
                    String subject = Integer.toBinaryString(courseIndex);
                    for (int k = subject.length(); k < 9 ; k++)
                        subject = "0" + subject; //connect empty 0s to make 9 bits
                 
                    chromosomes.add(day+slot+room+subject);
                    int studentsNotSeated = temp[courseIndex][courseIndex] - Capacity[r-1];
                    if (studentsNotSeated > 0){ //allocate more room if students are more
                        temp[courseIndex][courseIndex] -= Capacity[r-1];
                    }
                else
                    courseIndex++;
                }
            }
        }
    }
            
    public static void CrossOver(Vector Chromosomes){
        // Crossover generates  new chromosomes, if they are fit enough, they will pass 
        // on to the next generation
        NewChromosomes=new Vector(258);
        
        Random rand = new Random(); 
        int index = rand.nextInt(20);   //Randomly generating a point of crossover
        
        for(int i=0; i<129; i++)
        {
            int index1=RouletteWheelSelection();
            int index2=RouletteWheelSelection();
            if(index1!=-1 && index2!=-1)
            {
                String a = Chromosomes.get(index1).toString();   // crossover of 2 consecutive chromosomes
                String b = Chromosomes.get(index2).toString();

                String child1  = a.substring(0, index)+b.substring(index, b.length());
                String child2 = b.substring(0, index)+a.substring(index, a.length());

                child1=mutation(child1);
                child2=mutation(child2);
                NewChromosomes.add(child1);
                NewChromosomes.add(child2);
            }
        }
        chromosomes.addAll(NewChromosomes);
    }
    
    public static String mutation(String chromo){
               
        double mutation_prob=0.15;
        double random = new Random().nextDouble(); // generating a random number between 0-1
        double num = 0 + (random * (1 - 0));
        
        if(num< mutation_prob){
            Random rand=new Random();
            int index = rand.nextInt(chromo.length());  // index at which mutation occurs
            StringBuilder new_chromo = new StringBuilder(chromo);

            if (chromo.charAt(index) == '0')              // flip the bit
                new_chromo.setCharAt(index, '1');
            else if (chromo.charAt(index)=='1')
                new_chromo.setCharAt(index, '0');

            return new_chromo.toString();       // setting mutated chromosome in place of original one
        }
        return chromo;
    }
            
    
    public static void FitnessofDay(){
        for(int i=0; i < chromosomes.size()-1; i++){
        int day = Integer.parseInt(((String)chromosomes.get(i)).substring(0,2),2);
        if (day <= examDays && day > 0) {
            //do nothing
        }
        else
            fitness[i] += 0.1;
        }
    }
    
    public static int FitnessofCapacity(Vector Slot){
        int totalRoomCapacity = 0;
        for (int i = 0; i < Capacity.length; i++) totalRoomCapacity+= Capacity[i];
        int totalStudents = 0;
        for (int i = 0; i < Slot.size(); i++) {
            int course = Integer.parseInt(((String) Slot.get(i)).substring(11), 2);
            totalStudents += Registeration[course][course]; 
        }
        if (totalStudents < totalRoomCapacity)
            return 10;
        else
            return -(10*(totalStudents - totalRoomCapacity));
    }
    
    public static void computeFitness(Vector Chromosomes){
        Vector Slot1Courses = null;
        Vector Slot2Courses = null;
        Vector Slot3Courses = null;
        Vector Slot4Courses = null;
        Vector Slot5Courses = null;
        Vector Slot6Courses = null;
        
        DaySlot(Chromosomes,Slot1Courses, Slot2Courses,Slot3Courses,Slot4Courses,Slot5Courses, Slot6Courses, 1);
        DaySlot(Chromosomes,Slot1Courses, Slot2Courses,Slot3Courses,Slot4Courses,Slot5Courses, Slot6Courses, 2);
        DaySlot(Chromosomes,Slot1Courses, Slot2Courses,Slot3Courses,Slot4Courses,Slot5Courses, Slot6Courses, 3);
        
        FitnessofDay();
        TwoConsecutiveSlots();
        TwoInOneSlots();
        MoreThanTwoInOneSlot();
        ThreeConsecutiveSlots();
        MoreThanThreeInADay();
    }
    
    public static void DaySlot(Vector Chromosomes,Vector Slot1Courses, Vector Slot2Courses,Vector Slot3Courses, Vector Slot4Courses,Vector Slot5Courses, Vector Slot6Courses, int day){
        Slot1Courses = new Vector(48); //shouldn't they be 48?
        Slot2Courses = new Vector(48);
        Slot3Courses = new Vector(48);
        Slot4Courses = new Vector(48);
        Slot5Courses = new Vector(48);
        Slot6Courses = new Vector(48);
        
        for (int i = 0; i < Chromosomes.size(); i++){
            int d = Integer.parseInt(((String) Chromosomes.get(i)).substring(0,2), 2);
            int slot = Integer.parseInt(((String) Chromosomes.get(i)).substring(2,5), 2);
            int course = Integer.parseInt(((String) Chromosomes.get(i)).substring(11), 2); //i added room bits too
            
            if (d == day ) {
                switch (slot) {
                    case 1:
                        Slot1Courses.add(course);
                        break;
                    case 2:
                        Slot2Courses.add(course);
                        break;
                    case 3:
                        Slot3Courses.add(course);
                        break;
                    case 4:
                        Slot4Courses.add(course);
                        break;
                    case 5:
                        Slot5Courses.add(course);
                        break;
                    case 6:
                        Slot6Courses.add(course);
                        break;
                    default:
                        break;
                }
            }  
        }
        getFitness(Chromosomes,Slot1Courses,Slot2Courses,Slot3Courses,Slot4Courses,Slot5Courses,Slot6Courses);
    }
    
    public static void getFitness(Vector Chromosomes,Vector Slot1Courses, Vector Slot2Courses,Vector Slot3Courses, Vector Slot4Courses,Vector Slot5Courses, Vector Slot6Courses)
    {
        Vector temp1 = new Vector(258); temp1.addAll(Slot1Courses);
        Vector temp2 = new Vector(258); temp2.addAll(Slot2Courses);
        Vector temp3 = new Vector(258);  temp3.addAll(Slot3Courses);
        Vector temp4 = new Vector(258);  temp4.addAll(Slot4Courses);
        Vector temp5 = new Vector(258);  temp5.addAll(Slot5Courses);
        Vector temp6 = new Vector(258);   temp6.addAll(Slot6Courses);
       
        fitness = new double[Chromosomes.size()];
        int count=0;
        temp1.retainAll(temp2);
        temp2.retainAll(temp3);
        temp3.retainAll(temp4);
        temp4.retainAll(temp5);
        temp5.retainAll(temp6);
        temp6.retainAll(temp1);
        
       
        for (int i = 0; i < Chromosomes.size(); i++){  
            int course = Integer.parseInt(((String) Chromosomes.get(i)).substring(11), 2);
              
            if(temp1.contains(course) || temp2.contains(course)|| temp3.contains(course) || temp4.contains(course)
                    || temp5.contains(course) || temp6.contains(course))
            {
                fitness[i]=0.5;
            }
                
        }
        
        
        temp1.addAll(Slot1Courses);temp2.addAll(Slot2Courses);
        temp3.addAll(Slot3Courses);temp4.addAll(Slot4Courses);
        temp5.addAll(Slot5Courses);temp6.addAll(Slot6Courses);
        
        
        temp1.retainAll(temp2); temp1.retainAll(temp3);
        temp2.retainAll(temp3); temp2.retainAll(temp4);
        temp3.retainAll(temp4); temp3.retainAll(temp5);
        temp4.retainAll(temp5); temp4.retainAll(temp6);
        temp5.retainAll(temp6); temp5.retainAll(temp1);
        temp6.retainAll(temp1); temp6.retainAll(temp2);
        
        
        
        for (int i = 0; i < Chromosomes.size(); i++){
            int course = Integer.parseInt(((String) Chromosomes.get(i)).substring(11), 2);
            if(temp1.contains(course) || temp2.contains(course)|| temp3.contains(course) || temp4.contains(course)
                    || temp5.contains(course) || temp6.contains(course))
            {fitness[i]=0.4;}
            
        }
        
        temp1.addAll(Slot1Courses);temp2.addAll(Slot2Courses);
        temp3.addAll(Slot3Courses);temp4.addAll(Slot4Courses);
        temp5.addAll(Slot5Courses);temp6.addAll(Slot6Courses);
        
        
        temp1.retainAll(temp2); temp1.retainAll(temp3); temp1.retainAll(temp4);
        temp2.retainAll(temp3); temp2.retainAll(temp4); temp2.retainAll(temp5);
        temp3.retainAll(temp4); temp3.retainAll(temp5); temp3.retainAll(temp6);
        temp4.retainAll(temp5); temp4.retainAll(temp6); temp4.retainAll(temp1);
        temp5.retainAll(temp6); temp5.retainAll(temp1); temp5.retainAll(temp2);
        temp6.retainAll(temp1); temp6.retainAll(temp2); temp6.retainAll(temp3);
        
        
        
        for (int i = 0; i < Chromosomes.size(); i++){
            int course = Integer.parseInt(((String) Chromosomes.get(i)).substring(11), 2);
            if(temp1.contains(course) || temp2.contains(course)|| temp3.contains(course) || temp4.contains(course)
                    || temp5.contains(course) || temp6.contains(course))
            {fitness[i]=0.3;}
            
        }
        
            
        temp1.addAll(Slot1Courses);temp2.addAll(Slot2Courses);
        temp3.addAll(Slot3Courses);temp4.addAll(Slot4Courses);
        temp5.addAll(Slot5Courses);temp6.addAll(Slot6Courses);
        
        
        temp1.retainAll(temp2); temp1.retainAll(temp3); temp1.retainAll(temp4); temp1.retainAll(temp5);
        temp2.retainAll(temp3); temp2.retainAll(temp4); temp2.retainAll(temp5); temp2.retainAll(temp6);
        temp3.retainAll(temp4); temp3.retainAll(temp5); temp3.retainAll(temp6); temp3.retainAll(temp1);
        temp4.retainAll(temp5); temp4.retainAll(temp6); temp4.retainAll(temp1); temp4.retainAll(temp2);
        temp5.retainAll(temp6); temp5.retainAll(temp1); temp5.retainAll(temp2); temp5.retainAll(temp3);
        temp6.retainAll(temp1); temp6.retainAll(temp2); temp6.retainAll(temp3); temp6.retainAll(temp4);
        
        
        
        for (int i = 0; i < Chromosomes.size(); i++){
            int course = Integer.parseInt(((String) Chromosomes.get(i)).substring(11), 2);
            if(temp1.contains(course) || temp2.contains(course)|| temp3.contains(course) || temp4.contains(course)
                    || temp5.contains(course) || temp6.contains(course))
            {fitness[i]=0.2;}
            
        }
       
            
        temp1.addAll(Slot1Courses);temp2.addAll(Slot2Courses);
        temp3.addAll(Slot3Courses);temp4.addAll(Slot4Courses);
        temp5.addAll(Slot5Courses);temp6.addAll(Slot6Courses);
        
        
        temp1.retainAll(temp2); temp1.retainAll(temp3); temp1.retainAll(temp4); temp1.retainAll(temp5); temp1.retainAll(temp6);
        temp2.retainAll(temp3); temp2.retainAll(temp4); temp2.retainAll(temp5); temp2.retainAll(temp6); temp2.retainAll(temp1);
        temp3.retainAll(temp4); temp3.retainAll(temp5); temp3.retainAll(temp6); temp3.retainAll(temp1); temp3.retainAll(temp2);
        temp4.retainAll(temp5); temp4.retainAll(temp6); temp4.retainAll(temp1); temp4.retainAll(temp2); temp4.retainAll(temp3);
        temp5.retainAll(temp6); temp5.retainAll(temp1); temp5.retainAll(temp2); temp5.retainAll(temp3); temp5.retainAll(temp4);
        temp6.retainAll(temp1); temp6.retainAll(temp2); temp6.retainAll(temp3); temp6.retainAll(temp4); temp6.retainAll(temp5);
        
        
        
        for (int i = 0; i < Chromosomes.size(); i++){
            int course = Integer.parseInt(((String) Chromosomes.get(i)).substring(11), 2);
            if(temp1.contains(course) || temp2.contains(course)|| temp3.contains(course) || temp4.contains(course)
                    || temp5.contains(course) || temp6.contains(course))
            {fitness[i]=0.1;}
           
        }
         
    }
    
    public static int RouletteWheelSelection()
    {
        double sum=0;
        for(int i=0; i<fitness.length; i++)
            sum+=fitness[i];
        
        double random = new Random().nextDouble(); // generating a random number between 0-sum
        double num = 0 + (random * (sum - 0));
        
        double partial_sum=0;
        for(int i=0; i<fitness.length; i++){
            partial_sum+=fitness[i];
            if(partial_sum >=num)
                return i;
        }
        return -1;
    }
    
    public static boolean TerminationCriteria(){
        // 96% of population has to have fitness <=0.3
        int count=0;
        for(int i=0; i<fitness.length; i++){
            if(fitness[i]<=0.3)
                count++;
        }
        
        double criteria= ((double)95/ (double)100 ) * (double)fitness.length;
        if(count>=criteria)
            return true;
        return false;
    }

    public static void TwoConsecutiveSlots(){
        
        Vector PairsChecked = new Vector(33153); //258C2
        
        for(int i=0; i < chromosomes.size()-1; i++){
            int d = Integer.parseInt(((String) chromosomes.get(i)).substring(0,2), 2);
            int slot = Integer.parseInt(((String) chromosomes.get(i)).substring(2,5), 2);
            int course = Integer.parseInt(((String) chromosomes.get(i)).substring(11), 2);
            
            int d2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(0,2), 2);
            int slot2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(2,5), 2);
            int course2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(11), 2);
            
            if (d == d2){
                if (PairsChecked.contains(course+ " " + course2)){
                    //do nothing!
                }
                else if (((slot - slot2 == 1)|| (slot - slot2 == -1)) && course < 257 && course2 < 257){ //consecutive slot
                   if (((Vector)Clashes.get(course)).contains(course2)){ //clashing course found
                        fitness[i]+= 0.1;
                        PairsChecked.add(course+" "+course2);
                   }
                }
            }
        }
    }
    
    public static void TwoInOneSlots(){
        
        Vector PairsChecked = new Vector(33153); //258C2
        
        for(int i=0; i < chromosomes.size()-1; i++){
            int d = Integer.parseInt(((String) chromosomes.get(i)).substring(0,2), 2);
            int slot = Integer.parseInt(((String) chromosomes.get(i)).substring(2,5), 2);
            int course = Integer.parseInt(((String) chromosomes.get(i)).substring(11), 2);
            
            int d2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(0,2), 2);
            int slot2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(2,5), 2);
            int course2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(11), 2);
            
            if (d == d2 && slot == slot2){
                if (PairsChecked.contains(course+ " " + course2)){
                    //do nothing!
                }
                else if (course < 257 && course2 < 257){ //consecutive slot
                   if (((Vector)Clashes.get(course)).contains(course2)){ //clashing course found
                        fitness[i]+= 0.1;
                        PairsChecked.add(course+" "+course2);
                   }
                }
            }
        }
    }
    
    public static void MoreThanTwoInOneSlot(){
        
        Vector PairsChecked = new Vector(2829056); //258C3
        
        for(int i=0; i < chromosomes.size()-2; i++){
            int d = Integer.parseInt(((String) chromosomes.get(i)).substring(0,2), 2);
            int slot = Integer.parseInt(((String) chromosomes.get(i)).substring(2,5), 2);
            int course = Integer.parseInt(((String) chromosomes.get(i)).substring(11), 2);
            
            int d2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(0,2), 2);
            int slot2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(2,5), 2);
            int course2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(11), 2);
            
            int d3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(0,2), 2);
            int slot3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(2,5), 2);
            int course3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(11), 2);
            
            
            if (d == d2 && d2 == d3 && slot == slot2 && slot2 == slot3){
                if (PairsChecked.contains(course+ " " + course2 +  " " + course3)){
                    //do nothing!
                }
                else if (course < 258 && course2 < 258 && course3 < 258){ //consecutive slot
                   if (((Vector)Clashes.get(course)).contains(course2) && 
                           ((Vector)Clashes.get(course)).contains(course3)){ //clashing course found
                        fitness[i]+= 0.5; //super unfit
                        PairsChecked.add(course+ " " + course2 +  " " + course3);
                   }
                }
            }
        }
    }
    
    public static void ThreeConsecutiveSlots(){
        
        Vector PairsChecked = new Vector(33153); //258C2
        
        for(int i=0; i < chromosomes.size()-2; i++){
            int d = Integer.parseInt(((String) chromosomes.get(i)).substring(0,2), 2);
            int slot = Integer.parseInt(((String) chromosomes.get(i)).substring(2,5), 2);
            int course = Integer.parseInt(((String) chromosomes.get(i)).substring(11), 2);
            
            int d2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(0,2), 2);
            int slot2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(2,5), 2);
            int course2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(11), 2);
            
            int d3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(0,2), 2);
            int slot3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(2,5), 2);
            int course3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(11), 2);
            
            if (d == d2 && d2 == d3){
                if (PairsChecked.contains(course+ " " + course2 +  " " + course3)){
                    //do nothing!
                }
                else if (((((slot - slot2 == 1) || (slot - slot2 == -1)) 
                        && ((slot2 - slot3 == 1) || (slot3 - slot2 == -1))) 
                        || (((slot - slot3 == 1) || (slot - slot3 == -1)) 
                        && ((slot2 - slot3 == 1) || (slot3 - slot2 == -1)))
                        || (((slot - slot3 == 1) || (slot - slot3 == -1)) 
                        && ((slot - slot2 == 1) || (slot - slot2 == -1)))) 
                        && course < 258 && course2 < 258 && course3 < 258){ //consecutive slot
                        if (((Vector)Clashes.get(course)).contains(course2)
                                && ((Vector)Clashes.get(course)).contains(course3)){ //clashing course found
                        fitness[i]+= 0.5; //super unfit
                        PairsChecked.add(course+ " " + course2 +  " " + course3);
                   }
                }
            }
        }
    }
    
    public static void MoreThanThreeInADay(){
        
        Vector PairsChecked = new Vector(258); //258C4
        
        for(int i=0; i < chromosomes.size()-3; i++){
            int d = Integer.parseInt(((String) chromosomes.get(i)).substring(0,2), 2);
            int course = Integer.parseInt(((String) chromosomes.get(i)).substring(11), 2);
            
            int d2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(0,2), 2);
            int course2 = Integer.parseInt(((String) chromosomes.get(i+1)).substring(11), 2);
            
            int d3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(0,2), 2);
            int course3 = Integer.parseInt(((String) chromosomes.get(i+2)).substring(11), 2);
            
            
            int d4 = Integer.parseInt(((String) chromosomes.get(i+3)).substring(0,2), 2);
            int course4 = Integer.parseInt(((String) chromosomes.get(i+3)).substring(11), 2);
            
            if (d == d2 && d2 == d3 && d3 == d4){
                if (PairsChecked.contains(course+ " " + course2 +  " " + course3 + " " + course4)){
                    //do nothing!
                }
                else if (course < 258 && course2 < 258 && course3 < 258 && course4 < 258){
                   if (((Vector)Clashes.get(course)).contains(course2) && 
                           ((Vector)Clashes.get(course)).contains(course3) &&
                               ((Vector)Clashes.get(course)).contains(course4))    { //clashing course found
                        fitness[i]+= 0.5; //super unfit
                        PairsChecked.add(course+ " " + course2 +  " " + course3 + " " + course4);
                   }
                }
            }
        }
    }
}