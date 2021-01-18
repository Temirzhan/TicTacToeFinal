

import java.util.Random;
import java.util.Scanner;

/**
 * Project java_core_android
 *
 * @Author Alexander Grigorev
 * Created 11.01.2021
 * v1.0
 */
public class TicTacToe {
    private static char[][] field;
    private static final char DOT_HUMAN = 'X';
    private static final char DOT_AI = 'O';
    private static final char DOT_EMPTY = '.';
    private static final String RIGHT = "RIGHT"; // константы направления
    private static final String DOWN = "DOWN";
    private static final String RIGHT_DOWN = "RIGHTDOWN";
    private static final String RIGHT_UP = "RIHTUP";
    private static final Scanner SCANNER = new Scanner(System.in);
    private static final Random RANDOM = new Random();
    private static int fieldSizeX;
    private static int fieldSizeY;
    private static int[][] fieldHuman; //память компьютера ходы игрока
    private static int[][] fieldAi; //память компьютера ходы ИИ
    private static int numberToWin; // необходимое колл для Победы
    private static String directionAi; // направления ИИ
    private static int countAi; // колл удачных ходов ИИ


    public static void main(String[] args) {
        while (true) {
            initField(3,3,3); // Инициализация
            printField();
            System.out.println(fieldSizeX/2);
            while (true) {
                humanTurn();
                printField();
                if (checkGame(DOT_HUMAN, "Human wins!!!")) break;
                aiTurn();
                printField();
                if (checkGame(DOT_AI, "AI win!!!")) break;
            }
            System.out.println("Wanna play again?");
            if (!SCANNER.next().equals("y")){
                SCANNER.close();
                break;
            }
        }

    }

    private static void humanTurn() {
        int x;
        int y;

        do {
            System.out.print("Введите координаты хода Х и У от 1 до 5 через пробел ->");
            x = SCANNER.nextInt() - 1;
            y = SCANNER.nextInt() - 1;
        } while (!isCellValid(x, y) || !isCellEmpty(x, y));

        field[y][x] = DOT_HUMAN;
        saveDotXy(y,x,fieldHuman); // сохраняем в память
     }

    private static void aiTurn() {
        int[] yX= {-1,-1};
        int[] YandCount={0,0};
        do {
            YandCount = whoNext(DOT_HUMAN,fieldHuman);// ищем след точку игрока
            if(fieldHuman[1][0]!=-1&&YandCount[1]>countAi){ // исключаем удачных ходов, и проверяем больше ли у игрока удачных ходов
                yX = checkDirection(fieldHuman[YandCount[0]][0],fieldHuman[YandCount[0]][1],YandCount[1],DOT_HUMAN); //ищем в каком напр пусто
                removeDot(YandCount[0], fieldHuman);// удаляем из памяти ход
                countAi++;
            }else if(fieldAi[1][0]!=-1){ // ИИ переходит в атаку
                YandCount = whoNext(DOT_AI,fieldAi);
                yX = checkDirection(fieldAi[YandCount[0]][0],fieldAi[YandCount[0]][1],YandCount[1],DOT_AI);//ищем в каком напр пусто
                if(yX[0]==-1)
                removeDot(YandCount[0], fieldAi);//удаляем из памяти ход
                countAi=YandCount[1];
            }else {
                countAi=0;//обнулям удачные ходы
                if(isCellEmpty(fieldSizeX/2,fieldSizeY/2)) {
                    yX[0] = fieldSizeY/2;
                    yX[1] = fieldSizeX/2;
                    countAi=1;
                }else if(fieldAi[0][0]==-1){
                    yX[0] = RANDOM.nextInt(fieldSizeY);
                    yX[1] = RANDOM.nextInt(fieldSizeX);
                }
                else{
                        wearDot(DOT_AI,fieldAi);// ищем полны список ходов ИИ
                }
            }
        }while (!isCellEmpty(yX[1], yX[0]));

        field[yX[0]][yX[1]] = DOT_AI;
        saveDotXy(yX[0],yX[1],fieldAi);
    }


