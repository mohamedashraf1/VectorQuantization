/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vectorquantization;

import java.util.Vector;

/**
 *
 * @author Elliot
 */
public class Block {
    float [][] data;
    int height;
    int width;
    String code;
    
    public Block() {
        height = 0;
        width = 0;
        data = new float[width][height];
        code = "";
    }
    /*average = getAverageBlock(finalBlocks);
                myAverages.add(average);
                splittings = split(myAverages);
                groups = getNearest(splittings);
                
        for(int i = 0 ; i < groups.size() ;i ++){
            printManyBlocks(groups.get(i));
            System.out.println("*******************************************");
        }*/
    public Block(float[][] data, int height, int width , String code) {
        //this.data = data;
        for(int i = 0 ; i < width ; i++){
            for(int j  = 0 ; j < height ; j++){
                this.data[i][j] = data[i][j];
            }
        }
        this.height = height;
        this.width = width;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    
}
