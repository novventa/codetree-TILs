import java.util.Scanner;

class Node {
	int x, y;

	public Node(int x, int y) {
		this.x = x;
		this.y = y;
	}
}

public class Main {

	static final int MAX_N = 10;
	static int n, m, k;
	static int[][] maze = new int[MAX_N + 1][MAX_N + 1];
	static int[][] nextMaze = new int[MAX_N + 1][MAX_N + 1];
	static Node[] players = new Node[MAX_N + 1];
	static Node exit;
	static int ans;
	static int sx, sy, squareSize;

	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);

		n = sc.nextInt();
		m = sc.nextInt();
		k = sc.nextInt();

		for (int i = 1; i <= n; i++) {
			for (int j = 1; j <= n; j++) {
				maze[i][j] = sc.nextInt();
			}
		}

		for (int i = 1; i <= m; i++) {
			int x = sc.nextInt();
			int y = sc.nextInt();
			players[i] = new Node(x, y);
		}

		int x = sc.nextInt();
		int y = sc.nextInt();

		exit = new Node(x, y);

		while (k-- > 0) {
			moveAll();

			boolean isAllEscaped = true;

			for (int i = 1; i <= m; i++) {
				if (!(players[i].x == exit.x && players[i].y == exit.y)) {
					isAllEscaped = false;
				}
			}

			if (isAllEscaped)
				break;

			findMinSquare();
			rotateSquare();
			rotatePlayerAndExit();
		}

		System.out.println(ans);
		System.out.println(exit.x + " " + exit.y);

	}

	static void moveAll() {
		for (int i = 1; i <= m; i++) {
			if (players[i].x == exit.x && players[i].y == exit.y)
				continue;

			// 출구와 행이 다른 경우 행을 이동시켜본다.
			if (players[i].x != exit.x) {
				int nx = players[i].x;
				int ny = players[i].y;

				if (exit.x > nx)
					nx++;
				else
					nx--;

				// 빈칸이라면 행을 이동
				if (maze[nx][ny] == 0) {
					players[i].x = nx;
					players[i].y = ny;
					ans++;
					continue;
				}
			}

			// 출구와 열이 다른 경우 열을 이동시켜본다.
			if (players[i].y != exit.y) {
				int nx = players[i].x;
				int ny = players[i].y;

				if (exit.y > ny)
					ny++;
				else
					ny--;

				// 벽이 없다면 열을 이동
				if (maze[nx][ny] == 0) {
					players[i].x = nx;
					players[i].y = ny;
					ans++;
					continue;
				}
			}
		}
	}

	static void findMinSquare() {
		// 가장 작은 정사각형부터 만들면서 완전탐색
		for (int size = 2; size <= n; size++) {
			// 좌상단 r부터
			for (int x1 = 1; x1 <= n - size + 1; x1++) {
				// 좌상단 c부터
				for (int y1 = 1; y1 <= n - size + 1; y1++) {
					int x2 = x1 + size - 1;
					int y2 = y1 + size - 1;

					// 만약 정사각형 안에 출구가 없다면 스킵
					if (!(x1 <= exit.x && exit.x <= x2 && y1 <= exit.y && exit.y <= y2))
						continue;

					// 이번엔 참가자가 정사각형 안에 있는지 확인
					boolean isPlayerIn = false;
					for (int playerNum = 1; playerNum <= m; playerNum++) {
						if (x1 <= players[playerNum].x && players[playerNum].x <= x2 && y1 <= players[playerNum].y
								&& players[playerNum].y <= y2) {
							// 출구에 이미 도착한 사람은 제외
							if (!(players[playerNum].x == exit.x && players[playerNum].y == exit.y))
								isPlayerIn = true;
						}
					}

					// 한명 이상의 사람이 정사각형 안에 있다면
					// 최소 정사각형의 정보를 저장
					if (isPlayerIn) {
						sx = x1;
						sy = y1;
						squareSize = size;

						return;
					}
				}
			}
		}
	}

	static void rotateSquare() {
		// 저장되어있는 최소 정사각형의 정보를 바탕으로 그 안에 있는 벽들의 내구도를 1 감소
		for (int x = sx; x < sx + squareSize; x++) {
			for (int y = sy; y < sy + squareSize; y++) {
				if (maze[x][y] > 0)
					maze[x][y]--;
			}
		}

		// 가장 작은 정사각형 시계방향 90도 회전
		for (int x = sx; x < sx + squareSize; x++) {
			for (int y = sy; y < sy + squareSize; y++) {
				// sx,sy를 0,0으로 옮긴다.
				int ox = x - sx;
				int oy = y - sy;
				// 이 상태에서 회전 이후의 좌표가 (x, y) -> (y, squareSize-x-1)이 된다.
				// 180도 회전은 (x,y) -> (squareSize-x-1,squareSize-y-1);
				// 270도 회전은 (x,y) -> (squareSize-y-1,x)
				int rx = oy;
				int ry = squareSize - ox - 1;
				// 다시 sx,sy를 더한다.
				nextMaze[rx + sx][ry + sy] = maze[x][y];
			}
		}

		// 회전한 정사각형의 좌표를 원래 미로 자료구조에 옮긴다
		for (int x = sx; x < sx + squareSize; x++) {
			for (int y = sy; y < sy + squareSize; y++) {
				maze[x][y] = nextMaze[x][y];
			}
		}
	}

	static void rotatePlayerAndExit() {
		for (int i = 1; i <= m; i++) {
			// m명의 참가자들 모두 확인
			int x = players[i].x;
			int y = players[i].y;
			// 현재 참가자가 정사각형 안에 있을때에만 회전
			if (sx <= x && x < sx + squareSize && sy <= y && y < sy + squareSize) {
				// Step 1. (sx, sy)를 (0, 0)으로 옮겨주는 변환을 진행합니다.
				int ox = x - sx;
				int oy = y - sy;
				// Step 2. 변환된 상태에서는 회전 이후의 좌표가 (x, y) -> (y, squareN - x - 1)가 됩니다.
				int rx = oy;
				int ry = squareSize - ox - 1;
				// Step 3. 다시 (sx, sy)를 더해줍니다.
				players[i].x = rx + sx;
				players[i].y = ry + sy;
			}
		}

		// 출구 회전
		int x = exit.x;
		int y = exit.y;
		if (sx <= x && x < sx + squareSize && sy <= y && y < sy + squareSize) {
			// sx,sy를 0,0으로 옮긴다.
			int ox = x - sx;
			int oy = y - sy;
			// 이 상태에서 회전 이후의 좌표가 (x, y) -> (y, squareSize-x-1)이 된다.
			// 180도 회전은 (x,y) -> (squareSize-x-1,squareSize-y-1);
			// 270도 회전은 (x,y) -> (squareSize-y-1,x)
			int rx = oy;
			int ry = squareSize - ox - 1;
			// 다시 sx,sy를 더한다.
			exit.x = rx + sx;
			exit.y = ry + sy;
		}
	}
}