    private static boolean checkGame(char dot, String s) {

        if (checkWin(dot)) {
            System.out.println(s);
            return true;
        }
        if (checkDraw()) {
            System.out.println("Draw!!!");
            return true;
        }
        return false;
    }

    /* private static boolean checkWin(char c) {
         // hor
         for(int i = 0; i < field.length;i++) {
             if (field[i][0] == c && field[i][1] == c && field[i][2] == c) return true;
             if (field[0][i] == c && field[1][i] == c && field[2][i] == c) return true;
         }

         if (field[1][0] == c && field[1][1] == c && field[1][2] == c) return true;
         if (field[2][0] == c && field[2][1] == c && field[2][2] == c) return true;

         // ver
         if (field[0][0] == c && field[1][0] == c && field[2][0] == c) return true;
         if (field[0][1] == c && field[1][1] == c && field[2][1] == c) return true;
         if (field[0][2] == c && field[1][2] == c && field[2][2] == c) return true;

         // dia
         if (field[0][0] == c && field[1][1] == c && field[2][2] == c) return true;
         if (field[0][2] == c && field[1][1] == c && field[2][0] == c) return true;
         return false;
     }*/
    private static boolean checkWin(char c){
        int[][] fieldUser =new int[fieldSizeY*fieldSizeX/2][2];
        fillFieldUser(fieldUser);
        wearDot(c,fieldUser);
       for(int i=0;i<fieldUser.length;i++) {
            if(fieldUser[i][0]!=-1){
               if( numberToWin==countDot(fieldUser[i][0],fieldUser[i][1],c))
               return true;
            }
       }
        return false;
    }
    private static void wearDot(char c, int[][] fieldUser){  // поиск всех точек
        for (int y=0;y<fieldSizeY;y++)
        {
            for (int x=0;x<fieldSizeX;x++){
                if(c == field[y][x])
                    saveDotXy(y,x,fieldUser);
            }
        }
    }
    private static int[] whoNext(char c,int[][] fieldUser){ // поиск максм колл удачных ходов
        int max=0;
        int[] fieldYandCount={0,0};

            for (int i = 0; i < fieldUser.length; i++) {
                if (fieldUser[i][0] != -1) {
                    int count = countDot(fieldUser[i][0], fieldUser[i][1], c);
                    if (max < count) {
                        max = count;
                        fieldYandCount[0] = i;
                    }
                }
            }
            fieldYandCount[1]=max;
        return  fieldYandCount;
    }
    private static int[] checkDirection(int y, int x,int count,char c){ // проверка направления
        int[] yX={-1,-1};
        switch (directionAi){
            case(RIGHT) :
                if (isCellEmpty(x+count,y)){
                    yX[1]=x+count;
                    yX[0]=y;
                }else if(isCellEmpty(x-1,y)&&(count>2||c==DOT_AI)){
                    yX[1]=x-1;
                    yX[0]=y;
                }
                break;
            case(DOWN) :
                if (isCellEmpty(x,y+count)&&y+count<fieldSizeY){
                    yX[1]=x;
                    yX[0]=y+count;
                }else if(isCellEmpty(x,y-1)&&(count>2||c==DOT_AI)){
                    yX[1]=x;
                    yX[0]=y-1;
                }
                break;
            case(RIGHT_DOWN) :
                if (isCellEmpty(x+count,y+count)){
                    yX[1]=x+count;
                    yX[0]=y+count;
                }else if(isCellEmpty(x-1,y-1)&&(count>2||c==DOT_AI)){
                    yX[1]=x-1;
                    yX[0]=y-1;;
                }
                break;
            case(RIGHT_UP) :
                if (isCellEmpty(x+count,y-count)){
                    yX[1]=x+count;
                    yX[0]=y-count;
                }else if(isCellEmpty(x-1,y+1)&&(count>2||c==DOT_AI)){
                    yX[1]=x-1;
                    yX[0]=y+1;
                }
                break;
        }

        return yX;
    }
    private static void removeDot(int y,int[][]userFielld){ // удаление нужного элимента
        for(int i = y;y < userFielld.length-1; y++){
            userFielld[y][0] = userFielld[y+1][0];
            userFielld[y][1] = userFielld[y+1][1];
            userFielld[y+1][0] = -1;
            userFielld[y+1][1] = -1;
        }
    }

