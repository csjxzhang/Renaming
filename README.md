An Accurate Identifier Renaming Prediction and Suggestion Approach.

Brief Introduction:

Identifiers play an important role in helping developers analyze and comprehend source code. However, there exist many identifiers that are inconsistent with the corresponding code conventions or semantic functions, leading to flawed identifiers. Hence, identifiers need to be regularly renamed. Even though researchers have proposed several approaches to identify identifiers needing renaming and further suggest correct identifiers for them, these approaches only focus on a single or a limited number of granularities of identifiers without universally considering all the granularities and suggest a series of sub-tokens for composing identifiers without completely generating new identifiers. In this paper, we propose a novel identifier renaming prediction and suggestion approach. Specifically, given a set of training source code, we first extract all the identifiers in multiple granularities. Then, we design and extract five groups of features from identifiers to capture inherent properties of identifiers themselves, the relationships between identifiers and code conventions as well as other related code entities, enclosing files, and change history. By parsing the change history of identifiers, we can figure out whether specific identifiers have been renamed or not. These identifier features as well as their renaming history are used to train a Random Forest classifier, which can be further used to predict whether a given new identifier needs to be renamed or not. Subsequently, for the identifiers that need renaming, we extract all the related code entities and their renaming change history. Based on the intuition that identifiers are co-evolved with their relevant code entities with similar patterns and renaming sequences, we could suggest and recommend a series of new identifiers for those identifiers. We conduct extensive experiments to validate our approach in both Java projects and Android projects. Experimental results demonstrate that our approach could identify identifiers that need renaming with an average F-measure of more than 89%, which outperforms the state-of-the-art approach by 8.30% in Java projects and 21.38% in Android projects. In addition, our approach achieves the Hit@10 of 48.58% and 40.97% in Java and Android projects in suggesting correct identifiers and outperforms the state-of-the-art approach by 29.62% and 15.75% respectively.

Description of the replication package:

We provide the name of the experimental projects and the source code of the proposed approach. 

The src/DS folder contains the defined data structure of the proposed approach. 

The src/classifierRun folder contains the prediction and suggestion process of the proposed approach. 

The src/feature folder contains the feature extraction process of the proposed approach. 

The src/InfoStatistics folder contains the characteristic information of the experimental projects. 

The excel file contains the list of the experimental projects.
