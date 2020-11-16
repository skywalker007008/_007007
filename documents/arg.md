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

