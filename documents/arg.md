# K-Means Arg

## Model-0

### Datas

+ Ratio of different warn error: 			COEF_WARN
+ Ratio of different board error: 			COEF_BOARD
+ Ratio of different warn-board error: 		COEF_PAIR
+ Average level gap :						COEF_LEVEL

+ Distance Type: Eurcid

### Results

+ arg_type1:
	- ARGS:
		+ COEF_WARN: 	1.9
		+ COEF_BOARD:	0.5
		+ COEF_PAIR:	1.0
		+ COEF_LEVEL:	0.1
	- RESULTS:
		+ able to group relevant into a group
		+ unable to split out something supposed not in this group
		+ Summary: TP 100, FP high

+ arg_type2:
	- ARGS:
		+ COEF_WARN: 	1.9
		+ COEF_BOARD:	0.5
		+ COEF_PAIR:	1.0
		+ COEF_LEVEL:	0.01
	- RESULTS:
		+ able to group relevant into a group
		+ unable to split out something supposed not in this group, but ratio down, especially for MUT_LOS group, ratio is 0
		+ Summary: TP 100, FP exists, but for certain groups it equals 0 

> **Result 1: COEF_LEVEL affects the FP** 

## Model 1: different values related to Model 0

> **Result 2: The model of arg is not good**

## Model 2: new vector message

+ Ratio of different warn error: 	COEF_WARN
+ Type of its length:				COEF_MANY_TYPE

+ RESULTS:
	+ Somehow available to exclude those seemingly unsuccessful
	+ Warnings related to MUT_LOS shows another types of similar errors with length above 4

> **Result 3: Judge from length is somehow useful, but not completely successful**

## Model 3: new method to initialize the cluster.

+ Idea:
	- According to each type of warnings, set each ratio of 1, level as 0, as the first initialized coordinate.
	- Set new vector of only one port and only two port.

+ RESULTS:

## Model 4: 
 
+ Idea:
	- Together using the rule-finding methods and the k-means methods
	- First, cluster the length of 1, and then according to the length and the belief of it to decide whether it will defined as a cluster.
	- Later, use both ideas to decides the length of 2, and made 2 clusters
	- Later, when deciding clusters, first remove some existed cluster pairs out, and then, make new clusters.
