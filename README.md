# Database-Controller

Project 2 - Tarnowska


Compilation instructions:
    Java 17 JavaFX compile is broken. You must install IntelliJ to run this project. It does work, but we cannot export it into a JAR file 
    The full source is here for you to view
    The program can connect to any jdbc database, and take any query that you would normally write in mySQL.

We have one view called public_info, which displays the Fist and Last name, along with their position inside the company. This view is for public and does not display any personal data such as Phone #, Email, or ID.

We have one procedure called risky_user_alert, which checks the incident table for 10 < failed alerts for any particular user. 

We have one function called Failed Events analysis, which checks event table and evaluates the number of failed events within a specified date range, and gives risk ranking: low, medium or high. 

#
