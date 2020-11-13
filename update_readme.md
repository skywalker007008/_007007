# UPDATE_README

## 1027 version

+ First init

## 1028-1 version

### New function

+ Support same port with different warning type
+ Support frequent warnings related to related warnings

### Mostly change file

+ TorpoData.java
+ Analysis.java

### Find bugs

+ ErrorInfo 263: 5 minute count failed
+ Torpo still remains problem when facing the 1009-2-6-12OLP-3

## 1028-2 version

### New function

+ Make the output of the related warnings look better
+ Fix bugs of 5-minute count

## 1029-1 version

### Fix bugs

+ Fix bugs of torpo-display in excels
+ Fix bugs of port-info read error, newly supporting some exceptional statement

## 1030-1 version

### New function

+ Half structure built for the WarnChain Class
+ Reunit the order of warnings by level

## 1101-1 version

### New function

+ Delete repeated group warnings
+ Count the result of related rules, according to support and belief level

## 1104-1 version

### Modified Contents

+ Have to re-construct the TorpoDevice and TorpoRoute
+ New ways to handle the torpo-vision
+ New Branch of it

### What's next?

+ Finish the torpo re-construction
+ Enable to group the warning_lists and make different kinds of list

## 1104-2 version

### Finish Parts

+ Finish the reconstruct of torpo_device
+ For torpoDevice, only Visual Left

### What's next?

+ Modify the warnings_read
+ Enable to group the warning_lists and make different kinds of list

## 1105-1 version

### Finish Parts

+ still 1104-2 version, but fix some torpo_bug

### What's next?

+ Build hidden level, for grouping
+ fix some little bugs

## 1105-2 version

### What's new?

+ Add line info in the torpo

## 1109: Clone to windows
## 1110: Re-design the algorithms

## 1112-1 version

### What's new?

+ Rebuild some basic structure of clusters-finding
+ Build some basic reference library
+ Leave some core parts to be implemented

### What's next?

+ Finish GraphNode.ReadGroupData()
+ Finish K-means.Cluster()
+ Repeated testing, until the result seems good.

## 1113-1 version

### What's new?

+ Almost finish the process of k_means with only one test

### What's next?

+ Finish Cluster.Update()
+ Finish K-means.ChooseInitialNodes()
+ Using different values to test the answer of k-means
