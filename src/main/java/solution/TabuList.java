package solution;


import java.util.ArrayList;
import java.util.Random;

public class TabuList {
    private int tabuList[][];


    public void decreaseTabu(){
        for (int i = 0; i < tabuList[0].length; i++) {
            for (int j = 0; j < tabuList[0].length; j++) {
                tabuList[i][j] = tabuList[i][j] > 0 ? --tabuList[i][j] : tabuList[i][j];
            }
        }
    }

    public void setupDelays(ArrayList<Vertex> routeFrom, ArrayList<Vertex> routeTo, int swapA, int swapB, int horizon){
        Random tbRandomChanger = new Random();
        getTabuList()[routeFrom.get(swapA - 1).getId()][routeFrom.get(swapA).getId()] = horizon + tbRandomChanger.nextInt(5);
        getTabuList()[routeFrom.get(swapA).getId()][routeFrom.get(swapA + 1).getId()] = horizon + tbRandomChanger.nextInt(5);
        getTabuList()[routeTo.get(swapB).getId()][routeTo.get(swapB + 1).getId()] = horizon + tbRandomChanger.nextInt(5);
    }

    public boolean isInTabu(int i, int j){
        //Check if the move is a Tabu! - If it is Tabu break
        return tabuList[i][j] != 0;
    }

    public int[][] getTabuList() {
        return tabuList;
    }

    public void setTabuList(int[][] tabuList) {
        this.tabuList = tabuList;
    }
}
