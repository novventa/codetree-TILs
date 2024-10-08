import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

public class Main {
    static final int MAX_SIZE = 70;
    static int r,c,k;
    static int[][] map = new int[MAX_SIZE + 3][MAX_SIZE];
    static int[] dy = {-1,0,1,0};
    static int[] dx = {0,1,0,-1};
    static boolean[][] isExit = new boolean[MAX_SIZE + 3][MAX_SIZE];
    static int ans = 0;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        r = sc.nextInt();
        c = sc.nextInt();
        k = sc.nextInt();

        for(int idx = 1; idx <= k; idx++) {
            int x = sc.nextInt() - 1;
            int d = sc.nextInt();
            down(0,x,d,idx);
       }

        System.out.println(ans);

    }

    static void down(int y,int x,int d,int idx) {
        if(canGo(y+1,x)){
            down(y+1,x,d,idx);
        } else if (canGo(y+1,x-1)) {
            down(y+1,x-1,(d+3)%4,idx);
        } else if (canGo(y+1,x+1)) {
            down(y+1,x+1,(d+1)%4,idx);
        } else {
            if(!inRange(y-1,x-1) || !inRange(y+1,x+1))
                resetMap();
            else {
                map[y][x] = idx;
                for(int k = 0; k < 4; k++) {
                    map[y + dy[k]][x + dx[k]] = idx;
                }
                isExit[y+dy[d]][x+dx[d]] = true;
                ans += bfs(y,x) - 3 + 1;
            }
        }
    }

    static int bfs(int y,int x) {
        int result = y;
        Queue<int[]> q = new LinkedList<>();
        boolean[][] visited = new boolean[MAX_SIZE+3][MAX_SIZE];
        q.offer(new int[]{y,x});
        visited[y][x] = true;
        while(!q.isEmpty()) {
            int[] cur = q.poll();
            for(int k = 0; k < 4; k++) {
                int ny = cur[0] + dy[k];
                int nx = cur[1] + dx[k];

                if(inRange(ny,nx) && !visited[ny][nx] && (map[ny][nx] == map[cur[0]][cur[1]] || (map[ny][nx] != 0 && isExit[cur[0]][cur[1]]))) {
                    q.offer(new int[]{ny,nx});
                    visited[ny][nx] = true;
                    result = Math.max(result,ny);
                }
            }
        }
        return result;
    }

    static boolean canGo(int y,int x) {
        boolean flag = 0 <= x - 1 && x + 1 < c && y + 1 < r + 3;
        flag = flag && (map[y-1][x-1] == 0);
        flag = flag && (map[y-1][x] == 0);
        flag = flag && (map[y-1][x+1] == 0);
        flag = flag && (map[y][x-1] == 0);
        flag = flag && (map[y][x] == 0);
        flag = flag && (map[y][x+1] == 0);
        flag = flag && (map[y+1][x] == 0);
        return flag;
    }

    static void resetMap() {
        for(int i=0;i<r+3;i++) {
            for(int j=0;j<c;j++) {
                map[i][j] = 0;
                isExit[i][j] = false;
            }
        }
    }

    static boolean inRange(int y,int x) {
        return 3 <= y && y < r + 3 && 0 <= x && x < c;
    }

}