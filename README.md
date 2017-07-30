# TDIDT
Top down induction of decision tree(TDIDT) algorithm Java implementation for domains over binary attributes.

- Each training and test example is represented by a binary vector. 

- The input to the algorithm is an ASCII file of the following format:
  * The first line contains the number n of attributes and
  * Each training example is given in a separate line of the form <attribute_1> ... <atrribute_n> \<prediction\>.
 
- Empricial evaluation is conducted with SPECT Heart Data Set from the UCI machine learning repository, which has a binary target attribute for the prediction value and n=22 binary attributes.