    private static int countDot(int y,int x,char c){ //подсчет точек
        int countX = 1;
        int countY = 1;
        int diagonallyDown = 1;
        int diagonallyUp = 1;


        for(int i=y+1; i<fieldSizeX;i++) {
            if(c== field[i][x]){
                countY++;
            }else{
                break;
            }
        }

        for(int i=x+1; i<fieldSizeY;i++) {
            if(c== field[y][i]){
                countX++;
            }else{
                break;
            }
        }
        for(int i=y+1,j=x+1; i<fieldSizeY&&j<fieldSizeX;i++,j++) {
            if(c== field[i][j]){
                diagonallyDown++;
            }else{
                break;
            }
        }

        for(int i=y-1,j=x+1; i>=0&&j<fieldSizeX;i--,j++) {
            if(c== field[i][j]){
                diagonallyUp++;
            }else{
                break;
            }
        }


        if(countY>countX&&countY>diagonallyDown&&countY>diagonallyUp){
            directionAi=DOWN;
            return countY;
        } else if(countX>countY&&countX>diagonallyDown&&countX>diagonallyUp){
            directionAi=RIGHT;
            return countX;
        } else if(diagonallyDown>countX&&diagonallyDown>countY&&diagonallyDown>diagonallyUp){
            directionAi=RIGHT_DOWN;
            return diagonallyDown;
        } else if(diagonallyUp>countX&&diagonallyUp>countY&&diagonallyUp>diagonallyDown){
            directionAi=RIGHT_UP;
            return diagonallyUp;
        }
        return 0;
    }

    private static void saveDotXy(int y,int x,int[][] userField){ // сохраниние координат точек
        for(int i = 0; i < userField.length;i++){
            if(userField[i][0]==-1){
            userField[i][0] = y;
            userField[i][1] = x;
            break;
            }
        }
    }


    private static boolean checkDraw() {
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (isCellEmpty(x, y)) return false;
            }
        }
        return true;
    }

    private static void initField(int y,int x,int win) {
        countAi=0;
        directionAi = RIGHT_UP;
        fieldSizeX = x;
        fieldSizeY = y;
        numberToWin = win;
        field = new char[fieldSizeY][fieldSizeX];
        fieldHuman = new int[(fieldSizeY * fieldSizeX)/2][2];
        fieldAi = new int[(fieldSizeY * fieldSizeX)/2][2];
        for (int i = 0; i < fieldSizeY; i++) {
            for (int j = 0; j < fieldSizeX; j++) {
                field[i][j] = DOT_EMPTY;
            }
        }
        fillFieldUser(fieldHuman);
        fillFieldUser(fieldAi);
    }

    private static void fillFieldUser(int[][] userFielld){ // заполнение массива -1
        for(int y = 0;y < userFielld.length; y++){
            userFielld[y][0] = -1;
        }
    }


    private static void printField() {
        System.out.print("+");
        for (int i = 0; i < fieldSizeX * 2 + 1; i++)
            System.out.print((i % 2 == 0) ? "-" : i / 2 + 1);
        System.out.println();

        for (int i = 0; i < fieldSizeY; i++) {
            System.out.print(i + 1 + "|");
            for (int j = 0; j < fieldSizeX; j++)
                System.out.print(field[i][j] + "|");
            System.out.println();
        }

        for (int i = 0; i <= fieldSizeX * 2 + 1; i++)
            System.out.print("-");
        System.out.println();
    }

    private static boolean isCellEmpty(int x, int y) {
        if(x>-1&&y>-1&&x<fieldSizeX&&y<fieldSizeY)
        return field[y][x] == DOT_EMPTY;

        return false;
    }

    private static boolean isCellValid(int x, int y) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }
}