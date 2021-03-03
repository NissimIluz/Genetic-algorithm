import java.util.Random;
/**
 * A genetic algorithm to find the shortest path in an environment with obstacles.
 *
 * @author Nissim Iluz
 * @version 1
 * 
 * Operating Instructions: Creating an Object Containing:
 * 1. A description of the environment as a two-dimensional matrix, where 0 indicates a passage and 1 an obstacle.
 * 2. Starting point (row and column).
 * 3. Endpoint (row and column).
 * 
 * Create the solution by the generateSolution function.
 * 1 - Up movement, 2 - Right movement, 3 - Down movement, 4 - Left movement.
 */
public class Robot
{

    private final double Pm=0.01;
    private final double Pc=0.6;
    private final int N=20;
    private final int [][] ENVIRONMENT;
    private final int LENGTH;
    private final int START_POINT_ROW;
    private final int START_POINT_COLUMN;
    private final int END_POINT_ROW;
    private final int END_POINT_COLUMN;
    private int [][] _population;
    private final double FINE_LOCATION=1;
    private final int MAX_NUM_GENERATION=1000;
    private final int WITHOUT_CHANGE=100 ;
    private double _avg;
    private double _max_f=0;
    private int [] _solution;

    /**
     * @param environment  A two-dimensional matrix that describes an environment in which the robot should move.
     *                      1 marks an obstacle and 0 a crossing.
     * @param start_point_row  the starting row of the robot.
     * @param start_point_column  the starting column of the robot.
     * @param end_point_row  the ending row of the robot.
     * @param end_point_column  the ending column of the robot.
     */
    public Robot(int[][] environment, int start_point_row, int start_point_column, int end_point_row, int end_point_column ){
        ENVIRONMENT=environment;
        LENGTH=Math.abs(end_point_column-start_point_column)+Math.abs(start_point_row-end_point_row);
        START_POINT_ROW=start_point_row;
        START_POINT_COLUMN=start_point_column;
        END_POINT_ROW=end_point_row;
        END_POINT_COLUMN=end_point_column;
    }
    
    /**
     * get solution
     * @return  the solution as array.
     */
    public int[] getSolution()
    {
        if(_solution==null)
            return null;
        int [] solution= new int [_solution.length];
        for(int i=0;i<_solution.length;i++)
            solution[i]= _solution[i];
        return solution;
    }

    /**
     * @return  the solution as string.
     */
    public String toString()
    {
        String s="";
        if(_solution==null)
            return s;
        for(int i=0;i<_solution.length;i++)
            s=s+_solution[i];
        return s;
    }
    
    /**
     * create a path
     */
    public void generateSolution() {
        if(LENGTH==0 || notInTheMatrix()) {
            int[] solution={0};
            _solution=solution;
            System.out.println("NOT INVOLVED INPUT");
        }
        else{
            create_random_population();

            int i=0;
            int counter=0;
            while(i<MAX_NUM_GENERATION && counter<WITHOUT_CHANGE) {
                double temp=_max_f;
                generation();

                i++;
                if(temp>=_max_f)
                    counter++;
                else 
                    counter=0;
            }
        }
    }

    private boolean notInTheMatrix()
    {
        int length=ENVIRONMENT[0].length;
        for(int i=0;i<ENVIRONMENT.length;i++)
            if(length!=ENVIRONMENT[i].length)
                return true;
        if(START_POINT_ROW<0 || START_POINT_COLUMN<0 || END_POINT_COLUMN<0 ||END_POINT_ROW<0)
            return true;
        if(START_POINT_ROW>ENVIRONMENT[0].length-1 || START_POINT_COLUMN>ENVIRONMENT.length-1)
            return true;
        if(END_POINT_ROW>ENVIRONMENT[0].length-1 || END_POINT_COLUMN>ENVIRONMENT.length-1)
            return true;
        return false;
    }

    private void create_random_population(){
        _population=new int[N][];
        for(int i=0;i<N;i++) {
            int row=START_POINT_ROW;
            int column=START_POINT_COLUMN;
            int base1;
            _population[i]=new int [LENGTH];

            for(int j=0;j<LENGTH;j++)
            {
                boolean legal=false;
                Random base = new Random();
                base1=base.nextInt(4)+1;
                if(base1==1 &&  row!=0 ){
                    legal=true;
                    row--;
                }
                if(base1==2 && column!=ENVIRONMENT[row].length-1 ){
                    legal=true;
                    column++;
                }
                if(base1==3 && ENVIRONMENT.length-1!= row){
                    legal=true;
                    row++;
                }
                if(base1==4 && column!=0 ){
                    legal=true;
                    column--;
                }
                if(legal)
                    _population[i][j]=base1;
                else
                    j--;
            }
        }
    }

