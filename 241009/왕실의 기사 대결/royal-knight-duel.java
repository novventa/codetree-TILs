import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class Knight {
    int r, c;        // 현재 위치 (좌상단 좌표)
    int h, w;        // 크기 (높이, 너비)
    int k;           // 현재 체력
    int bef_k;       // 초기 체력 (피해 계산을 위해 저장)

    int nr, nc;      // 이동 후 임시 위치
    int dmg;         // 이동 중 받은 피해
    boolean isMoved; // 이번 이동에서 이동했는지 여부

    public Knight(int r, int c, int h, int w, int k) {
        this.r = r;
        this.c = c;
        this.h = h;
        this.w = w;
        this.k = k;
        this.bef_k = k;

        this.nr = r;
        this.nc = c;
        this.dmg = 0;
        this.isMoved = false;
    }
}

public class Main {
    public static final int MAX_N = 31;
    public static final int MAX_L = 41;

    public static int l, n, q;
    public static int[][] info = new int[MAX_L][MAX_L];
    public static Knight[] knights = new Knight[MAX_N]; // 1-based index 사용

    public static int[] dx = {-1, 0, 1, 0}; // 위, 오른쪽, 아래, 왼쪽 방향
    public static int[] dy = {0, 1, 0, -1};

    // 움직임을 시도해봅니다.
    public static boolean tryMovement(int idx, int dir) {
        Queue<Integer> queue = new LinkedList<>();

        // 초기화 작업입니다.
        for (int i = 1; i <= n; i++) {
            knights[i].dmg = 0;
            knights[i].isMoved = false;
            knights[i].nr = knights[i].r;
            knights[i].nc = knights[i].c;
        }

        queue.add(idx);
        knights[idx].isMoved = true;

        while (!queue.isEmpty()) {
            int x = queue.poll();
            Knight knight = knights[x];

            knight.nr += dx[dir];
            knight.nc += dy[dir];

            // 경계를 벗어나는지 체크합니다.
            if (knight.nr < 1 || knight.nc < 1 || knight.nr + knight.h - 1 > l || knight.nc + knight.w - 1 > l)
                return false;

            // 대상 기사가 다른 기사나 장애물과 충돌하는지 검사합니다.
            for (int i = knight.nr; i <= knight.nr + knight.h - 1; i++) {
                for (int j = knight.nc; j <= knight.nc + knight.w - 1; j++) {
                    if (info[i][j] == 1)
                        knight.dmg++;
                    if (info[i][j] == 2)
                        return false;
                }
            }

            // 다른 기사와 충돌하는 경우, 해당 기사도 같이 이동합니다.
            for (int i = 1; i <= n; i++) {
                if (knights[i].isMoved || knights[i].k <= 0)
                    continue;

                Knight other = knights[i];

                if (other.r > knight.nr + knight.h - 1 || knight.nr > other.r + other.h - 1)
                    continue;
                if (other.c > knight.nc + knight.w - 1 || knight.nc > other.c + other.w - 1)
                    continue;

                knights[i].isMoved = true;
                queue.add(i);
            }
        }

        knights[idx].dmg = 0; // 명령을 받은 기사는 피해를 받지 않습니다.
        return true;
    }

    // 특정 기사를 지정된 방향으로 이동시키는 함수입니다.
    public static void movePiece(int idx, int dir) {
        if (knights[idx].k <= 0)
            return;

        // 이동이 가능한 경우, 실제 위치와 체력을 업데이트합니다.
        if (tryMovement(idx, dir)) {
            for (int i = 1; i <= n; i++) {
                if (knights[i].isMoved) {
                    knights[i].r = knights[i].nr;
                    knights[i].c = knights[i].nc;
                    knights[i].k -= knights[i].dmg;
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // 입력값을 받습니다.
        l = sc.nextInt();
        n = sc.nextInt();
        q = sc.nextInt();

        for (int i = 1; i <= l; i++)
            for (int j = 1; j <= l; j++)
                info[i][j] = sc.nextInt();

        // 기사 정보 입력 및 객체 생성
        for (int i = 1; i <= n; i++) {
            int r = sc.nextInt();
            int c = sc.nextInt();
            int h = sc.nextInt();
            int w = sc.nextInt();
            int k = sc.nextInt();
            knights[i] = new Knight(r, c, h, w, k);
        }

        // 명령 처리
        for (int i = 1; i <= q; i++) {
            int idx = sc.nextInt();
            int dir = sc.nextInt();
            movePiece(idx, dir);
        }

        // 결과를 계산하고 출력합니다.
        long ans = 0;
        for (int i = 1; i <= n; i++) {
            if (knights[i].k > 0) {
                ans += knights[i].bef_k - knights[i].k;
            }
        }

        System.out.println(ans);
    }
}