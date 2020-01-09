/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorquantization;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import javax.imageio.ImageIO;

/**
 *
 * @author Elliot
 */
public class VectorQuantization {
    public static Block Image = new Block();
    public static Block returnedImage = new Block();
    public static Vector<Block> finalBlocks = new Vector<Block>();
    public static Vector<Block> decompressd = new Vector<Block>();
    public static Vector<Block> myAverages = new Vector<>();
    public static Scanner input = new Scanner(System.in);
    
    public static void readImage(String filePath) throws IOException {
        int width = 0;
        int height = 0;
        File file = new File(filePath);
        BufferedImage image = null;
        image = ImageIO.read(file);

        width = image.getWidth();
        height = image.getHeight();
        int[][] pixels = new int[height][width];
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int rgb = image.getRGB(x, y);
                int alpha = (rgb >> 24) & 0xff;
                int r = (rgb >> 16) & 0xff;
                int g = (rgb >> 8) & 0xff;
                int b = (rgb >> 0) & 0xff;
                pixels[y][x] = r;
            }
        }
        
        Image.setHeight(height);
        Image.setWidth(width);
        Image.data = new float[width][height];
        
        for(int i = 0 ; i < width ; i++){
            for(int j = 0 ; j < height ; j++){
                Image.data[i][j] = Float.valueOf(pixels[i][j]);
            }
        }
        
        
    }
    public static void writeImage(){
    
        File fileout=new File("decompress.jpg");
        BufferedImage image2=new BufferedImage(Image.getWidth(),Image.getHeight(),BufferedImage.TYPE_INT_RGB );
 
        for(int x = 0; x < Image.getWidth(); x++)
        {
            for(int y = 0; y < Image.getHeight(); y++)
            {
                image2.setRGB(y,x,((int)returnedImage.data[x][y]<<16)|((int)returnedImage.data[x][y]<<8)|((int)returnedImage.data[x][y]));
            }
        }
        try
        {
            ImageIO.write(image2, "jpg", fileout);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public static void printBlock(int w , int h , Block b){
        System.out.println("block code : " + b.getCode());
        for(int i = 0 ; i < w ; i++){
            for(int j = 0 ; j < h ; j++){
                System.out.print(b.data[i][j] + " ");
            }
            System.out.println("");
        }
    }
    public static void printManyBlocks(Vector<Block> blocks){
        for(int i = 0 ; i < blocks.size() ; i++){
            printBlock(blocks.get(i).getWidth(), blocks.get(i).getHeight(), blocks.get(i));
            System.out.println();
        }
    }
    public static Block getBlock(){
        Block b = new Block();
        int height = 6 , width = 6;
        /*System.out.println("enter the size and width you want");
        System.out.print("height : ");
        height = input.nextInt();
        System.out.print("\nwidth : ");
        width = input.nextInt();
        */
        b.setWidth(width);
        b.setHeight(height);
        b.data = new float[width][height];
        float arr[] = {1,2,7,9,4,11,3,4,6,6,12,12,4,9,15,14,9,9,10,10,20,18,8,8,4,3,17,16,1,4,4,5,18,18,5,6};
        
        int counter = 0;
       for(int i = 0 ; i < width ; i++){
           for(int j = 0 ; j < height ; j++){
               b.data[i][j] = arr[counter];
               counter++;
           }
       }
        
        return b;
    }
    public static void divideImage(int width , int height , Block b){//take the image and divide into blocks and store them
        
        int counter1 = 0;
        int counter2 = 0;
        for(int i = 0 ; i < b.getWidth() ;i += width){
            for(int j = 0 ; j < b.getHeight() ; j += height){
                Block blockTmp = new Block();
                blockTmp.setHeight(height);
                blockTmp.setWidth(width);
                blockTmp.data = new float[width][height];
                for(int m = i ; m < i + width ; m++){
                    for(int n = j ; n < j + height ; n++){
                        blockTmp.data[counter1][counter2] = b.data[m][n];
                        counter2++;
                    }
                    counter1++;
                    counter2 = 0;
                }
                counter1 = 0;
                counter2 = 0;
                
                finalBlocks.add(blockTmp);
            }
            
        }
        
    }
    
    
    public static void constructImage(int width , int height){//take the image and divide into blocks and store them
        
        int counter1 = 0;
        int counter2 = 0;
        int k = 0;
        
        for(int i = 0 ; i < returnedImage.getWidth() ;i += width){
            for(int j = 0 ; j < returnedImage.getHeight() ; j += height ){
                
                    for(int m = i ; m < i + width ; m++){
                        for(int n = j ; n < j + height ; n++){
                            returnedImage.data[m][n] = decompressd.get(k).data[counter1][counter2];
                            counter2++;
                        }
                    counter1++;
                    counter2 = 0;
                }
                
                counter1 = 0;
                counter2 = 0;
                k++;
            }
        }
    }
    public static Block getAverageBlock(Vector<Block> blocks){
        int width = blocks.get(0).getWidth();
        int height = blocks.get(0).getHeight();
        
        Block average = new Block();
        average.setHeight(height);
        average.setWidth(width);
        average.data = new float[width][height];
       
        for(int i = 0 ; i < blocks.size() ; i++){
            for(int j = 0 ; j < width ; j++){
                for(int k = 0 ; k < height ; k++){
                    if(i == 0)
                        average.data[j][k] = blocks.get(i).data[j][k] / blocks.size();
                    else
                        average.data[j][k] += blocks.get(i).data[j][k] / blocks.size();
                }
            }
        }
        return average;
    }
    
    public static Vector<Block> split(Vector<Block> averages){
        Vector<Block> splitings  = new Vector<>();
        for(int k = 0 ; k < averages.size() ; k++){
            Block floor = new Block();
            Block ceil = new Block();

            floor.setWidth(averages.get(k).getWidth());
            floor.setHeight(averages.get(k).getHeight());
            floor.data = new float[averages.get(k).getWidth()][averages.get(k).getHeight()];

            ceil.setWidth(averages.get(k).getWidth());
            ceil.setHeight(averages.get(k).getHeight());
            ceil.data = new float[averages.get(k).getWidth()][averages.get(k).getHeight()];

            for(int i = 0 ; i < averages.get(k).getWidth() ; i++){
                for(int j = 0  ; j < averages.get(k).getHeight() ; j++){
                    if(averages.get(k).data[i][j] == (float)Math.floor(averages.get(k).data[i][j])){
                        floor.data[i][j] = averages.get(k).data[i][j] - 1 ;
                        ceil.data[i][j] = averages.get(k).data[i][j] + 1;
                    }else{
                        floor.data[i][j] = (float)Math.floor(averages.get(k).data[i][j]);
                        ceil.data[i][j] = (float)Math.ceil(averages.get(k).data[i][j]);
                    }
                }
            }
            
            splitings.add(floor);
            splitings.add(ceil);
        }
        return splitings;
    }
    
    public static boolean equal(Block b1 , Block b2){
        boolean equal = true;
        
        for(int i = 0 ; i < b1.getWidth() ; i++){
            for(int j = 0 ; j < b1.getHeight() ; j++){
                if(b1.data[i][j] != b2.data[i][j]){
                    equal = false;
                    break;
                }
            }
            
        }
        return equal;
    }
    public static boolean check(Block b ,Vector<Vector<Block>> groups){
        boolean found = false;
        int check = 0;
        for(int i = 0  ; i < groups.size() ; i++){
            for(int j = 0 ; j < groups.get(i).size() ; j++){
                if(equal(b, groups.get(i).get(j))){
                 found = true;   
                }
            }
        }
        return found;
    }
    public static Vector<Vector<Block>> getNearest(Vector<Block> myAverages){//find the best match for each block
        Vector<Vector<Block>> groups = new Vector<Vector<Block>>();
        int width = finalBlocks.get(0).getWidth();
        int height = finalBlocks.get(0).getHeight();
        int differ = 0;
        Vector<Vector<Integer>> differs = new Vector<Vector<Integer>>();
        
        
        for(int i = 0 ; i < myAverages.size() ; i++){
            Vector<Block> tmp = new Vector<>();
            groups.add(tmp);
        }
        
        for(int m = 0 ; m < myAverages.size() ; m++){
            Vector<Integer> tmp = new Vector<>();
            for(int i = 0 ; i < finalBlocks.size() ; i++){
                for(int j = 0 ; j < width ; j++){
                    for(int k = 0 ; k < height ; k++){
                        differ += Math.abs(finalBlocks.get(i).data[j][k] - myAverages.get(m).data[j][k]);
                    }
                }
                tmp.add(differ);
                differ = 0;
            }
            differs.add(tmp);
        }
        
        for(int i = 0 ; i < differs.size() ; i++){
            for(int j = 0 ;j < differs.get(0).size() ; j++){
                int min = differs.get(i).get(j);
                int indx = i;
                for(int k = 0 ; k < differs.size() ; k++){
                    if(differs.get(k).get(j) < min){
                        min = differs.get(k).get(j);
                        indx = k;
                    }
                }
                
                for(int l = 0 ; l < groups.size() ; l++){
                    if(l == indx){
                        if(!check(finalBlocks.get(j), groups))
                            groups.get(indx).add(finalBlocks.get(j));
                        break;
                    }
                }
            }
        }
        
        return groups;
    }
    public static void setCodes(int codebook,Vector<Block> myAverages){

        int numberOfBits = (int)(Math.log(codebook - 1) /  Math.log(2) + 1);
        
        for(int i = 0; i < codebook; i++)
        {
            String str  = Integer.toBinaryString(i);

            String s = "";
            if(str.length()<numberOfBits)
            {
                for(int j = 0; j < numberOfBits-str.length(); j++)
                    s+="0";
            }
            str = s + Integer.toBinaryString(i);

            myAverages.get(i).setCode(str);

        }

    }
    public static void compress() throws IOException{
        readImage("B&W.jpeg");
        
        int height , width ;
        System.out.println("enter the size and width you want");
        System.out.print("height : ");
        height = input.nextInt();
        System.out.print("\nwidth : ");
        width = input.nextInt();
        System.out.println("enter the code book");
        int codebook = input.nextInt();
        
        //Block b = getBlock();
        
        divideImage(width, height,Image);
        //divideImage(width, height,b);
        
        
        Vector<Vector<Block>> groups = new Vector<Vector<Block>>();
        Block average = new Block();
        Vector<Block> splittings = new Vector<>();
                
        
        int counter = 0 ;
        while(myAverages.size() < codebook){
            if(counter == 0){
                System.out.println("point 0");
                
                average = getAverageBlock(finalBlocks);
                myAverages.add(average);
                
                System.out.println("point 1");
                
                splittings = split(myAverages);
                
                System.out.println("point 2");
                
                groups = getNearest(splittings);
                
                System.out.println("----------first-----------");
                counter++;
            }
            else{
                System.out.println("----------second-----------");
                myAverages.clear();
                splittings.clear();
                
                System.out.println("point 3");
                
                for(int i = 0  ; i < groups.size() ; i++){
                    average = getAverageBlock(groups.get(i));
                    myAverages.add(average);
                }
                
                System.out.println("point 4");
                
                if(myAverages.size() < codebook){
                    System.out.println("point 5");
                    splittings = split(myAverages);
                    System.out.println("point 6");
                    
                }
                System.out.println("point 7");
                groups = getNearest(splittings);
                System.out.println("point 8");
            }
        }
        System.out.println("point 9");
        for(int i  = 0 ; i < 2 ; i++){
            System.out.println("point 10");
            
            groups = getNearest(myAverages);
            
            System.out.println("point 11");
            
            myAverages.clear();
            
            for(int j = 0  ; j < groups.size() ; j++){
                average = getAverageBlock(groups.get(j));
                myAverages.add(average);
            }
            
        }
        
        setCodes(codebook,myAverages);
        
        System.out.println("////////////my averages //////////////////");
        printManyBlocks(myAverages);
        System.out.println("////////////my averages //////////////////");
        
        for(int i = 0 ; i < groups.size() ; i++){
            for(int j = 0 ; j < groups.get(i).size() ; j++){
                groups.get(i).get(j).setCode(myAverages.get(i).getCode());
            }
        }
        
       
        
    }
    public static void Decompress(){
        for(int i = 0 ; i < finalBlocks.size() ; i++){
            Block tmp = new Block();
            decompressd.add(tmp);
        }
        for(int i = 0  ; i < finalBlocks.size() ; i++){            
            decompressd.get(i).setWidth(finalBlocks.get(i).getWidth());
            decompressd.get(i).setHeight(finalBlocks.get(i).getHeight());
            decompressd.get(i).setCode(finalBlocks.get(i).getCode());
            decompressd.get(i).data = new float[decompressd.get(i).getWidth()][decompressd.get(i).getHeight()];
        }
        
        for(int i = 0 ; i < decompressd.size() ; i++){
            for(int j = 0 ; j < myAverages.size() ; j++){
                if(decompressd.get(i).getCode().equals(myAverages.get(j).getCode())){
                    for(int k = 0 ; k < myAverages.get(j).getWidth() ; k++){
                        for(int m = 0  ; m < myAverages.get(j).getHeight() ; m++){
                            decompressd.get(i).data[k][m] = (int)myAverages.get(j).data[k][m];
                        }
                    }
                   break;
                }
            }
        }
        System.out.println("enter the width of the new image : ");
        int width = input.nextInt();
        System.out.println("enter the height of the new image : ");
        int height = input.nextInt();
        returnedImage.setWidth(width);
        returnedImage.setHeight(height);
        returnedImage.data = new float[returnedImage.getWidth()][returnedImage.getHeight()];
        
        constructImage(finalBlocks.get(0).getWidth(), finalBlocks.get(0).getHeight());
        
        
        writeImage();
        printBlock(returnedImage.getWidth(), returnedImage.getHeight(), returnedImage);
    }
    public static void main(String[] args) throws IOException {
        compress();
        Decompress();
    }
    
}