    private void generation() {
        int [][] new_generation=new int[N][];
        double [] evaluation_function=new double[N];
        int [][] parents= new int[N][];
        double max=0;
        double sum=0;
        for(int j=0;j<N;j++) {
            evaluation_function[j]=evaluation_function(_population[j]);
            sum=sum+evaluation_function[j];
            if(evaluation_function[j]>max) {
                max=evaluation_function[j];
                if(max>_max_f) {
                    _solution=_population[j];
                    _max_f=max;
                }
            }
        }

        _avg=sum/N;
        for(int j=0;j<N;j++) 
            evaluation_function[j]=evaluation_function[j]/sum;
        parents[0]=_solution;
        parents[N-1]=_solution;
        for(int j=1;j<N-1;j++){
            double p=Math.random();
            double p_sum=0;
            int i=0;
            while(p>p_sum && i <evaluation_function.length){
                p_sum=p_sum+evaluation_function[i];
                i++;
            }
            i--;
            parents[j]=_population[i];
        }
        Random r = new Random();
        int length=r.nextInt(LENGTH);
        for(int j=0;j<parents.length;j=j+2) {
            int [][] children=crossover(parents[j],parents[j+1],length);
            _population[j]=mutation(children[0]);
            _population[j+1]=mutation(children[1]); 
        }
    }

    private int[] mutation(int []chromosome) {
        int row=START_POINT_ROW;
        int column=START_POINT_COLUMN;
        boolean legal=true;
        double p_mutation;
        int i;
        for(i=0;i<chromosome.length;i++) {

            if(legal)
                p_mutation = Math.random();
            else
                p_mutation=Pm;

            legal=false;
            if(p_mutation<=Pm) {
                Random base = new Random();
                chromosome[i]=base.nextInt(4)+1;
            }

            if(chromosome[i]==1 && (i==0 ||chromosome[i-1]!=3) && row!=0 ){
                legal=true;
                row--;
            }           
            if(chromosome[i]==2 && (i==0 ||chromosome[i-1]!=4) && column!=ENVIRONMENT[row].length-1 ){
                legal=true;
                column++;
            }
            if(chromosome[i]==3 && (i==0 ||chromosome[i-1]!=1) && ENVIRONMENT.length-1!= row ){
                legal=true;
                row++;
            }
            if(chromosome[i]==4 && (i==0 ||chromosome[i-1]!=2) && column!=0 ){
                legal=true;
                column--;
            }

            if(!legal)
                i--;
        }
        return chromosome;
    }

    private int[][] crossover(int[] chromosome1,int[] chromosome2, int location) {
        double p = Math.random();
        int [][]children=new int[2][LENGTH];
        int i=0;
        if(p<=Pc && (!(chromosome1.equals(chromosome2)))){ //
            for(;i<LENGTH;i++) {
                if(i<location) {
                    children[0][i]=chromosome1[i];
                    children[1][i]=chromosome2[i];
                }
                else{
                    children[0][i]=chromosome1[i];
                    children[1][i]=chromosome2[i];
                }
            }
        }
        else {
            for(;i<LENGTH;i++) {
                children[0][i]=chromosome1[i];
                children[1][i]=chromosome2[i];
            }
        }
        return children;  //returns new objects
    }

    private double evaluation_function(int[] chromosome) {
        int row=START_POINT_ROW;
        int column=START_POINT_COLUMN;
        for(int i=0;i<chromosome.length;i++) {
            if(chromosome[i]==1)             
                row--;
            if(chromosome[i]==2 )
                column++;
            if(chromosome[i]==3 )
                row++;
            if(chromosome[i]==4 )
                column--;
        }
        double f=FINE_LOCATION*Math.abs(column-END_POINT_COLUMN);
        f=f+FINE_LOCATION*Math.abs(END_POINT_ROW-row);

        return 2*(FINE_LOCATION*LENGTH)-f;
    }

    
}