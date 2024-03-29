package algorithms;


import environment.Vertex;

import java.util.ArrayList;
import java.util.Random;

public class TabuList {
    private int tabuList[][];


    /**
     * Decrease novel list on each iteration
     */
    public void decreaseTabu(){
        for (int i = 0; i < tabuList[0].length; i++) {
            for (int j = 0; j < tabuList[0].length; j++) {
                tabuList[i][j] = tabuList[i][j] > 0 ? --tabuList[i][j] : tabuList[i][j];
            }
        }
    }

    /**
     *  Add edges into tabu-list in case of "swap" strategy
     */
    public void setupSwapDelays(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int swapA, int swapB, int horizon, int distance){

        Random tbBoundChanger = new Random();

        getTabuList()[routeFrom.get(swapA - 1).getId()][routeFrom.get(swapA).getId()] = horizon + tbBoundChanger.nextInt(distance);
        getTabuList()[routeFrom.get(swapA).getId()][routeFrom.get(swapA + 1).getId()] = horizon + tbBoundChanger.nextInt(distance);

        getTabuList()[routeTo.get(swapB - 1).getId()][routeTo.get(swapB).getId()] = horizon + tbBoundChanger.nextInt(distance);
        getTabuList()[routeTo.get(swapB).getId()][routeTo.get(swapB + 1).getId()] = horizon + tbBoundChanger.nextInt(distance);
    }


    /**
     * Add edges into tabu-list in case of "relocation" strategy
     */
    public void setupRelocationDelays(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int swapA, int swapB, int horizon, int distance){
        Random tbBoundChanger = new Random();
        getTabuList()[routeFrom.get(swapA - 1).getId()][routeFrom.get(swapA).getId()] = horizon + tbBoundChanger.nextInt(distance);
        getTabuList()[routeFrom.get(swapA).getId()][routeFrom.get(swapA + 1).getId()] = horizon + tbBoundChanger.nextInt(distance);
        getTabuList()[routeTo.get(swapB).getId()][routeTo.get(swapB + 1).getId()] = horizon + tbBoundChanger.nextInt(distance);
    }

    /**
     * Check if vertex is in tabu-list
     */
    public boolean isInTabu(int i, int j){
        return tabuList[i][j] != 0;
    }

    public int[][] getTabuList() {
        return tabuList;
    }

    public void setTabuList(int[][] tabuList) {
        this.tabuList = tabuList;
    }
}
