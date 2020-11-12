+ Node:
	- map(node_num, class)

+ class:
	- next_list
	- level
	- map(end, distance)

+ 進行dijkstra的時候，碰到一個點之後，就直接創建一個對象繼續進行處理。

+ 當A啓動找到了B之後，標注B開始尋找的各個節點