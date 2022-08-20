# bigtable-schema-edd-utility

  This is a sprint boot utility app which manages the Bigtable schema(Create/Update/Delete tables). It reads input from GCS in YAML format (As shown below) and takes the necessary. 
  actions. 
  
 Path YAML Format Ex :  https://github.com/vali7394/bigtable-schema-edd-utility/blob/develop/src/main/resources/bigtable-edd-18-Aug-2022.yml


Used a JIB plug in to generate and push the image to GCR.

JAVA : 17
Gradle Commands :
    Gradle clean build jib 

Set GOOGLE_APPLICATION_CREDENTIALS  env variable. 

Roles Required in GCP :
  . BigTable Admin
  . GCS Reader 
  . GCR Pull and Push
  
We can use this image to run in CloudRun. 

API Relative Path : /schema-update/{file_name}

    
