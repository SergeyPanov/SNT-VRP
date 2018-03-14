package tabu;

public class TabuList {
    private int tabuList[][];


    public void decreaseTabu(){
        for (int i = 0; i < tabuList[0].length; i++) {
            for (int j = 0; j < tabuList[0].length; j++) {

                tabuList[i][j] = tabuList[i][j] > 0 ? --tabuList[i][j] : tabuList[i][j];

            }
        }
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
